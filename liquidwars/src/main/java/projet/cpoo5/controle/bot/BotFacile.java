package projet.cpoo5.controle.bot;

import projet.cpoo5.modele.*;


public class BotFacile extends Bot {
    private static final int[][] DIR4 = {{1,0},{-1,0},{0,1},{0,-1}};
    public BotFacile(Equipe e, Grille g, java.util.List<Equipe> adv) { super(e,g,adv); }

    @Override public void update() {
        int[] d = DIR4[rand.nextInt(DIR4.length)];
        var c = monEquipe.getCible();
        trySetTarget(c.x() + d[0], c.y() + d[1]);
    }
}
