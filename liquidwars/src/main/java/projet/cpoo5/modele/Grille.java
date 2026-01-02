package projet.cpoo5.modele;

import java.util.LinkedList;
import java.util.Queue;

import projet.cpoo5.util.GameConfig;
import projet.cpoo5.util.Point;


/**
 * Représente le plateau de jeu .
 * Gère les particules, les obstacles (murs) et leurs interactions.
 * Contient également la logique de génération procédurale de la carte.
 */
public class Grille implements IntGrille {

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

    @Override
    public int getLargeur() {
        return largeur;
    }

    @Override
    public int getHauteur() {
        return hauteur;
    }

    public void setParticule(int x, int y, Particule p) {
        if (!inLimites(x, y))
            return;
        particules[x][y] = p;
    }

    @Override
    public Particule getParticule(int x, int y) {
        if (!inLimites(x, y))
            return null;
        return particules[x][y];
    }

    @Override
    public boolean inLimites(int x, int y) {
        return x >= 0 && x < largeur && y >= 0 && y < hauteur;
    }

    @Override
    public boolean isObstacle(int x, int y) {
        if (!inLimites(x, y)) {
            return true;
        }
        return murs[x][y];
    }

    public boolean isParticule(int x, int y) {
        return getParticule(x, y) != null;
    }

    /**
     * Vérifie si la particule en (x,y) est ennemie par rapport à p.
     */
    public boolean isEnnemi(Particule p, int x, int y) {
        return isParticule(x, y) && getParticule(x, y).getEquipe() != p.getEquipe();
    }

    /**
     * Gère l'attaque d'une particule sur une autre.
     * Si la cible n'a plus d'énergie, elle change d'équipe.
     */
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

    /**
     * Transfère de l'énergie d'une particule à un allié.
     */
    public void transfererEnergie(Particule donneur, int x, int y) {
        Particule receveur = getParticule(x, y);
        float seuilMax = 100.0f;
        
        if (donneur.getEnergie() < 20 || receveur.getEnergie() >= seuilMax) return;

        float don = config.transfertEnergie();
        
        float qte = Math.min(donneur.getEnergie(), don);
        
        donneur.removeEnergie(qte);
        receveur.addEnergie(qte);
    }

    /**
     * Déplace une particule d'une case à une autre (si vide et sans obstacle).
     */
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
     * Génère des murs massifs en travaillant d'abord en basse résolution.
     */
    private void genererMursAleatoires() {
        int echelle = 6; 

        //On crée une grille temporaire minuscule
        int wSmall = largeur / echelle;
        int hSmall = hauteur / echelle;
        boolean[][] petiteGrille = new boolean[wSmall][hSmall];
        
        java.util.Random rand = new java.util.Random();

        //On génère le bruit sur la petite grille
        for (int x = 0; x < wSmall; x++) {
            for (int y = 0; y < hSmall; y++) {
                if (x < 3 || x > wSmall - 4 || y < 3 || y > hSmall - 4) {
                    petiteGrille[x][y] = false;
                } else {
                    petiteGrille[x][y] = rand.nextDouble() < 0.45;
                }
            }
        }

        //On lisse la petite grille (pour avoir des formes cohérentes)
        for (int i = 0; i < 5; i++) {
            petiteGrille = lisserPetiteGrille(petiteGrille, wSmall, hSmall);
        }

        // On reporte le résultat sur la vraie grille
        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                // On va chercher la valeur correspondante dans la petite grille
                int sx = Math.min(x / echelle, wSmall - 1);
                int sy = Math.min(y / echelle, hSmall - 1);
                murs[x][y] = petiteGrille[sx][sy];
            }
        }

        for (int i = 0; i < 2; i++) {
            lisserCarte();
        }


        int spawnRougeX = 40;  
        int spawnRougeY = 40;
        
        int spawnBleuX = largeur-40; 
        int spawnBleuY = 40;

        int spawnJauneX = 40;  
        int spawnJauneY = hauteur-40;

        int spawnVertX = largeur-40;  
        int spawnVertY = hauteur-40;
        nettoyerCercle(spawnRougeX, spawnRougeY, 45);
        nettoyerCercle(spawnBleuX, spawnBleuY, 45);
        nettoyerCercle(spawnJauneX, spawnJauneY, 45);
        nettoyerCercle(spawnVertX, spawnVertY, 45);
        // On supprime les petites îles isolées pour que tout soit connecté
        supprimerZonesIsolees();
    
    }

    /**
     * Force une zone à devenir vide (supprime les murs).
     */
    private void nettoyerCercle(int cx, int cy, int rayon) {
        int minX = Math.max(0, cx - rayon);
        int maxX = Math.min(largeur, cx + rayon);
        int minY = Math.max(0, cy - rayon);
        int maxY = Math.min(hauteur, cy + rayon);

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                if (Math.pow(x - cx, 2) + Math.pow(y - cy, 2) <= rayon * rayon) {
                    murs[x][y] = false;
                }
            }
        }
    }


    private boolean[][] lisserPetiteGrille(boolean[][] input, int w, int h) {
        boolean[][] output = new boolean[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int count = 0;
                // Compter les voisins sur la petite grille
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int nx = x + i;
                        int ny = y + j;
                        if (nx >= 0 && nx < w && ny >= 0 && ny < h) {
                            if (i != 0 || j != 0) {
                                if (input[nx][ny]) count++;
                            }
                        } else {
                            count++; 
                        }
                    }
                }
                if (count > 4) output[x][y] = true;
                else if (count < 4) output[x][y] = false;
                else output[x][y] = input[x][y];
            }
        }
        return output;
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
