package projet.cpoo5.modele;

public class GrilleGradient {

    private final int[][] distance;

    private final int largeur;
    private final int hauteur;



    public GrilleGradient(int[][] distance){
        this.distance = distance;
        this.hauteur = distance.length;
        this.largeur = (hauteur > 0) ? distance[0].length : 0;
    }

    public int getDistance(int x, int y) {

        if (x < 0 || x >= largeur || y < 0 || y >= hauteur) {
            return Integer.MAX_VALUE;
        }
        return distance[x][y];
    }
    
}
