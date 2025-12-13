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
import projet.cpoo5.vue.FinPartieVue;
import projet.cpoo5.vue.GameCanvas;
import projet.cpoo5.vue.MenuVue;

public class LiquidWarApp extends Application {

    private Stage primaryStage; 

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        
        MenuVue menu = new MenuVue(stage, this);
        menu.afficher();
    }

    /**
     * Cette méthode est appelée par le MenuController quand on clique sur Jouer.
     */
    public void lancerPartie(boolean modeSolo) {
        //INITIALISATION
        int wGrille = 320;
        int hGrille = 240;
        GameConfig config = new GameConfig(wGrille, hGrille, 500, 100, 0, 5, 2);
        Grille grille = new Grille(config);
        
        int spawnRX = 50;
        int spawnRY = hGrille / 2;
        
        int spawnBX = wGrille - 50;
        int spawnBY = hGrille / 2;

        // On place les cibles exactement au centre du spawn
        Equipe rouge = new Equipe(Color.RED, new Point(spawnRX, spawnRY));
        Equipe bleu = new Equipe(Color.BLUE, new Point(spawnBX, spawnBY));

        // On fait apparaître les armées autour de ces points fixes
        spawnerEquipeAutour(grille, rouge, spawnRX, spawnRY);
        spawnerEquipeAutour(grille, bleu, spawnBX, spawnBY);
        
        BFS bfs = new BFS();

        //VUE JEU
        GameCanvas canvas = new GameCanvas(grille, rouge, bleu);
        StackPane root = new StackPane(canvas); 
        Scene sceneJeu = new Scene(root, 800, 600); 

        canvas.widthProperty().bind(sceneJeu.widthProperty());
        canvas.heightProperty().bind(sceneJeu.heightProperty());

        //CONTROLE JEU
        new InputHandler(sceneJeu, canvas, rouge, bleu, config.largeur(), config.hauteur(), grille);

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

                if (countR == 0) {
                    this.stop();
                    root.getChildren().add(FinPartieVue.creer("LES BLEUS GAGNENT !", Color.BLUE, () -> LiquidWarApp.this.start(primaryStage)));
                } 
                else if (countB == 0) {
                    this.stop();
                    root.getChildren().add(FinPartieVue.creer("LES ROUGES GAGNENT !", Color.RED, () -> LiquidWarApp.this.start(primaryStage)));
                }
            }
        };
        timer.start();
        
        // CHANGEMENT DE SCÈNE
        primaryStage.setScene(sceneJeu);
    }



    private void spawnerEquipeAutour(Grille grille, Equipe equipe, int cx, int cy) {
        int particulesPlacees = 0;
        int nombreTotal = 1000;
        Random rand = new Random();

        while (particulesPlacees < nombreTotal) {
            double r = rand.nextDouble() * 35.0; 
            double angle = rand.nextDouble() * 2 * Math.PI;

            int px = (int) (cx + r * Math.cos(angle));
            int py = (int) (cy + r * Math.sin(angle));

            if (grille.inLimites(px, py) && grille.getParticule(px, py) == null) {
                grille.setParticule(px, py, new Particule(equipe, px, py, 100));
                particulesPlacees++;
            }
        }
    }

    public static void main(String[] args) { launch(); }
}