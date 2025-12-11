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
import projet.cpoo5.controle.MenuController; 
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

    private Stage primaryStage; 

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        
        MenuController menu = new MenuController(stage, this);
        menu.afficher();
    }

    /**
     * Cette méthode est appelée par le MenuController quand on clique sur Jouer.
     */
    public void lancerPartie(boolean modeSolo) {
        //INITIALISATION
        GameConfig config = new GameConfig(320, 240, 500, 100, 0, 5, 2);
        Grille grille = new Grille(config);
        
        Equipe rouge = new Equipe(Color.RED, new Point(50, 50));
        Equipe bleu = new Equipe(Color.BLUE, new Point(200, 200));

        Random rand = new Random();
        // Spawn Rouge
        for(int i=0; i<1000; i++) {
            int rx = rand.nextInt(180); int ry = rand.nextInt(400);
            if(grille.inLimites(rx, ry) && !grille.isObstacle(rx, ry) && grille.getParticule(rx, ry) == null) 
                grille.setParticule(rx, ry, new Particule(rouge, rx, ry, 100));
        }
        // Spawn Bleu
        for(int i=0; i<1000; i++) {
            int bx = 160 + rand.nextInt(150); int by = rand.nextInt(240);
            if(grille.inLimites(bx, by) && !grille.isObstacle(bx, by) && grille.getParticule(bx, by) == null) 
                grille.setParticule(bx, by, new Particule(bleu, bx, by, 100));
        }
        
        BFS bfs = new BFS();

        //VUE JEU
        GameCanvas canvas = new GameCanvas(grille, rouge, bleu);
        StackPane root = new StackPane(canvas); 
        Scene sceneJeu = new Scene(root, 800, 600); 

        canvas.widthProperty().bind(sceneJeu.widthProperty());
        canvas.heightProperty().bind(sceneJeu.heightProperty());

        //CONTROLE JEU
        new InputHandler(sceneJeu, canvas, rouge, bleu, config.largeur(), config.hauteur());

        //BOUCLE DE JEU
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!primaryStage.isShowing()) this.stop();

                rouge.setGrilleGradient(bfs.calcul(grille, rouge));
                bleu.setGrilleGradient(bfs.calcul(grille, bleu));
                
                int countR = 0;
                int countB = 0;

                List<Particule> aDeplacer = new ArrayList<>();
                for(int x=0; x<grille.getLargeur(); x++) {
                    for(int y=0; y<grille.getHauteur(); y++) {
                        Particule p = grille.getParticule(x, y);
                        if (p != null) {
                            aDeplacer.add(p);
                            if (p.getEquipe() == rouge) countR++;
                            else if (p.getEquipe() == bleu) countB++;
                        }
                    }
                }

                Collections.shuffle(aDeplacer);

                for (Particule p : aDeplacer) {
                    GrilleGradient gradient = p.getEquipe().getGrilleGradient();
                    if (gradient != null) {
                        Deplacement.choisirDirection(grille, gradient, p);
                    }
                }
                canvas.setScores(countR, countB);
                canvas.draw();
            }
        };
        timer.start();
        
        // CHANGEMENT DE SCÈNE
        primaryStage.setScene(sceneJeu);
    }

    public static void main(String[] args) { launch(); }
}