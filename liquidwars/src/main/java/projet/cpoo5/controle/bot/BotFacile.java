package projet.cpoo5.controle.bot;

import java.util.ArrayList;
import java.util.List;

import projet.cpoo5.modele.Equipe;
import projet.cpoo5.modele.Grille;
import projet.cpoo5.util.Point;

/**
 * Bot de niveau facile : stratégie simple.
 * Il est basé sur des demi-tours
 *
 */
public final class BotFacile extends Bot {

    /** Dernière position de la cible : sert à éviter les demi-tours en boucle */
    private Point derniereCible = null;

    /** Petit objectif aléatoire (pas un ennemi) */
    private Point objectif = null;

    /** Compteur : toutes les X frames, on choisit un nouvel objectif */
    private int delaiChangementObjectif = 0;


    /**
     * Constructeur du bot facile.
     *
     * @param equipeControlee L'équipe contrôlée par ce bot
     * @param grilleJeu La grille de jeu
     * @param equipesAdverses La liste des équipes adverses
     */
    public BotFacile(Equipe equipeControlee, Grille grilleJeu, List<Equipe> equipesAdverses) {
        super(equipeControlee, grilleJeu, equipesAdverses);
    }

    @Override
    public void update() {

        // 1) De temps en temps , choisir un nouvel objectif aléatoire
        delaiChangementObjectif--;
        if (delaiChangementObjectif <= 0 || objectif == null) {
            delaiChangementObjectif = 60;          // facile = objectif change rarement
            objectif = caseLibreAleatoire(300);     // objectif random dans la map
        }

        // 2) Faire 2 pas max : plus visible
        for (int pas = 0; pas < 2; pas++) {

            Point cibleActuelle = equipeControlee.getCible();

            // Si c'est la 1ère fois, on init la mémoire
            if (derniereCible == null) {
                derniereCible = cibleActuelle;
            }

            // 3) Déplacement simple : on avance vers l'objectif avec BFS
            // (BFS = contourne obstacles , car l'objectif est random)
            Point avant = cibleActuelle;
            avancerCibleVersAvecBFS(objectif);
            Point apres = equipeControlee.getCible();

            // 4) Si on n'a pas bougé (bloqué ou aucun chemin), on fait un petit pas aléatoire
            if (apres.equals(avant)) {
                pasAleatoireSansDemiTour();
            }

            // 5) Mettre à jour la mémoire de la dernière position
            derniereCible = avant;
        }
    }

    /**
     * Fait un pas aléatoire, mais évite autant que possible de revenir sur la case précédente.
     */
    private void pasAleatoireSansDemiTour() {
        Point cibleActuelle = equipeControlee.getCible();

        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        List<Point> voisinsLibres = new ArrayList<>();
        List<Point> voisinsLibresSansDemiTour = new ArrayList<>();

        // On liste les voisins libres
        for (int[] d : directions) {
            int nx = cibleActuelle.x() + d[0];
            int ny = cibleActuelle.y() + d[1];
            if (estLibre(nx, ny)) {
                Point v = new Point(nx, ny);
                voisinsLibres.add(v);

                // On garde aussi ceux qui ne sont pas la case précédente (anti demi-tour)
                if (derniereCible == null || !v.equals(derniereCible)) {
                    voisinsLibresSansDemiTour.add(v);
                }
            }
        }

        if (voisinsLibres.isEmpty()) return;

        // Priorité : voisins sans demi-tour ; sinon on prend quand même un voisin libre
        List<Point> choix = voisinsLibresSansDemiTour.isEmpty() ? voisinsLibres : voisinsLibresSansDemiTour;

        Point choisi = choix.get(aleatoire.nextInt(choix.size()));
        essayerDePlacerCible(choisi.x(), choisi.y());
    }
}