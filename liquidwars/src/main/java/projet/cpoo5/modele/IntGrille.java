package projet.cpoo5.modele;

public interface IntGrille {
    int getLargeur();
    int getHauteur();
    boolean inLimites(int x, int y);
    boolean isObstacle(int x, int y);
    
    IntParticule getParticule(int x, int y);
}
