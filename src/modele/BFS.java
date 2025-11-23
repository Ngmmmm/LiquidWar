package modele;



public final class BFS {

    private static final int [][] directions = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    public GrilleGradient calcul(Grille grille, Equipe equipe){
        int largeur = grille.getLargeur();
        int hauteur = grille.getHauteur();

        int[][] distances = new int[largeur][hauteur];

        //CODE BFS A COMPLETER

        return new GrilleGradient(distances);
    }



    
}
