package projet.cpoo5.controle.bot;

import java.util.List;
import java.util.Random;

import projet.cpoo5.modele.*;
import projet.cpoo5.util.Point;

/**
 * Classe abstraite représentant un bot qui contrôle une équipe.
 * Chaque type de bot (facile, moyen, difficile) hérite de cette classe
 * et implémente sa propre stratégie dans la méthode update().
 */
public sealed abstract class Bot permits BotFacile, BotMedium , BotDifficile {

    // L'équipe que ce bot contrôle
    protected final Equipe equipeControlee;

    // La grille de jeu sur laquelle le bot joue
    protected final Grille grilleJeu;

    // La liste des équipes adverses (pour détecter les ennemis)
    protected final List<Equipe> equipesAdverses;

    // Générateur de nombres aléatoires pour les décisions du bot
    protected final Random aleatoire = new Random();

    /**
     * Constructeur d'un bot.
     *
     * @param equipeControlee L'équipe que ce bot va contrôler
     * @param grilleJeu La grille de jeu
     * @param equipesAdverses La liste des équipes ennemies
     */
    protected Bot(Equipe equipeControlee, Grille grilleJeu, List<Equipe> equipesAdverses) {
        this.equipeControlee = equipeControlee;
        this.grilleJeu = grilleJeu;
        this.equipesAdverses = equipesAdverses;
    }

    // Méthode appelée à chaque frame

    /**
     * Méthode abstraite appelée à chaque tour de jeu.
     * Chaque bot doit implémenter sa propre stratégie ici.
     */
    public abstract void update();

    //  Méthodes utilitaires communes à tous les bots

    /**
     * Vérifie si une case est libre (pas d'obstacle et dans les limites).
     *
     * @param x Coordonnée X de la case à vérifier
     * @param y Coordonnée Y de la case à vérifier
     * @return true si la case est libre, false sinon
     */
    protected boolean estLibre(int x, int y) {
        return grilleJeu.inLimites(x, y) && !grilleJeu.isObstacle(x, y);
    }

    /**
     * Essaye de placer la cible de l'équipe à la position donnée.
     * Ne fait rien si la position n'est pas libre.
     *
     * @param x Coordonnée X de la nouvelle cible
     * @param y Coordonnée Y de la nouvelle cible
     */
    protected void essayerDePlacerCible(int x, int y) {
        if (estLibre(x, y)) {
            equipeControlee.setCible(new Point(x, y));
        }
    }

    /**
     * Déplace la cible de l'équipe d'UNE SEULE case vers un objectif donné,
     * en contournant intelligemment les obstacles grâce à l'algorithme BFS
     * (Breadth-First Search = recherche en largeur).
     *
     * Comment ça marche :
     * 1. On part de l'objectif et on calcule la distance minimale vers toutes les cases
     * 2. Depuis la position actuelle, on choisit la case voisine qui nous rapproche le plus
     * 3. On déplace la cible vers cette case voisine
     *
     * @param objectif Le point vers lequel on veut aller
     */
    protected void avancerCibleVersAvecBFS(Point objectif) {
        if (objectif == null) return;

        // Si l'objectif est sur un obstacle (par exemple le centroïde tombe sur un mur),
        // on cherche la case libre la plus proche
        if (!estLibre(objectif.x(), objectif.y())) {
            objectif = trouverCaseLibreProche(objectif, 25);
            if (objectif == null) return; // Aucune case libre trouvée
        }

        int largeur = grilleJeu.getLargeur();
        int hauteur = grilleJeu.getHauteur();

        // Tableau qui stocke la distance minimale de chaque case vers l'objectif
        int[][] distance = new int[largeur][hauteur];

        // On initialise toutes les distances à l'infini
        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                distance[x][y] = Integer.MAX_VALUE;
            }
        }

        // File pour le BFS (exploration en largeur)
        java.util.ArrayDeque<Point> file = new java.util.ArrayDeque<>();

        // L'objectif est à distance 0 de lui-même
        distance[objectif.x()][objectif.y()] = 0;
        file.add(objectif);

        // Les 4 directions possibles : droite, gauche, bas, haut
        int[][] DIR4 = {{1,0}, {-1,0}, {0,1}, {0,-1}};

        // BFS : on explore toutes les cases accessibles depuis l'objectif
        while (!file.isEmpty()) {
            Point courant = file.poll();
            int distanceProchaine = distance[courant.x()][courant.y()] + 1;

            // On regarde les 4 cases voisines
            for (int[] direction : DIR4) {
                int nouvelleX = courant.x() + direction[0];
                int nouvelleY = courant.y() + direction[1];

                // Si la case n'est pas libre, on l'ignore
                if (!estLibre(nouvelleX, nouvelleY)) continue;

                // Si on a trouvé un chemin plus court vers cette case
                if (distance[nouvelleX][nouvelleY] > distanceProchaine) {
                    distance[nouvelleX][nouvelleY] = distanceProchaine;
                    file.add(new Point(nouvelleX, nouvelleY));
                }
            }
        }

        // Maintenant qu'on a la carte des distances, on choisit la meilleure direction
        Point cibleActuelle = equipeControlee.getCible();
        int x = cibleActuelle.x();
        int y = cibleActuelle.y();
        int distanceActuelle = distance[x][y];

        // Si on est déjà à l'objectif ou si l'objectif est inatteignable
        if (distanceActuelle == Integer.MAX_VALUE || distanceActuelle == 0) return;

        // On cherche la case voisine qui a la distance minimale
        int meilleurX = x;
        int meilleurY = y;
        int meilleureDistance = distanceActuelle;

        for (int[] direction : DIR4) {
            int nouvelleX = x + direction[0];
            int nouvelleY = y + direction[1];

            if (!estLibre(nouvelleX, nouvelleY)) continue;

            int distanceVoisin = distance[nouvelleX][nouvelleY];

            // Si ce voisin nous rapproche de l'objectif
            if (distanceVoisin < meilleureDistance) {
                meilleureDistance = distanceVoisin;
                meilleurX = nouvelleX;
                meilleurY = nouvelleY;
            }
        }

        // Si on a trouvé une meilleure case, on y déplace la cible
        if (meilleureDistance < distanceActuelle) {
            essayerDePlacerCible(meilleurX, meilleurY);
        }
    }

    /**
     * Cherche une case libre proche autour d'un point donné.
     * Utile quand le point cible tombe sur un obstacle.
     * Recherche en spirale autour du point.
     *
     * @param p Le point de départ
     * @param rayonMax Le rayon maximal de recherche
     * @return La case libre la plus proche, ou null si aucune n'est trouvée
     */
    protected Point trouverCaseLibreProche(Point p, int rayonMax) {
        // On cherche en cercles concentriques de rayon croissant
        for (int rayon = 1; rayon <= rayonMax; rayon++) {

            // On vérifie les bords horizontaux du carré (haut et bas)
            for (int deltaX = -rayon; deltaX <= rayon; deltaX++) {
                int x = p.x() + deltaX;
                int yHaut = p.y() - rayon;
                int yBas = p.y() + rayon;

                if (estLibre(x, yHaut)) return new Point(x, yHaut);
                if (estLibre(x, yBas)) return new Point(x, yBas);
            }

            // On vérifie les bords verticaux du carré (gauche et droite)
            // On saute les coins car déjà vérifiés dans la boucle précédente
            for (int deltaY = -rayon + 1; deltaY <= rayon - 1; deltaY++) {
                int y = p.y() + deltaY;
                int xGauche = p.x() - rayon;
                int xDroite = p.x() + rayon;

                if (estLibre(xGauche, y)) return new Point(xGauche, y);
                if (estLibre(xDroite, y)) return new Point(xDroite, y);
            }
        }

        // Aucune case libre trouvée dans le rayon
        return null;
    }

    /**
     * Renvoie une case libre aléatoire sur la carte.
     * Fait plusieurs tentatives pour trouver une case libre.
     *
     * @param essais Nombre de tentatives maximales
     * @return Une case libre aléatoire, ou la cible actuelle si aucune n'est trouvée
     */
    protected Point caseLibreAleatoire(int essais) {
        for (int i = 0; i < essais; i++) {
            int x = aleatoire.nextInt(grilleJeu.getLargeur());
            int y = aleatoire.nextInt(grilleJeu.getHauteur());

            if (estLibre(x, y)) {
                return new Point(x, y);
            }
        }

        // Si on n'a pas trouvé de case libre, on retourne la position actuelle
        return equipeControlee.getCible();
    }

    /**
     * Calcule le centre de masse (centroïde) des particules d'une équipe.
     * C'est la position moyenne de toutes les particules de l'équipe.
     * Utile pour savoir "où se trouve" l'équipe en général.
     *
     * @param e L'équipe dont on veut calculer le centre de masse
     * @return Le point représentant le centre de masse de l'équipe
     */
    protected Point centreDeMasse(Equipe e) {
        long sommeX = 0;
        long sommeY = 0;
        long nombreParticules = 0;

        // On parcourt toute la grille
        for (int x = 0; x < grilleJeu.getLargeur(); x++) {
            for (int y = 0; y < grilleJeu.getHauteur(); y++) {
                Particule particule = grilleJeu.getParticule(x, y);

                // Si cette case contient une particule de l'équipe
                if (particule != null && particule.getEquipe() == e) {
                    sommeX += x;
                    sommeY += y;
                    nombreParticules++;
                }
            }
        }

        // Si l'équipe n'a aucune particule, on retourne sa cible actuelle
        if (nombreParticules == 0) {
            return e.getCible();
        }

        // On calcule la moyenne pour obtenir le centre de masse
        return new Point((int)(sommeX / nombreParticules), (int)(sommeY / nombreParticules));
    }
}