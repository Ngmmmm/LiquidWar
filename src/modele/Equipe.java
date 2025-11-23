package modele;

import java.awt.Color;

public class Equipe {

    private final Color couleur;
    private int score;

    private volatile int cibleX;
    private volatile int cibleY;

    private volatile GrilleGradient grilleGradient;

    public Equipe(Color couleur, int cibleX, int cibleY) {
        this.couleur = couleur;
        this.score = 0;
        this.cibleX = cibleX;
        this.cibleY = cibleY;
    }

    public Color getCouleur() {
        return couleur;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    public GrilleGradient getGrilleGradient() {
        return grilleGradient;
    }
    public void setGrilleGradient(GrilleGradient grilleGradient) {
        this.grilleGradient = grilleGradient;
    }
    public int getCibleX() {
        return cibleX;
    }
    public int getCibleY() {
        return cibleY;
    }
    public void setCible(int x, int y) {
        this.cibleX = x;
        this.cibleY = y;
    }
}
