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
        GameConfig config = new GameConfig(600, 400, 1000, 100, 0, 5, 2);
        
        // 2. INITIALISATION
        Grille grille = new Grille(config);
        
        for(int x=200; x<220; x++) {
            for(int y=0; y<250; y++) {
                grille.setWall(x, y, true); 
            }
        }
        
        for(int x=380; x<400; x++) {
            for(int y=150; y<400; y++) {
                grille.setWall(x, y, true); 
            }
        }

        Equipe rouge = new Equipe(Color.RED, new Point(50, 50));
        Equipe bleu = new Equipe(Color.BLUE, new Point(550, 350));

        Random rand = new Random();
        
   
        for(int i=0; i<2000; i++) {
            int rx = rand.nextInt(180); 
            int ry = rand.nextInt(400);
            if(grille.inLimites(rx, ry) && !grille.isObstacle(rx, ry) && grille.getParticule(rx, ry) == null) {
                grille.setParticule(rx, ry, new Particule(rouge, rx, ry, 100));
            }
        }
        

        for(int i=0; i<2000; i++) {
            int rx = 420 + rand.nextInt(180); 
            int ry = rand.nextInt(400);
            if(grille.inLimites(rx, ry) && !grille.isObstacle(rx, ry) && grille.getParticule(rx, ry) == null) {
                grille.setParticule(rx, ry, new Particule(bleu, rx, ry, 100));
            }
        }

        BFS bfs = new BFS();

        // 3. VUE
        GameCanvas canvas = new GameCanvas(grille);
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, config.largeur(), config.hauteur());

        // 4. INPUTS
        scene.setOnMouseMoved(event -> {
            rouge.setCible(new Point((int)event.getX(), (int)event.getY()));
        });
        
        scene.setOnMouseClicked(event -> {
             bleu.setCible(new Point((int)event.getX(), (int)event.getY()));
        });

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
        stage.setTitle("Liquid War - Etape 3 (Demo Fluide)");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}