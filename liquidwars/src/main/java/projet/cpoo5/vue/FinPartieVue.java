package projet.cpoo5.vue;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Classe utilitaire créant l'interface de fin de partie (overlay).
 * Affiche le message de victoire et un bouton de retour au menu.
 */
public final class FinPartieVue {

    /**
     * Crée le panneau visuel de fin de partie.
     * @param message Le texte à afficher
     * @param couleur La couleur du texte
     * @param actionRetourMenu L'action à exécuter quand on clique sur le bouton (Callback)
     * @return Une VBox prête à être affichée par-dessus le jeu
     */
    public static VBox creer(String message, Color couleur, Runnable actionRetourMenu) {
        
        //Le conteneur (Fond semi-transparent)
        VBox panneau = new VBox(20);
        panneau.setAlignment(Pos.CENTER);
        panneau.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-background-radius: 20;");
        panneau.setMaxSize(400, 300);

        //Le Message de victoire
        Label lblVictoire = new Label(message);
        lblVictoire.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        lblVictoire.setTextFill(couleur);
        lblVictoire.setStyle("-fx-effect: dropshadow( one-pass-box , white , 0, 0.0 , 0 , 1 );");

        // Bouton Retour
        Button btnMenu = new Button("Retour au Menu");
        btnMenu.setFont(Font.font("Arial", 20));
        btnMenu.setMinWidth(200);
        
        btnMenu.setOnAction(e -> actionRetourMenu.run());

        panneau.getChildren().addAll(lblVictoire, btnMenu);
        
        return panneau;
    }
}