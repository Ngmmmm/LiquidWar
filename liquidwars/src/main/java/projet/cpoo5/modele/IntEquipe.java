package projet.cpoo5.modele;

import javafx.scene.paint.Color;
import projet.cpoo5.util.Point;


/**
 * Interface en lecture seule pour une Equipe.
 * Permet à la Vue d'accéder aux infos d'affichage sans modifier le modèle.
 */
public interface IntEquipe {
    String getNom();
    Color getCouleur();
    Point getCible();
}
