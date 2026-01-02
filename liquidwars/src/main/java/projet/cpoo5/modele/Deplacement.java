package projet.cpoo5.modele;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import projet.cpoo5.util.TypeDirection;


/**
 * Gère la logique de déplacement et d'interaction des particules.
 * Décide pour chaque particule si elle doit bouger, attaquer ou transférer de l'énergie
 * en fonction du gradient calculé par le BFS.
 */
public class Deplacement {

    private static final int[][] DIRECTIONS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1},   // Cardinales
        {1, 1}, {1, -1}, {-1, 1}, {-1, -1}  // Diagonales
    };

    /**
     * Choisit la meilleure action pour une particule.
     * Priorise le mouvement vers les cases ayant une distance plus faible vers la cible.
     * Si bloqué, tente d'attaquer les ennemis ou de transférer de l'énergie aux alliés.
     * @param grille La grille de jeu (pour vérifier l'occupation des cases).
     * @param grilleGradient La carte des distances pour l'équipe de la particule.
     * @param p La particule à déplacer.
     */
    public static void choisirDirection(Grille grille, GrilleGradient grilleGradient, Particule p) {

        // Préparation des listes pour classer les voisins
        Map<TypeDirection, List<Integer>> directions = new HashMap<>();
        directions.put(TypeDirection.PRINCIPALE, new ArrayList<>());
        directions.put(TypeDirection.BONNE, new ArrayList<>());
        directions.put(TypeDirection.ACCEPTABLE, new ArrayList<>());

        int x = p.getX();
        int y = p.getY();
        
        // Distance actuelle
        int d0 = grilleGradient.getDistance(x, y);

        int minVoisin = Integer.MAX_VALUE;
        int[] dVoisins = new int[DIRECTIONS.length];

       
        for (int i = 0; i < DIRECTIONS.length; i++) {
            int nx = x + DIRECTIONS[i][0];
            int ny = y + DIRECTIONS[i][1];
            
            if (!grille.inLimites(nx, ny)) dVoisins[i] = Integer.MAX_VALUE;
            else dVoisins[i] = grilleGradient.getDistance(nx, ny);
            
            if (dVoisins[i] < minVoisin) minVoisin = dVoisins[i];
        }

        // On classe les directions 
        for (int i = 0; i < dVoisins.length; i++) {
            int d = dVoisins[i];
            if (d == Integer.MAX_VALUE) continue;

            // Direction Principale 
            if (d == minVoisin && minVoisin < d0) {
                directions.get(TypeDirection.PRINCIPALE).add(i);
            } 
            // Bonne Direction 
            else if (d < d0) {
                directions.get(TypeDirection.BONNE).add(i);
            } 
            // Direction Acceptable 
            else if (d == d0) {
                directions.get(TypeDirection.ACCEPTABLE).add(i);
            }
        }

        

        // 1. Si une direction principale est libre, on y va.
        if (essayerDeplacer(directions.get(TypeDirection.PRINCIPALE), x, y, grille, p)) return;

        // 2. Si une bonne direction est libre, on y va.
        if (essayerDeplacer(directions.get(TypeDirection.BONNE), x, y, grille, p)) return;

        // 3. Si une direction acceptable est libre, on y va.
        if (essayerDeplacer(directions.get(TypeDirection.ACCEPTABLE), x, y, grille, p)) return;

        //4. Si une direction principale est occupée par un ennemi, on l'attaque.
        if (essayerAttaquer(directions.get(TypeDirection.PRINCIPALE), x, y, grille, p)) return;

        // 5. Si une bonne direction est occupée par un ennemi, on l'attaque.
        if (essayerAttaquer(directions.get(TypeDirection.BONNE), x, y, grille, p)) return;

        // 6. Si une direction principale est occupée par un ami, on lui transfère de l'énergie mais on continue de bouger
        if (essayerTransferer(directions.get(TypeDirection.PRINCIPALE), x, y, grille, p)){
            List<Integer> directionsAleatoires = new ArrayList<>();
            directionsAleatoires.add(0); directionsAleatoires.add(1);
            directionsAleatoires.add(2); directionsAleatoires.add(3);
            if (essayerDeplacer(directionsAleatoires, x, y, grille, p)) return;
            return;
        }

        // 7. Sinon on tremble un peu pour éviter de figer le jeu
        List<Integer> directionsAleatoires = new ArrayList<>();
        directionsAleatoires.add(0); directionsAleatoires.add(1);
        directionsAleatoires.add(2); directionsAleatoires.add(3);
        essayerDeplacer(directionsAleatoires, x, y, grille, p);
    }

    

    private static boolean essayerDeplacer(List<Integer> direction, int x, int y, Grille grille, Particule p) {
        if (direction.isEmpty()) return false;
        Collections.shuffle(direction);
        for (int d : direction) {
            int nx = x + DIRECTIONS[d][0];
            int ny = y + DIRECTIONS[d][1];

            if (grille.inLimites(nx, ny) && !grille.isObstacle(nx, ny) && grille.getParticule(nx, ny) == null) {
                grille.deplacerParticule(p, nx, ny);
                return true;
            }
        }
        return false;
    }

    private static boolean essayerAttaquer(List<Integer> direction, int x, int y, Grille grille, Particule p) {
        if (direction.isEmpty()) return false;
        Collections.shuffle(direction);
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
        if (direction.isEmpty()) return false;
        Collections.shuffle(direction);
        for (int d : direction) {
            int nx = x + DIRECTIONS[d][0];
            int ny = y + DIRECTIONS[d][1];

            if (grille.inLimites(nx, ny) && !grille.isObstacle(nx, ny) 
                && !grille.isEnnemi(p, nx, ny) && grille.getParticule(nx, ny) != null) {
                
                grille.transfererEnergie(p, nx, ny);
                return true;
            }
        }
        return false;
    }
}