package modele;

import java.util.LinkedList;
import java.util.Queue;
import util.Point;

public final class BFS {

    private static final int [][] DIRECTIONS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    public GrilleGradient calcul(Grille grille, Equipe equipe){
        int largeur = grille.getLargeur();
        int hauteur = grille.getHauteur();

        int[][] distances = new int[largeur][hauteur];


        
        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                distances[x][y] = Integer.MAX_VALUE;
            }
        }

        Queue<Point> queue = new LinkedList<>();

        int targetX = equipe.getCible().x();
        int targetY = equipe.getCible().y();

        if (grille.inLimites(targetX, targetY) && !grille.isObstacle(targetX, targetY)) {
            distances[targetX][targetY] = 0;
            queue.add(new Point(targetX, targetY));
        }
        while (!queue.isEmpty()) {
            Point current = queue.poll();
            int currentDist = distances[current.x()][current.y()];
            int nextDist = currentDist + 1;

            for (int[] dir : DIRECTIONS) {
                int nx = current.x() + dir[0];
                int ny = current.y() + dir[1];

                if (grille.inLimites(nx, ny)) {
                    if (!grille.isObstacle(nx, ny) && distances[nx][ny] > nextDist) {
                        
                        distances[nx][ny] = nextDist;
                        queue.add(new Point(nx, ny));
                    }
                }
            }
        }
        return new GrilleGradient(distances);
    }



    
}
