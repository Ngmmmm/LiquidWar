package projet.cpoo5.controle.bot;

import projet.cpoo5.modele.*;
import projet.cpoo5.util.Point;
import java.util.List;

/**
 * Bot de niveau difficile : stratégie agressive.
 * Ce bot analyse la situation tous les 10 tours et attaque l'adversaire le plus proche.
 * Il utilise le BFS pour contourner intelligemment les obstacles.
 */
public final class BotDifficile extends Bot {

    // Compteur pour éviter de recalculer la stratégie à chaque frame
    private int tempsDAttente = 0;

    // L'objectif actuel vers lequel le bot se dirige
    private Point objectif = null;

    /**
     * Constructeur du bot difficile.
     *
     * @param equipe L'équipe contrôlée par ce bot
     * @param grille La grille de jeu
     * @param adversaire La liste des équipes adverses
     */
    public BotDifficile(Equipe equipe, Grille grille, List<Equipe> adversaire) {
        super(equipe, grille, adversaire);
    }

    /**
     * Compte le nombre de particules appartenant à une équipe donnée.
     * Parcourt toute la grille et compte les particules de cette équipe.
     *
     * @param e L'équipe dont on veut compter les particules
     * @return Le nombre de particules de cette équipe sur la grille
     */
    protected int compterParticules(Equipe e) {
        int nombreParticules = 0;

        // On parcourt toute la grille
        for (int x = 0; x < grilleJeu.getLargeur(); x++) {
            for (int y = 0; y < grilleJeu.getHauteur(); y++) {
                Particule particule = grilleJeu.getParticule(x, y);

                // Si cette case contient une particule de l'équipe recherchée
                if (particule != null && particule.getEquipe() == e) {
                    nombreParticules++;
                }
            }
        }

        return nombreParticules;
    }

    /**
     * Vérifie si une équipe est encore en vie.
     * Une équipe est vivante si elle possède au moins 1 particule sur la grille.
     *
     * @param e L'équipe à vérifier
     * @return true si l'équipe a au moins une particule, false sinon
     */
    protected boolean equipeVivante(Equipe e) {
        return compterParticules(e) > 0;
    }

    /**
     * Stratégie du bot difficile, appelée à chaque tour.
     *
     * Stratégie :
     * 1. Tous les 10 tours, recalcule l'adversaire le plus proche
     * 2. Vise le centre de masse de cet adversaire
     * 3. Avance vers cet objectif en utilisant le BFS pour contourner les obstacles
     */
    @Override
    public void update() {
        // Si aucun adversaire n'existe, on ne fait rien
        if (equipesAdverses.isEmpty()) return;

        // Décrémenter le compteur de temps d'attente
        tempsDAttente--;

        // Tous les 10 tours (ou si on n'a pas d'objectif), on recalcule la stratégie
        if (tempsDAttente <= 0 || objectif == null) {
            tempsDAttente = 10; // Réinitialiser le compteur

            // Calculer le centre de masse de notre propre équipe
            Point notreCentre = centreDeMasse(equipeControlee);

            // Variables pour trouver l'ennemi le plus proche
            Equipe ennemiLePlusProche = null;
            long distanceMinimale = Long.MAX_VALUE;

            // Parcourir tous les adversaires pour trouver le plus proche
            for (Equipe ennemi : equipesAdverses) {

                // Ignorer les équipes mortes (sans particules)
                if (!equipeVivante(ennemi)) continue;

                // Calculer le centre de masse de cet ennemi
                Point centreEnnemi = centreDeMasse(ennemi);

                // Calculer la distance au carré (plus rapide que la vraie distance)
                long deltaX = (long) centreEnnemi.x() - notreCentre.x();
                long deltaY = (long) centreEnnemi.y() - notreCentre.y();
                long distanceCarree = deltaX * deltaX + deltaY * deltaY;

                // Si cet ennemi est plus proche que le précédent
                if (distanceCarree < distanceMinimale) {
                    distanceMinimale = distanceCarree;
                    ennemiLePlusProche = ennemi;
                }
            }

            // Définir l'objectif en fonction du résultat
            if (ennemiLePlusProche == null) {
                // Aucun ennemi vivant trouvé -> exploration aléatoire
                objectif = caseLibreAleatoire(300);
            } else {
                // Attaquer l'ennemi le plus proche en visant son centre de masse
                objectif = centreDeMasse(ennemiLePlusProche);
            }
        }

        // À chaque tour, avancer d'une case vers l'objectif en utilisant le BFS
        if (objectif != null) {
            avancerCibleVersAvecBFS(objectif);
        }
    }
}