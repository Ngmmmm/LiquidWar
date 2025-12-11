package projet.cpoo5.controle;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import projet.cpoo5.modele.Equipe;
import projet.cpoo5.modele.Grille;
import projet.cpoo5.util.Point;
import projet.cpoo5.vue.GameCanvas;

public class InputHandler {

    private final Equipe joueur1; 
    private final Equipe joueur2; 

    private final int largeurMap;
    private final int hauteurMap;

    private final GameCanvas canvas;

    private final Grille grille;

    /**
     * @param width Largeur de la grille (pour borner les mouvements)
     * @param height Hauteur de la grille
     */
    public InputHandler(Scene scene, GameCanvas canvas, Equipe joueur1, Equipe joueur2, int width, int height, Grille grille) {
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        this.canvas = canvas;
        this.largeurMap = width;
        this.hauteurMap = height;
        this.grille = grille;

        scene.setOnMouseMoved(this::gererMouvementSouris);
        scene.setOnKeyPressed(this::gererClavier);
    }

    // Méthode utilitaire pour garder la cible dans la carte
    private int clamp(int valeur, int max) {
        
        return Math.max(0, Math.min(valeur, max - 1));
    }

    //Gestion SOURIS

    private void gererMouvementSouris(MouseEvent event) {
        
        Point p = canvas.convertirCoordonnees(event.getX(), event.getY());
        int x = clamp(p.x(), largeurMap);
        int y = clamp(p.y(), hauteurMap);
        
        if (!grille.isObstacle(x, y)) {
            joueur1.setCible(new Point(x, y));
        }
    }

    //Gestion CLAVIER

    private void gererClavier(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            System.exit(0);
        }
        
        Point p = joueur2.getCible();
        int nx = p.x(); 
        int ny = p.y(); 
        if (event.getCode() == KeyCode.UP)    ny -= 10;
        if (event.getCode() == KeyCode.DOWN)  ny += 10;
        if (event.getCode() == KeyCode.LEFT)  nx -= 10;
        if (event.getCode() == KeyCode.RIGHT) nx += 10;
        
        nx = clamp(nx, grille.getLargeur());
        ny = clamp(ny, grille.getHauteur());

        if (!grille.isObstacle(nx, ny)) {
            joueur2.setCible(new Point(nx, ny));
        }
    }
}