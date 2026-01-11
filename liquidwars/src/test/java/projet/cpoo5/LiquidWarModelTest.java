package projet.cpoo5;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import projet.cpoo5.modele.*;
import projet.cpoo5.util.*;
import javafx.scene.paint.Color;

/**
 * Classe de tests unitaires pour valider la logique métier du jeu Liquid War.
 * Couvre : Configuration, Physique des particules, Logique de grille et Pathfinding.
 */
public class LiquidWarModelTest {

    /**
     * Teste que la configuration refuse bien les valeurs incohérentes.
     * Critère : Robustesse (ne pas crasher si config invalide).
     */
    @Test
    public void testGameConfigValidation() {
        // Cas nominal : Tout va bien
        assertDoesNotThrow(() -> new GameConfig(100, 100, 10, 100, 0, 5, 2));

        // Cas d'erreur : Dimensions négatives
        assertThrows(IllegalArgumentException.class, () -> 
            new GameConfig(-10, 100, 10, 100, 0, 5, 2),
            "Une largeur négative doit lever une exception"
        );

        // Cas d'erreur : Énergie négative ou nulle
        assertThrows(IllegalArgumentException.class, () -> 
            new GameConfig(100, 100, 10, -50, 0, 5, 2),
            "Une énergie initiale négative doit être interdite"
        );
    }

    /**
     * Teste la logique de bornage de l'énergie des particules.
     * Critère : Règles métier (Max 100, Min 0).
     */
    @Test
    public void testParticuleEnergie() {
        Equipe equipe = new Equipe("Test", Color.RED, new Point(0,0));
        Particule p = new Particule(equipe, 10, 10, 50.0f);

        // 1. Test Ajout normal
        p.addEnergie(20);
        assertEquals(70.0f, p.getEnergie(), 0.01, "50 + 20 devrait faire 70");

        // 2. Test Plafond (Max 100)
        p.addEnergie(200); // On ajoute énormément
        assertEquals(100.0f, p.getEnergie(), 0.01, "L'énergie ne doit pas dépasser 100");

        // 3. Test Retrait normal
        p.removeEnergie(50);
        assertEquals(50.0f, p.getEnergie(), 0.01, "100 - 50 devrait faire 50");

        // 4. Test Plancher (Min 0)
        p.removeEnergie(200); // On enlève tout
        assertEquals(0.0f, p.getEnergie(), 0.01, "L'énergie ne doit pas être négative");
    }

    /**
     * Teste les limites de la grille et la détection d'obstacles.
     * Critère : Cohérence spatiale.
     */
    @Test
    public void testGrilleLimites() {
        GameConfig config = new GameConfig(10, 10, 0, 100, 0, 5, 2);
        Grille grille = new Grille(config);

        // Test limites intérieures
        assertTrue(grille.inLimites(0, 0), "0,0 devrait être dans la grille");
        assertTrue(grille.inLimites(9, 9), "9,9 devrait être dans la grille (taille - 1)");

        // Test limites extérieures
        assertFalse(grille.inLimites(-1, 0), "X négatif interdit");
        assertFalse(grille.inLimites(0, -1), "Y négatif interdit");
        assertFalse(grille.inLimites(10, 10), "10,10 est hors limites pour une grille 10x10");
    }

    /**
     * Teste que le BFS calcule bien une distance 0 sur la cible.
     * Critère : Algorithmique (Pathfinding).
     */
    @Test
    public void testBFSCoherence() {
        // Mise en place
        GameConfig config = new GameConfig(20, 20, 0, 100, 0, 5, 2);
        Grille grille = new Grille(config);
        
        // On force une zone vide autour de la cible pour éviter qu'elle soit dans un mur aléatoire
        grille.setWall(10, 10, false); 
        
        Point cible = new Point(10, 10);
        Equipe equipe = new Equipe("Bot", Color.BLUE, cible);
        
        // Exécution BFS
        BFS bfs = new BFS();
        GrilleGradient gradient = bfs.calcul(grille, equipe);

        // Vérification
        assertNotNull(gradient, "Le gradient ne doit pas être null");
        assertEquals(0, gradient.getDistance(10, 10), "La distance sur la cible doit être 0");
        
        // Vérification d'une case voisine (si elle n'est pas un mur)
        // La distance doit être petite (ex: 1) si le chemin est direct
        if (!grille.isObstacle(11, 10)) {
            int distVoisine = gradient.getDistance(11, 10);
            assertTrue(distVoisine > 0, "Les voisins doivent avoir une distance > 0");
            assertTrue(distVoisine < 1000, "Le voisin direct doit être accessible");
        }
    }
}