package modele;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;


public class Deplacement {

    private static final int[][] DIRECTIONS = {
            { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }
    };

    public static void choisirDirection(Grille grille, GrilleGradient grilleGradient, Particule p) {

        Map<TypeDirection, List<Integer>> directions = new HashMap<>();

        directions.put(TypeDirection.PRINCIPALE, new ArrayList<>());
        directions.put(TypeDirection.BONNE, new ArrayList<>());
        directions.put(TypeDirection.ACCEPTABLE, new ArrayList<>());

        int x = p.getX();
        int y = p.getY();

        int d0 = grilleGradient.getDistance(x, y);

        int[] dVoisins = new int[DIRECTIONS.length];

        // Pour récupérer le gradient de tous les voisins
        for (int i = 0; i < DIRECTIONS.length; i++) {
            int nx = x + DIRECTIONS[i][0];
            int ny = y + DIRECTIONS[i][1];
            if (!grille.inLimites(nx, ny))
                dVoisins[i] = Integer.MAX_VALUE;
            else
                dVoisins[i] = grilleGradient.getDistance(nx, ny);
        }

        // Pour trouver la distance minimale des voisins
        int minVoisin = Integer.MAX_VALUE;
        for (int d : dVoisins) {
            if (d < minVoisin)
                minVoisin = d;
        }

        // Pour classer les directions
        for (int i = 0; i < dVoisins.length; i++) {
            int d = dVoisins[i];
            if (d == Integer.MAX_VALUE)
                continue ;

            /* direction principale (valeur de gradient minimale) */

            if (d == minVoisin && minVoisin < d0)
                directions.get(TypeDirection.PRINCIPALE).add(i);

            /*
             * – bonne direction (valeur de gradient plus petite que la valeur de gradient
             * du pixel où se
             * trouve la particule)
             */

            else if (d < d0)
                directions.get(TypeDirection.BONNE).add(i);

            /* – direction acceptable (valeur de gradient identique) */

            else if (d == d0)
                directions.get(TypeDirection.ACCEPTABLE).add(i);

            /*
             * – direction impossible (valeur plus grande, qui nous éloigne de l’objectif)
             */

            else
                continue ;
        }

        /* 1. Si une direction principale est libre, on y va. */

        if (essayerDeplacer(directions.get(TypeDirection.PRINCIPALE), x, y, grille, p))
            return;

        /* 2. Si une bonne direction est libre, on y va. */

        if (essayerDeplacer(directions.get(TypeDirection.BONNE), x, y, grille, p))
            return;

        /* 3. Si une direction acceptable est libre, on y va. */

        if (essayerDeplacer(directions.get(TypeDirection.ACCEPTABLE), x, y, grille, p))
            return;

        /* 4. Si une direction principale est occupée par un ennemi, on l’attaque. */

        if (essayerAttaquer(directions.get(TypeDirection.PRINCIPALE), x, y, grille, p))
            return;

        /* 5. Si une bonne direction est occupée par un ennemi, on l’attaque 2 */

        if (essayerAttaquer(directions.get(TypeDirection.BONNE), x, y, grille, p))
            return;

        /*
         * 6. Si une direction principale est occupée par un ami, on lui transfère de
         * notre énergie
         */

        if (essayerTransferer(directions.get(TypeDirection.PRINCIPALE), x, y, grille, p))
            return;

        /* 7. Sinon, on ne fait rien. */

        return;

    }

    private static boolean essayerDeplacer(List<Integer> direction, int x, int y, Grille grille, Particule p) {
        for (int d : direction) {
            int nx = x + DIRECTIONS[d][0];
            int ny = y + DIRECTIONS[d][1];

            if (grille.inLimites(nx, ny)
                    && !grille.isObstacle(nx, ny)
                    && grille.getParticule(nx, ny) == null) {

                grille.deplacerParticule(p, nx, ny);
                return true;
            }
        }
        return false;
    }

    private static boolean essayerAttaquer(List<Integer> direction, int x, int y, Grille grille, Particule p) {
        for (int d : direction) {
            int nx = x + DIRECTIONS[d][0];
            int ny = y + DIRECTIONS[d][1];

            if (grille.inLimites(nx, ny) && !grille.isObstacle(nx, ny) && grille.isEnnemi(p, nx, ny)) {
                grille.attaquerParticule(p, nx, ny);
                return true;
            }
        }
        return false;
    }

    private static boolean essayerTransferer(List<Integer> direction, int x, int y, Grille grille, Particule p) {
        for (int d : direction) {
            int nx = x + DIRECTIONS[d][0];
            int ny = y + DIRECTIONS[d][1];

            if (grille.inLimites(nx, ny) && !grille.isObstacle(nx, ny) && !grille.isEnnemi(p, nx, ny)) {
                grille.transfererEnergie(p, nx, ny);
                return true;
            }
        }
        return false;
    }


}
