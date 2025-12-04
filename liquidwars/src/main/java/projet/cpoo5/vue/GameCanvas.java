package projet.cpoo5.vue;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import projet.cpoo5.modele.Grille;
import projet.cpoo5.modele.Particule;

public class GameCanvas extends Canvas {
    private final Grille grille;
    private final WritableImage buffer;
    private final PixelWriter pixelWriter;

    public GameCanvas(Grille grille) {
        this.grille = grille;
        this.setWidth(grille.getLargeur());
        this.setHeight(grille.getHauteur());
        this.buffer = new WritableImage(grille.getLargeur(), grille.getHauteur());
        this.pixelWriter = buffer.getPixelWriter();
    }

    public void draw() {
        GraphicsContext gc = this.getGraphicsContext2D();
        for (int x = 0; x < grille.getLargeur(); x++) {
            for (int y = 0; y < grille.getHauteur(); y++) {
                if (grille.isObstacle(x, y)) {
                    pixelWriter.setColor(x, y, Color.BLACK);
                } else {
                    Particule p = grille.getParticule(x, y);
                    if (p != null) pixelWriter.setColor(x, y, p.getEquipe().getCouleur());
                    else pixelWriter.setColor(x, y, Color.WHITE);
                }
            }
        }
        gc.drawImage(buffer, 0, 0);
    }
}