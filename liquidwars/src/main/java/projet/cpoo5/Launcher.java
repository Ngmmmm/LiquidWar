package projet.cpoo5;
/**
 * Point d'entrée technique de l'application.
 * Cette classe permet de lancer l'application JavaFX sans conflit de modules
 * lors de la création du JAR exécutable.
 */
public class Launcher {

    /**
     * Méthode principale qui délègue le démarrage à LiquidWarApp.
     * @param args Arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        LiquidWarApp.main(args);
    }
}