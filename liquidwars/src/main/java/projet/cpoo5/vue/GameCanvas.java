package projet.cpoo5.vue;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import projet.cpoo5.modele.Grille;
import projet.cpoo5.modele.Particule;
import projet.cpoo5.util.Point;

public class GameCanvas extends Canvas {

    private final Grille grille;
    private final WritableImage buffer;
    private final PixelWriter pixelWriter;

    // Variables pour la conversion Souris <-> Jeu
    private double ratio = 1.0;
    private double xOffset = 0;
    private double yOffset = 0;

    public GameCanvas(Grille grille) {
        this.grille = grille;
        
        // Le buffer interne reste FIXE à la taille du jeu (320x240)
        this.buffer = new WritableImage(grille.getLargeur(), grille.getHauteur());
        this.pixelWriter = buffer.getPixelWriter();

        // Initialisation à une taille par défaut
        this.setWidth(800);
        this.setHeight(600);
    }

    /**
     * Méthode principale de dessin appelée par l'AnimationTimer
     */
    public void draw() {
        // 1. On dessine le jeu dans le petit buffer (Pixel Art)
        peindreBufferInterne();

        // 2. On affiche ce buffer en grand sur l'écran
        afficherSurEcran();
    }

    private void peindreBufferInterne() {
        // On efface le buffer interne en blanc (ou couleur de fond)
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
    }

    private void afficherSurEcran() {
        GraphicsContext gc = this.getGraphicsContext2D();
        double wEcran = getWidth();
        double hEcran = getHeight();

        // Fond gris pour les bandes noires
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, 0, wEcran, hEcran);

        // Calcul du ratio pour garder les proportions (320x240)
        double ratioX = wEcran / grille.getLargeur();
        double ratioY = hEcran / grille.getHauteur();
        this.ratio = Math.min(ratioX, ratioY);

        double wImage = grille.getLargeur() * ratio;
        double hImage = grille.getHauteur() * ratio;
        
        // Centrage
        this.xOffset = (wEcran - wImage) / 2;
        this.yOffset = (hEcran - hImage) / 2;

        // Mode "Pixel Art" net
        gc.setImageSmoothing(false);
        
        // Dessin centré
        gc.drawImage(buffer, xOffset, yOffset, wImage, hImage);
    }

    /**
     * Conversion Clic Ecran -> Pixel Jeu
     */
    public Point convertirCoordonnees(double screenX, double screenY) {
        int gameX = (int) ((screenX - xOffset) / ratio);
        int gameY = (int) ((screenY - yOffset) / ratio);
        
        // Clamp pour rester dans la grille
        gameX = Math.max(0, Math.min(gameX, grille.getLargeur() - 1));
        gameY = Math.max(0, Math.min(gameY, grille.getHauteur() - 1));
        
        return new Point(gameX, gameY);
    }

    @Override
    public boolean isResizable() { return true; }
    @Override
    public double maxHeight(double w) { return Double.MAX_VALUE; }
    @Override
    public double maxWidth(double h) { return Double.MAX_VALUE; }
    @Override
    public double minHeight(double w) { return 100; }
    @Override
    public double minWidth(double h) { return 100; }
}