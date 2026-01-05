package projet.cpoo5.controle.bot;

import projet.cpoo5.modele.*;
import projet.cpoo5.util.Point;
import java.util.List;

public  final class BotDifficile extends Bot {
    private int cooldown = 0;
    private Point objectif = null;

    public BotDifficile(Equipe equipe, Grille grille, List<Equipe> adversaire) {
        super(equipe,grille,adversaire);
    }

    // Compte le nombre de particules appartenant à une équipe
    protected int compterParticules(Equipe e) {
        int n = 0;
        for (int x = 0; x < grille.getLargeur(); x++) {
            for (int y = 0; y < grille.getHauteur(); y++) {
                Particule p = grille.getParticule(x, y);
                if (p != null && p.getEquipe() == e) {
                    n++;
                }
            }
        }
        return n;
    }

    // Une équipe est "vivante" si elle a au moins 1 particule sur la grille
    protected boolean equipeVivante(Equipe e) {
        return compterParticules(e) > 0;
    }

    @Override
    public void update() {
        // S'il n'y a aucun adversaire on ne fait rien
        if (adversaires.isEmpty()) return;

        //Toutes les 10 frames , le bot recalcule sa stratégie
        cooldown-- ;
        if (cooldown <= 0 || objectif == null) {
            cooldown = 10;

            //Centre de masse des particules
            Point centre = centroidOf(monEquipe);

            Equipe meilleurCible = null;
            long meilleurDistance = Long.MAX_VALUE;

            //On cherche l'adversaire (vivant) le plus proche
            for (Equipe ennemi : adversaires) {

                if (!equipeVivante(ennemi)) continue;
                Point centreEnnemi = centroidOf(ennemi);
                long dx = (long) centreEnnemi.x() - centre.x();
                long dy = (long) centreEnnemi.y() - centre.y();
                long d2 = dx * dx + dy * dy; // distance au carré
                if (d2 < meilleurDistance) {
                    meilleurDistance = d2;
                    meilleurCible = ennemi;
                }
            }

            // Si aucun ennemi vivant trouvé -> on prend une case libre au hasard
            if (meilleurCible == null) {
                objectif = randomFreeCell(300);
            } else {
                // Sinon, on vise le centroïde du meilleur ennemi
                objectif = centroidOf(meilleurCible);
            }
        }
        // on avance vers l'objectif à chaque frame
        if (objectif != null) {
            stepTargetToward(objectif);
        }
    }
}

