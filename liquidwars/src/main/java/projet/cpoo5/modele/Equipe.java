package projet.cpoo5.modele;

import javafx.scene.paint.Color;
import projet.cpoo5.util.Point;


/**
 * Représente une équipe dans le jeu.
 * Contient les informations de base (nom, couleur) ainsi que la cible actuelle
 * et la grille de gradient associée (calculée par le BFS).
 */
public final class Equipe implements IntEquipe {

    private final Color couleur;
    private int score;
    private final String nom;

    private volatile Point cible;

    private volatile GrilleGradient grilleGradient;

    public Equipe(String nom,Color couleur, Point cible) {
        this.couleur = couleur;
        this.score = 0;
        this.cible = cible;
        this.nom = nom;
    }

    @Override
    public String getNom() {
        return nom;
    }
    @Override
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

    @Override
    public Point getCible(){
        return this.cible;
    }
}
