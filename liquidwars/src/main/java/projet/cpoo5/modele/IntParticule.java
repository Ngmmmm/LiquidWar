package projet.cpoo5.modele;

/**
 * Interface en lecture seule pour une Particule.
 * Permet à la Vue d'afficher les particules (position, énergie, équipe).
 */
public interface IntParticule {
    int getX();
    int getY();
    float getEnergie(); 
    
    
    IntEquipe getEquipe();
}