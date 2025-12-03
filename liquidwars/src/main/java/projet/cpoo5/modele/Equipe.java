package projet.cpoo5.modele;

import java.awt.Color;

import projet.cpoo5.util.Point;


public class Equipe {

    private final Color couleur;
    private int score;

    private volatile Point cible;

    private volatile GrilleGradient grilleGradient;

    public Equipe(Color couleur, Point cible) {
        this.couleur = couleur;
        this.score = 0;
        this.cible = cible;
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
    public void setCible(Point cible) {
        this.cible = cible;
    }

    public Point getCible(){
        return this.cible;
    }
}
