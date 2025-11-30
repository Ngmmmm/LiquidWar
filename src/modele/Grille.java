package modele;

import util.GameConfig;

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
        if (getParticule(x, y) == null)
            return false;
        else
            return true;
    }

    public boolean isEnnemi(Particule p, int x, int y) {
        return isParticule(x, y) && getParticule(x, y).getEquipe() != p.getEquipe();
    }

    public void AttaquerParticule(Particule attaquant, int x, int y) {
        Particule cible = getParticule(x, y);
        if (cible == null) return;

        float degats = config.degatsAttaque();
        float pris = Math.min(cible.getEnergie(), degats);

        cible.removeEnergie(pris);
        attaquant.addEnergie(pris);

        if (cible.getEnergie() < config.seuilConversion()) {
            cible.setEquipe(attaquant.getEquipe());

            float besoin = config.energieInitiale() - cible.getEnergie();
            if (besoin > 0) {
                float paye = Math.min(attaquant.getEnergie(), besoin);
                attaquant.removeEnergie(paye);
                cible.addEnergie(paye);
            }
        }
    }

    public void TransfererEnergie(Particule donneur, int x, int y) {
        Particule receveur = getParticule(x, y);
        float qte = Math.min(donneur.getEnergie(), config.transfertEnergie());
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

}
