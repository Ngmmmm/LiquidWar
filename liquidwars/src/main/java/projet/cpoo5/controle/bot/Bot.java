package projet.cpoo5.controle.bot;

import java.util.List;
import java.util.Random;

import projet.cpoo5.modele.*;
import projet.cpoo5.util.Point;


public abstract class Bot {

    protected final Equipe monEquipe;
    protected final Grille grille;
    protected final List<Equipe> adversaires; 
    protected final Random rand = new Random();

    public Bot(Equipe monEquipe, Grille grille, List<Equipe> adversaires) {
        this.monEquipe = monEquipe;
        this.grille = grille;
        this.adversaires = adversaires;
    }

    public abstract void update();

    protected boolean isFree(int x, int y) {
        return grille.inLimites(x, y) && !grille.isObstacle(x, y);
    }

    protected void trySetTarget(int x, int y) {
        if (isFree(x, y)) monEquipe.setCible(new Point(x, y));
    }

    protected void stepTargetToward(Point goal) {
        var c = monEquipe.getCible();
        int dx = Integer.compare(goal.x(), c.x());
        int dy = Integer.compare(goal.y(), c.y());

        // essaie d’abord diagonale, puis axes (pour contourner un mur)
        int[][] tries = { {dx,dy}, {dx,0}, {0,dy}, {1,0}, {-1,0}, {0,1}, {0,-1} };
        for (int[] t : tries) {
            int nx = c.x() + t[0];
            int ny = c.y() + t[1];
            if (isFree(nx, ny)) { trySetTarget(nx, ny); return; }
        }
    }


    protected Point randomFreeCell(int tries) {
        for (int i = 0; i < tries; i++) {
            int x = rand.nextInt(grille.getLargeur());
            int y = rand.nextInt(grille.getHauteur());
            if (isFree(x, y)) return new Point(x, y);
        }
        return monEquipe.getCible();
    }

    protected Point centroidOf(Equipe e) {
        long sx = 0, sy = 0, n = 0;
        for (int x = 0; x < grille.getLargeur(); x++) {
            for (int y = 0; y < grille.getHauteur(); y++) {
                var p = grille.getParticule(x, y);
                if (p != null && p.getEquipe() == e) { sx += x; sy += y; n++; }
            }
        }
        if (n == 0) return e.getCible();
        return new Point((int)(sx / n), (int)(sy / n));
    }
}