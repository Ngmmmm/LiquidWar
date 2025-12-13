package projet.cpoo5.controle.bot;

import java.util.List;
import java.util.Random;

import projet.cpoo5.modele.Equipe;
import projet.cpoo5.modele.Grille;

public abstract class Bot {

    protected final Equipe monEquipe;
    protected final Grille grille;
    protected final List<Equipe> adversaires; 
    protected final Random rand = new Random();

    public Bot(Equipe monEquipe, Grille grille, List<Equipe> adversaires) {
        this.monEquipe = monEquipe;
        this.grille = grille;
        this.adversaires = adversaires;
    }

    /**
     * Implementation des algos de déplacement du bot
     */
    public abstract void update();
}