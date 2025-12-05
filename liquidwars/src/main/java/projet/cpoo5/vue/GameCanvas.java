package projet.cpoo5.vue;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import projet.cpoo5.modele.Equipe;
import projet.cpoo5.modele.Grille;
import projet.cpoo5.modele.Particule;
import projet.cpoo5.util.Point;

public class GameCanvas extends Canvas {

    private final Grille grille;
    private final WritableImage buffer;
    private final PixelWriter pixelWriter;

    private final Equipe equipeRouge;
    private final Equipe equipeBleue;
    // Variables pour la conversion Souris <-> Jeu
    private double ratio = 1.0;
    private double xOffset = 0;
    private double yOffset = 0;

    private long tickCounter = 0;

    public GameCanvas(Grille grille, Equipe rouge, Equipe bleu) {
        this.grille = grille;
        this.equipeRouge = rouge;
        this.equipeBleue = bleu;
        
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
        // On avance le temps
        tickCounter++;
        // 1. On dessine le jeu dans le petit buffer (Pixel Art)
        peindreBufferInterne();

        // 2. On affiche ce buffer en grand sur l'écran
        afficherSurEcran();
    }

    private void peindreBufferInterne() {
        for (int x = 0; x < grille.getLargeur(); x++) {
            for (int y = 0; y < grille.getHauteur(); y++) {
                if (grille.isObstacle(x, y)) {
                    pixelWriter.setColor(x, y, Color.BLACK);
                } else {
                    Particule p = grille.getParticule(x, y);
                    if (p != null) {
                        //CALCUL DE LA BRILLANCE
                        // On récupère l'énergie
                        double energie = p.getEnergie();
                        double maxEnergie = 100.0; 
                        
                        // On calcule un facteur de luminosité entre 0.3 (sombre) et 1.0 (vif)
                        double facteur = 0.3 + (0.7 * (energie / maxEnergie));
                    
                        facteur = Math.min(1.0, Math.max(0.3, facteur));

                        // On prend la couleur de base de l'équipe
                        Color base = p.getEquipe().getCouleur();
                        
                        Color finale = base.deriveColor(0, 1.0, facteur, 1.0);
                        
                        pixelWriter.setColor(x, y, finale);
                    } 
                    else {
                        pixelWriter.setColor(x, y, Color.WHITE);
                    }
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

        dessinerCible(gc, equipeRouge);
        dessinerCible(gc, equipeBleue);
    }
    

    private void dessinerCible(GraphicsContext gc, Equipe equipe) {
        Point cible = equipe.getCible();
        if (cible == null) return;

        double cx = cible.x() * ratio + xOffset;
        double cy = cible.y() * ratio + yOffset;
        
        double rayon = 5 * ratio; 
        double epaisseur = 2 * ratio;
 
        gc.setStroke(equipe.getCouleur().deriveColor(0, 1, 1, 0.5)); 
        gc.setLineWidth(epaisseur);
        gc.strokeOval(cx - rayon, cy - rayon, rayon * 2, rayon * 2);

        Color couleurOpposee = getOppositeColor(equipe.getCouleur());
        gc.setStroke(couleurOpposee);
        
        double angleRotation = (tickCounter * 5) % 360; 
        
        gc.strokeArc(cx - rayon, cy - rayon, rayon * 2, rayon * 2, angleRotation, 60, ArcType.OPEN);
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

    /**
     * Calcule la couleur opposée (Négatif) sur le spectre.
     */
    private Color getOppositeColor(Color c) {
        // On inverse les composantes RGB : 1.0 - valeur
        // Rouge (1,0,0) devient Cyan (0,1,1)
        // Bleu (0,0,1) devient Jaune (1,1,0)
        return new Color(1.0 - c.getRed(), 1.0 - c.getGreen(), 1.0 - c.getBlue(), 1.0);
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