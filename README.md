# 💧 Liquid War - Projet CPOO5

**Liquid War** est un jeu de stratégie et d'action multijoueur (local ou contre IA) où vous contrôlez une armée de particules liquides. Le but est de submerger les armées adverses en les encerclant pour convertir leurs particules à votre couleur.

Ce projet a été réalisé dans le cadre du cours de Conception et Programmation Orientée Objet (CPOO5).

---

## 🚀 Installation et Lancement

Ce projet utilise **Maven** et **Java 21** (avec JavaFX).

### Pré-requis
* JDK 21 installé.
* Maven installé et configuré dans le PATH.

### Commandes rapides

**1. Compiler et Lancer le jeu :**
```bash
mvn clean javafx:run
```

**2. Lancer les tests unitaires :**
```bash
mvn test
```

---

## 🎮 Fonctionnalités Implémentées

### Modes de Jeu
* **Solo vs Bots :** Un mode "Arène" à 4 équipes. Le joueur (Rouge) affronte 3 Intelligences Artificielles de niveaux différents (Facile, Moyen, Difficile).
* **1v1 Local :** Deux joueurs humains s'affrontent sur le même clavier/souris (ZQSD/Flèches).

### Mécaniques
* **Génération Procédurale :** Chaque partie propose une carte unique générée aléatoirement (algorithme de bruit lissé) avec suppression des zones inaccessibles.
* **Physique des Fluides :** Les particules se déplacent de manière organique en suivant des champs de vecteurs (Flow Field).
* **Système de Combat :** Une mécanique d'attaque/défense où les particules "mangent" l'énergie adverse jusqu'à la conversion.

---

## 🏗️ Architecture et Choix Techniques

Le projet respecte une architecture **MVC (Modèle-Vue-Contrôleur)** stricte et utilise plusieurs Design Patterns pour assurer modularité et performance.

### 1. Multithreading
Pour gérer des milliers de particules sans ralentir l'interface graphique (JavaFX Application Thread), nous avons parallélisé les calculs lourds.
* **Pattern Worker / Thread Pool :** Utilisation d'un `ExecutorService` (via `Executors.newFixedThreadPool`).
* **Fonctionnement :** À chaque frame, l'IA des bots et le calcul du Pathfinding (BFS) sont envoyés à des threads séparés via des `Future<?>`. Le thread principal attend la fin des calculs (synchronisation) avant de mettre à jour l'affichage.

### 2. Algorithme de Pathfinding
Utiliser A* pour 4000 particules serait trop lent.
* **Solution :** Utilisation d'un **BFS inversé (Breadth-First Search)** partant de la cible.
* Cela génère une **Carte de Gradient** (`GrilleGradient`) : chaque case connaît sa distance vers l'objectif.
* Les particules n'ont plus qu'à "rouler" vers la case voisine ayant la plus petite valeur (Descente de gradient).

### 3. Design Patterns
* **Pattern Strategy (Bots) :** La classe abstraite `Bot` définit le contrat. Les stratégies concrètes implémentent la méthode `update()` différemment.
* **Pattern Proxy / Interface :** La Vue (`GameCanvas`) ne manipule jamais directement les objets `Grille` ou `Particule` en écriture. Elle passe par des interfaces de lecture seule `IntGrille` et `IntParticule` pour empêcher toute modification accidentelle du modèle depuis l'affichage.
* **Pattern Observer :** Utilisation de l'`AnimationTimer` de JavaFX pour la boucle de jeu.

---

## 🧪 Tests et Qualité

* **Configuration :** Vérification des bornes.
* **Logique Métier :** Gestion de l'énergie et conversion des particules.
* **Pathfinding :** Validation mathématique du BFS.

---

## 📂 Structure du Projet

```text
src/main/java/projet/cpoo5
├── controle          # Gestion des entrées (Clavier/Souris)
│   └── bot           # IA (Stratégies)
├── modele            # Logique métier (Grille, Particule, BFS)
├── vue               # Affichage JavaFX (Canvas, Menus)
├── util              # Classes utilitaires (Point, Config)
└── LiquidWarApp.java # Point d'entrée et Boucle principale
```

---

## 👥 Auteurs

Projet réalisé par **[Thiam Adama -- Gregoire-Mellier Noa]**.