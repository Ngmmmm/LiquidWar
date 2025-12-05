package projet.cpoo5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import projet.cpoo5.controle.InputHandler;
import projet.cpoo5.modele.BFS;
import projet.cpoo5.modele.Deplacement;
import projet.cpoo5.modele.Equipe;
import projet.cpoo5.modele.Grille;
import projet.cpoo5.modele.GrilleGradient;
import projet.cpoo5.modele.Particule;
import projet.cpoo5.util.GameConfig;
import projet.cpoo5.util.Point;
import projet.cpoo5.vue.GameCanvas;

public class LiquidWarApp extends Application {

    @Override
    public void start(Stage stage) {
        // 1. CONFIGURATION
        GameConfig config = new GameConfig(320, 240, 500, 100, 0, 5, 2);
        
        // 2. INITIALISATION
        Grille grille = new Grille(config);
        
        Equipe rouge = new Equipe(Color.RED, new Point(50, 50));
        Equipe bleu = new Equipe(Color.BLUE, new Point(200, 200));

        Random rand = new Random();
        
        for(int i=0; i<1000; i++) {
            int rx = rand.nextInt(180); 
            int ry = rand.nextInt(400);
            if(grille.inLimites(rx, ry) && !grille.isObstacle(rx, ry) && grille.getParticule(rx, ry) == null) {
                grille.setParticule(rx, ry, new Particule(rouge, rx, ry, 100));
            }
        }

        for(int i=0; i<1000; i++) {
            int bx = 160 + rand.nextInt(150); 
            int by = rand.nextInt(240);
             
            if(grille.inLimites(bx, by) && !grille.isObstacle(bx, by) && grille.getParticule(bx, by) == null) {
                grille.setParticule(bx, by, new Particule(bleu, bx, by, 100));
            }
        }
        
        BFS bfs = new BFS();

        // 3. VUE
        GameCanvas canvas = new GameCanvas(grille);
        StackPane root = new StackPane(canvas); 
        Scene scene = new Scene(root, 800, 600); // Taille de départ de la fenêtre

        // INDISPENSABLE : Le canvas doit suivre la taille de la fenêtre
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        // 4. Gestion COntrole
        new InputHandler(scene, canvas, rouge, bleu, config.largeur(), config.hauteur());

        // 5. BOUCLE DE JEU
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Calcul
                rouge.setGrilleGradient(bfs.calcul(grille, rouge));
                bleu.setGrilleGradient(bfs.calcul(grille, bleu));

                // Déplacement
                List<Particule> aDeplacer = new ArrayList<>();
                for(int x=0; x<grille.getLargeur(); x++) {
                    for(int y=0; y<grille.getHauteur(); y++) {
                        Particule p = grille.getParticule(x, y);
                        if (p != null) aDeplacer.add(p);
                    }
                }

                
                // aDeplacer.sort((p1, p2) -> {
                //         GrilleGradient g1 = p1.getEquipe().getGrilleGradient();
                //         GrilleGradient g2 = p2.getEquipe().getGrilleGradient();
                //         if (g1 == null || g2 == null) return 0;
                //         int d1 = g1.getDistance(p1.getX(), p1.getY());
                //         int d2 = g2.getDistance(p2.getX(), p2.getY());
                //         return Integer.compare(d1, d2);
                //     });
                    Collections.shuffle(aDeplacer);

                for (Particule p : aDeplacer) {
                    GrilleGradient gradient = p.getEquipe().getGrilleGradient();
                    if (gradient != null) {
                        Deplacement.choisirDirection(grille, gradient, p);
                    }
                }

                // Dessin
                canvas.draw();
            }
        };
        
        timer.start();
        stage.setTitle("Liquid War - MVC");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}