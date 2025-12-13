package projet.cpoo5.vue;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import projet.cpoo5.LiquidWarApp;

public class MenuVue {

    private final Stage stage;
    private final LiquidWarApp app; 

    public MenuVue(Stage stage, LiquidWarApp app) {
        this.stage = stage;
        this.app = app;
    }

    public void afficher() {
        //Titre
        Label titre = new Label("LIQUID WAR");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        titre.setTextFill(Color.DARKBLUE);

        //Boutons
        Button btnMulti = creerBouton("1v1 MultiLocal");
        btnMulti.setOnAction(e -> {
            app.lancerPartie(false); 
        });

        Button btnSolo = creerBouton("Solo vs Bot");
        btnSolo.setOnAction(e -> app.lancerPartie(true));

        Button btnQuitter = creerBouton("Quitter");
        btnQuitter.setOnAction(e -> System.exit(0));

        //Layout
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(titre, btnMulti, btnSolo, btnQuitter);

        //Affichage
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Liquid War");
        stage.show();
    }

    private Button creerBouton(String texte) {
        Button btn = new Button(texte);
        btn.setMinWidth(200);
        btn.setMinHeight(40);
        btn.setFont(Font.font("Arial", 16));
        return btn;
    }
}