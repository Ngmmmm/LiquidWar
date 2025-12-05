package projet.cpoo5.modele;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import projet.cpoo5.util.GameConfig;
import projet.cpoo5.util.Point;

public class Grille {

    private final int largeur;
    private final int hauteur;

    private final boolean[][] murs;
    private final Particule[][] particules;

    private final GameConfig config;

    public Grille(GameConfig config) {
        this.largeur = config.largeur();
        this.hauteur = config.hauteur();
        this.config = config;
        this.murs = new boolean[largeur][hauteur];
        this.particules = new Particule[largeur][hauteur];

        genererMursAleatoires();
    }

    public int getLargeur() {
        return largeur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public void setParticule(int x, int y, Particule p) {
        if (!inLimites(x, y))
            return;
        particules[x][y] = p;
    }

    public Particule getParticule(int x, int y) {
        if (!inLimites(x, y))
            return null;
        return particules[x][y];
    }

    public boolean inLimites(int x, int y) {
        return x >= 0 && x < largeur && y >= 0 && y < hauteur;
    }

    public boolean isObstacle(int x, int y) {
        if (!inLimites(x, y)) {
            return true;
        }
        return murs[x][y];
    }

    public boolean isParticule(int x, int y) {
        return getParticule(x, y) != null;
    }

    public boolean isEnnemi(Particule p, int x, int y) {
        return isParticule(x, y) && getParticule(x, y).getEquipe() != p.getEquipe();
    }

    public void attaquerParticule(Particule attaquant, int x, int y) {
        Particule cible = getParticule(x, y);
        if (cible == null)
            return;

        float degats = config.degatsAttaque();
        float pris = Math.min(cible.getEnergie(), degats);

        cible.removeEnergie(pris);
        attaquant.addEnergie(pris);

        if (cible.getEnergie() <= config.seuilConversion()) {
            cible.setEquipe(attaquant.getEquipe());

            cible.addEnergie(10); 
            attaquant.removeEnergie(10);
        }
    }

    public void transfererEnergie(Particule donneur, int x, int y) {
        Particule receveur = getParticule(x, y);
        float seuilMax = 100.0f;
        
        if (donneur.getEnergie() < 20 || receveur.getEnergie() >= seuilMax) return;

        float don = config.transfertEnergie();
        
        float qte = Math.min(donneur.getEnergie(), don);
        
        donneur.removeEnergie(qte);
        receveur.addEnergie(qte);
    }

    public void deplacerParticule(Particule p, int nx, int ny) {
        int x = p.getX();
        int y = p.getY();

        if (!inLimites(nx, ny))
            return;
        if (isObstacle(nx, ny))
            return;
        if (getParticule(nx, ny) != null)
            return;

        particules[x][y] = null;
        particules[nx][ny] = p;
        p.deplacer(nx, ny);
    }

    public void setWall(int x, int y, boolean isWall) {
        if (inLimites(x, y)) {
            this.murs[x][y] = isWall;
        }
    }

    /**
     * Génère des murs organiques avec un Automate Cellulaire.
     */
    private void genererMursAleatoires() {
        Random rand = new Random();
        
        // 1. Initialisation aléatoire (Bruit)
        // 45% de chances d'avoir un mur au départ
        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                // On garde les bords vides pour que les particules puissent circuler
                if (x < 5 || x > largeur - 5 || y < 5 || y > hauteur - 5) {
                    murs[x][y] = false;
                } else {
                    murs[x][y] = rand.nextDouble() < 0.57;
                }
            }
        }

        for (int i = 0; i < 10; i++) {
            lisserCarte();
        }
        supprimerZonesIsolees();
    }

    private void lisserCarte() {
        boolean[][] nouveauxMurs = new boolean[largeur][hauteur];

        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                int mursVoisins = compterMursVoisins(x, y);

                if (mursVoisins >= 5) {
                    nouveauxMurs[x][y] = true;
                } else {
                    nouveauxMurs[x][y] = false;
                }
            }
        }
        
        for (int x = 0; x < largeur; x++) {
            System.arraycopy(nouveauxMurs[x], 0, murs[x], 0, hauteur);
        }
    }

    private int compterMursVoisins(int gridX, int gridY) {
        int count = 0;
        for (int neighborX = gridX - 1; neighborX <= gridX + 1; neighborX++) {
            for (int neighborY = gridY - 1; neighborY <= gridY + 1; neighborY++) {
                if (inLimites(neighborX, neighborY)) {
                    if (neighborX != gridX || neighborY != gridY) {
                        if (murs[neighborX][neighborY]) {
                            count++;
                        }
                    }
                } else {
                    count++;
                }
            }
        }
        return count;
    }


    /**
     * Supprime les petites cavernes isolées pour garantir une carte connectée.
     */
    private void supprimerZonesIsolees() {
        boolean[][] accessible = new boolean[largeur][hauteur];
        Queue<Point> file = new LinkedList<>();

        //Trouver un point de départ vide au centre
        int startX = -1, startY = -1;
        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                if (!murs[x][y]) {
                    startX = x;
                    startY = y;
                    break; 
                }
            }
            if (startX != -1) break;
        }

        if (startX == -1) return;

        //marquer tout ce qui est accessible
        file.add(new Point(startX, startY));
        accessible[startX][startY] = true;

        int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};

        while (!file.isEmpty()) {
            Point p = file.poll();
            
            for (int[] dir : directions) {
                int nx = p.x() + dir[0];
                int ny = p.y() + dir[1];

                if (inLimites(nx, ny) && !murs[nx][ny] && !accessible[nx][ny]) {
                    accessible[nx][ny] = true;
                    file.add(new Point(nx, ny));
                }
            }
        }

        //Remplissage des zones inaccessibles
        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                if (!murs[x][y] && !accessible[x][y]) {
                    murs[x][y] = true;
                }
            }
        }
    }

}
