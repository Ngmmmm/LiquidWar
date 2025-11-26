package modele;

public class Grille {

    private final int largeur;
    private final int hauteur;

    private final boolean[][] murs;
    private final Particule[][] particules;

    public Grille(int largeur, int hauteur) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.murs = new boolean[largeur][hauteur];
        this.particules = new Particule[largeur][hauteur];
    }


    public int getLargeur() {
        return largeur;
    }

    public int getHauteur() {
        return hauteur;
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
    
}
