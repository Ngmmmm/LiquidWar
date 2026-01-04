package projet.cpoo5.modele;

/**
 * Conteneur immuable représentant la carte des distances (Flow Field).
 * Utilisé par les particules pour connaître la distance vers la cible depuis n'importe quelle case.
 */
public final class GrilleGradient {

    private final int[][] distance;

    private final int largeur;
    private final int hauteur;



    public GrilleGradient(int[][] distance){
        this.distance = distance;
        this.largeur = distance.length;
        this.hauteur = (largeur > 0) ? distance[0].length : 0;
    }

    public int getLargeur() {
        return largeur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public int getDistance(int x, int y) {

        if (x < 0 || x >= largeur || y < 0 || y >= hauteur) {
            return Integer.MAX_VALUE;
        }
        return distance[x][y];
    }
    
}
