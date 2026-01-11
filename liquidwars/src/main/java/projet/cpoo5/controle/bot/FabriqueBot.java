package projet.cpoo5.controle.bot;

import projet.cpoo5.modele.Equipe;
import projet.cpoo5.modele.Grille;

import java.util.List;

public final class  FabriqueBot {

    private FabriqueBot() {}

    public static Bot creerBot (NiveauBot niveau , Equipe equipe , Grille grille, List<Equipe> adverses){
        return switch (niveau){
            case FACILE -> new BotFacile(equipe, grille, adverses);
            case MOYEN -> new BotMedium(equipe, grille, adverses);
            case DIFFICILE -> new BotDifficile(equipe, grille, adverses);
        };
    }

    public enum NiveauBot { FACILE, MOYEN, DIFFICILE }

}
