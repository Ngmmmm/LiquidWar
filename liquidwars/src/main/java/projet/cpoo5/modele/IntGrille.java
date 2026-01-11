package projet.cpoo5.modele;


/**
 * Interface en lecture seule pour la Grille.
 * Permet à la Vue de lire l'état du plateau (murs, particules) sans le modifier.
 */
public interface IntGrille {
    int getLargeur();
    int getHauteur();
    boolean inLimites(int x, int y);
    boolean isObstacle(int x, int y);
    
    IntParticule getParticule(int x, int y);
}
