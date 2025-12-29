package projet.cpoo5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import projet.cpoo5.controle.InputHandler;
import projet.cpoo5.controle.bot.Bot;
import projet.cpoo5.controle.bot.BotDifficile;
import projet.cpoo5.controle.bot.BotFacile;
import projet.cpoo5.controle.bot.BotMedium;
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
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        
        MenuVue menu = new MenuVue(stage, this);
        menu.afficher();
    }

    @Override
    public void stop() throws Exception {
        executor.shutdown();
        super.stop();
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

        List<Equipe> equipes = new ArrayList<>();
        List<Bot> bots = new ArrayList<>();
        List<Equipe> adversairesB = new ArrayList<>();
        List<Equipe> adversairesJ = new ArrayList<>();
        List<Equipe> adversairesV = new ArrayList<>();

        if (modeSolo) {
            // MODE 4 JOUEURS 
            Equipe rouge = new Equipe("ROUGE",Color.RED, new Point(40, 40));

            equipes.add(rouge);
            adversairesB.add(rouge);
            adversairesV.add(rouge);
            adversairesJ.add(rouge);

            Equipe bleu = new Equipe("BLEU",Color.BLUE, new Point(wGrille - 40, 40));
            equipes.add(bleu);
            adversairesV.add(bleu);
            adversairesJ.add(bleu);
           

            Equipe vert = new Equipe("VERT",Color.LIME, new Point(wGrille - 40, hGrille - 40));
            equipes.add(vert);
            adversairesB.add(vert);
            adversairesJ.add(vert);
      

            Equipe jaune = new Equipe("JAUNE",Color.YELLOW, new Point(40, hGrille - 40));
            equipes.add(jaune);
            adversairesB.add(jaune);
            adversairesV.add(jaune);
            
             bots.add(new BotDifficile(bleu, grille, adversairesB));
             bots.add(new BotFacile(vert, grille, adversairesV));
             bots.add(new BotMedium(jaune, grille, adversairesJ));

        } else {
            // MODE CLASSIQUE 2 JOUEURS 
            Equipe rouge = new Equipe("ROUGE",Color.RED, new Point(40, 40));
            Equipe bleu = new Equipe("BLEU",Color.BLUE, new Point(wGrille - 40, hGrille - 40));
            equipes.add(rouge);
            equipes.add(bleu);
        }

        // SPAWN DES PARTICULES
        for (Equipe e : equipes) {
            spawnerEquipeAutour(grille, e, e.getCible().x(), e.getCible().y());
        }
        
        BFS bfs = new BFS();

        //VUE JEU
        GameCanvas canvas = new GameCanvas(grille, equipes);
        StackPane root = new StackPane(canvas); 
        Scene sceneJeu = new Scene(root, 800, 600); 

        canvas.widthProperty().bind(sceneJeu.widthProperty());
        canvas.heightProperty().bind(sceneJeu.heightProperty());

        Equipe joueur2 = (modeSolo) ? null : equipes.get(1);
        new InputHandler(sceneJeu, canvas, equipes.get(0), joueur2,config.largeur(),config.hauteur(), grille);

        //BOUCLE DE JEU
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!primaryStage.isShowing()) this.stop();

                List<Future<?>> botsFutures = new ArrayList<>();
                for (Bot bot : bots) {
                    Future<?> f = executor.submit(() -> bot.update());
                    botsFutures.add(f);
                }
                for (Future<?> f : botsFutures) {
                    try {
                        f.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                //CALCUL BFS (Pour toutes les équipes)
                List<Future<GrilleGradient>> resultatsFuturs = new ArrayList<>();

                for (Equipe e : equipes) {
                    Future<GrilleGradient> f = executor.submit(() -> bfs.calcul(grille, e));
                    resultatsFuturs.add(f);
                }
                for (int i = 0; i < equipes.size(); i++) {
                    try {
                        GrilleGradient resultat = resultatsFuturs.get(i).get();
                        equipes.get(i).setGrilleGradient(resultat);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                int[] counts = new int[equipes.size()];
                List<Particule> aDeplacer = new ArrayList<>();

                for(int x=0; x<grille.getLargeur(); x++) {
                    for(int y=0; y<grille.getHauteur(); y++) {
                        Particule p = grille.getParticule(x, y);
                        if (p != null) {
                            aDeplacer.add(p);
                            int index = equipes.indexOf(p.getEquipe());
                            if (index != -1) counts[index]++;
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
                canvas.setScores(counts);
                canvas.draw();

                int vivants = 0;
                int gagnantIndex = -1;
                for (int i=0; i<counts.length; i++) {
                    if (counts[i] > 0) {
                        vivants++;
                        gagnantIndex = i;
                    }
                }

                if (vivants <= 1 && aDeplacer.size() > 0) {
                    this.stop();
                    String msg = "VICTOIRE DES " + equipes.get(gagnantIndex).getNom();
                    root.getChildren().add(FinPartieVue.creer(msg, equipes.get(gagnantIndex).getCouleur(), () -> LiquidWarApp.this.start(primaryStage)));
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