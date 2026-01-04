package projet.cpoo5.controle.bot;

import projet.cpoo5.modele.*;

public  final class BotDifficile extends Bot {
    private int cooldown = 0;
    private projet.cpoo5.util.Point goal = null;

    public BotDifficile(Equipe e, Grille g, java.util.List<Equipe> adv) { super(e,g,adv); }

    @Override public void update() {
        if (adversaires.isEmpty()) return;

        if (cooldown-- <= 0 || goal == null) {
            cooldown = 15;

            var myC = centroidOf(monEquipe);

            Equipe best = null;
            long bestDist = Long.MAX_VALUE;

            for (Equipe enemy : adversaires) {
                var ec = centroidOf(enemy);
                long dx = ec.x() - myC.x();
                long dy = ec.y() - myC.y();
                long d2 = dx*dx + dy*dy;
                if (d2 < bestDist) { bestDist = d2; best = enemy; }
            }

            goal = centroidOf(best);
        }

        stepTargetToward(goal);
    }
}
