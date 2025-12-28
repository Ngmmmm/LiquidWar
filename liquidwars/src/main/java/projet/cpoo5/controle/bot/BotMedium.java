package projet.cpoo5.controle.bot;

import projet.cpoo5.modele.*;
import java.util.List;
public class BotMedium extends Bot {
    private projet.cpoo5.util.Point goal = null;
    private int cooldown = 0;

    public BotMedium(Equipe e, Grille g, List<Equipe> adv) { super(e,g,adv); }

    @Override public void update() {
        if (cooldown-- <= 0 || goal == null) {
            cooldown = 40;
            goal = randomFreeCell(300);
        }
        stepTargetToward(goal);
    }
}
