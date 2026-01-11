package projet.cpoo5.controle.bot;

import java.util.List;

import projet.cpoo5.modele.Equipe;
import projet.cpoo5.modele.Grille;
import projet.cpoo5.util.Point;

/**
 * Bot de niveau moyen : exploration méthodique avec pathfinding.
 * Ce bot choisit un objectif aléatoire et s'y dirige intelligemment
 * en contournant les obstacles grâce à l'algorithme BFS.
 */
public final class BotMedium extends Bot {

    // L'objectif actuel vers lequel le bot se dirige
    private Point objectif = null;

    // Compteur pour éviter de recalculer l'objectif à chaque frame
    private int delaiRecalcul = 0;

    /**
     * Constructeur du bot moyen.
     *
     * @param equipeControlee L'équipe contrôlée par ce bot
     * @param grilleJeu La grille de jeu
     * @param equipesAdverses La liste des équipes adverses
     */
    protected BotMedium(Equipe equipeControlee, Grille grilleJeu, List<Equipe> equipesAdverses) {
        super(equipeControlee, grilleJeu, equipesAdverses);
    }

    /**
     * Stratégie du bot moyen, appelée à chaque tour.
     *
     * Stratégie :
     * 1. Tous les 40 tours, choisit un nouvel objectif aléatoire sur la carte
     * 2. À chaque tour, avance d'une case vers cet objectif
     * 3. Utilise le BFS pour contourner intelligemment les obstacles
     *
     * Ce bot est plus intelligent que le bot facile car il a un but
     * et sait contourner les murs, mais il reste prévisible car
     * il ne réagit pas aux adversaires.
     */
    @Override
    public void update() {
        // Décrémenter le compteur de délai
        delaiRecalcul--;

        // Tous les 40 tours (ou si on n'a pas encore d'objectif),
        // on choisit un nouvel objectif aléatoire
        if (delaiRecalcul <= 0 || objectif == null) {
            delaiRecalcul = 40; // Réinitialiser le compteur

            // Choisir une case libre aléatoire sur la carte (300 tentatives max)
            objectif = caseLibreAleatoire(300);
        }

        // À chaque tour, avancer d'une case vers l'objectif
        // en contournant les obstacles grâce au BFS
        avancerCibleVersAvecBFS(objectif);
    }
}