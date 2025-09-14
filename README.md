# Système de gestion de restaurant en Java

Ce projet est un système de gestion de restaurant développé en Java, utilisant Java Swing pour l'interface graphique (GUI) et une base de données MySQL pour la persistance des données. Il a été créé dans le cadre d'un mini-projet universitaire, l'objectif étant de démontrer les principes de la programmation orientée objet (POO) et l'interaction avec une base de données.

## Fonctionnalités Clés

Le système est conçu pour gérer les opérations principales d'un restaurant, en s'adressant à trois types d'utilisateurs différents : le client, la serveuse et le cuisinier.

### Fonctionnalités Client

  - **Parcourir le menu :** Afficher la liste des menus et des plats disponibles.
  - **Commander des plats :** Sélectionner et commander des plats à partir du menu.

### Fonctionnalités Serveuse

  - **Parcourir le menu :** Afficher les menus et les plats.
  - **Gérer les commandes :** Visualiser les commandes reçues et en cours de préparation.
  - **Générer une facture :** Créer et générer des factures pour les commandes terminées.

### Fonctionnalités Cuisinier

  - **Gestion des menus :** Ajouter, modifier et supprimer des plats du menu.
  - **Traitement des commandes :** Afficher les nouvelles commandes, commencer ou annuler leur préparation, et les marquer comme prêtes.
  - **Suivi des commandes :** Surveiller les commandes en cours de préparation.

## Technologies Utilisées

  - **Frontend :** Java Swing
  - **Backend/Logique :** Java
  - **Base de données :** MySQL
  - **Connectivité de la base de données :** JDBC
  - **Sécurité des mots de passe :** BCrypt pour le hachage des mots de passe

## Structure du Projet

Le projet suit une structure modulaire pour séparer les préoccupations, en utilisant principalement le modèle de conception **Modèle-Vue-Contrôleur (MVC)**.

  - **`dao/` (Data Access Objects) :** Ce paquet contient les classes (`CommandeDAO`, `FactureDAO`, `PlatDAO`, `MenuDAO`, `UtilisateurDAO`, etc.) responsables de toutes les interactions avec la base de données. Elles isolent la logique de l'application des détails de la base de données. `SingletonConnection.java` gère une instance de connexion unique à la base de données pour l'application.
  - **`model/` (Modèles) :** Ce paquet inclut les modèles de données (`Commande`, `Facture`, `Plat`, `Menu`, `Utilisateur`, `Client`, `Serveuse`, `Cuisinier`) qui représentent la logique métier et la structure des données de l'application.
  - **`controller/` (Contrôleurs) :** La classe `UtilisateurController.java` gère la logique métier liée à l'utilisateur, comme l'enregistrement et l'authentification.
  - **`gui/` (Interface Graphique) :** Ce paquet contient toutes les classes Java Swing qui définissent les interfaces utilisateur pour les différents rôles (`Login.java`, `Signup.java`, `ClientMenuInterface.java`, `ServeuseInterface.java`, `CuisinierInterface.java`, etc.).

## Schéma de la Base de Données

Basé sur les classes DAO et modèle, voici le schéma relationnel pour le projet :

  - **`utilisateur` :**

      - `idUtilisateur` (Clé primaire)
      - `nomUtilisateur`
      - `motDePasse` (Mot de passe haché)
      - `role` (par exemple, 'client', 'serveuse', 'cuisinier')

  - **`menu` :**

      - `idMenu` (Clé primaire)
      - `nom`
      - `description`

  - **`plat` :**

      - `idPlat` (Clé primaire)
      - `nom`
      - `description`
      - `prix`
      - `idMenu` (Clé étrangère vers `menu.idMenu`)
      - `imagePlat` (BLOB pour l'image)

  - **`commande` :**

      - `idCommande` (Clé primaire)
      - `idClient` (Clé étrangère vers `utilisateur.idUtilisateur` avec le rôle 'client')
      - `dateCommande`
      - `statut` (par exemple, 'en\_attente', 'en\_traitement', 'Terminée', 'Annulée')
      - `montantTotal`

  - **`facture` :**

      - `idFacture` (Clé primaire)
      - `idCommande` (Clé étrangère vers `commande.idCommande`)
      - `dateFacture`
      - `montantTotal`

## Configuration et Installation

1.  **Configuration de la base de données :**

      - Créez une base de données MySQL.
      - Exécutez les commandes SQL nécessaires pour créer les tables basées sur le schéma ci-dessus.
      - Vous devrez configurer vos propriétés de connexion à la base de données dans un fichier `conf.properties`, comme utilisé par `SingletonConnection.java`.

2.  **Cloner le dépôt :**

    ```bash
    git clone [votre-url-de-dépôt]
    cd [votre-nom-de-dépôt]
    ```

3.  **Dépendances :**

      - Ajoutez le pilote MySQL Connector/J à votre projet.
      - Ajoutez la bibliothèque jBCrypt pour le hachage des mots de passe.

4.  **Exécuter l'application :**

      - Compilez les fichiers Java.
      - Exécutez la méthode `main` dans `gui.Login.java` pour démarrer l'application.
