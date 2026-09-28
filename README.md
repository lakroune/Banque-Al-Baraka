# Banque Al Baraka

Application **console Java** (JDBC + PostgreSQL) de gestion bancaire : clients, comptes
(courant / épargne), transactions (versement, retrait, virement), historique, analyses et alertes.

## Sommaire

- [Fonctionnalités](#fonctionnalités)
- [Prérequis](#prérequis)
- [Base de données](#base-de-données)
- [Compilation et exécution](#compilation-et-exécution)
- [Architecture](#architecture)
- [Structure](#structure)
- [Menus](#menus)
- [Modèle de données](#modèle-de-données)
- [Limites connues](#limites-connues)

## Fonctionnalités

| Domaine | Détail |
| --- | --- |
| Clients | Créer, consulter (par ID), supprimer |
| Comptes | Créer un compte **courant** (découvert autorisé) ou **épargne** (taux d'intérêt), consulter par numéro, supprimer |
| Transactions | **Versement**, **retrait**, **virement** ; saisie du lieu et horodatage automatique (`LocalDateTime`) |
| Règles métier | Montant strictement positif, solde insuffisant refusé (le découvert autorisé est pris en compte pour un compte courant), comptes source et destination obligatoirement différents pour un virement |
| Atomicité | Chaque opération met à jour le(s) solde(s) **et** enregistre la transaction dans une même transaction SQL (`commit` / `rollback`) |
| Historique | Toutes les transactions d'un compte (en tant que source **ou** destination), triées par date décroissante |
| Analyses | Top 5 clients par solde total, comptes inactifs (aucune opération depuis 12 mois) |
| Suppression | Supprimer un client supprime ses comptes et leurs transactions en cascade |

## Prérequis

- **JDK 17** — le code utilise `Stream.toList()` et `Optional.isEmpty()` (Java 16 minimum) ;
  la configuration IntelliJ du projet cible JDK 17.
- **PostgreSQL** accessible en local.
- Pilote JDBC fourni : `lib/postgresql.jar`.

## Base de données

1. Créer la base :

   ```sql
   CREATE DATABASE bankab;
   ```

2. Exécuter le script de schéma (il **supprime puis recrée** les tables `clients`, `comptes`, `transactions`) :

   ```bat
   psql -U postgres -h localhost -p 5433 -d bankab -f sql/shema.sql
   ```

   > - Pour une base **existante** dont `date_transaction` est encore en `DATE`, décommenter
   >   l'`ALTER TABLE ... TYPE TIMESTAMP` présent dans le script avant exécution (ou recréer la base).
   > - Le script se termine par des requêtes de contrôle (`SELECT` / `UPDATE`) utilisées pendant
   >   le développement. Certaines font référence à une ancienne table `compte` et provoquent des
   >   erreurs dans `psql` : elles n'affectent pas le schéma et peuvent être ignorées.

3. Vérifier ou ajuster la connexion dans `src/util/DatabaseConnection.java` :

   | Paramètre | Valeur par défaut |
   | --- | --- |
   | URL | `jdbc:postgresql://localhost:5433/bankab` |
   | Utilisateur | `postgres` |
   | Mot de passe | `123456` |

## Compilation et exécution

### Windows (cmd)

Le chemin du projet contient un espace : compiler **depuis `src`** avec des chemins
relatifs évite les erreurs de classpath.

```bat
cd src
javac -cp "..\lib\postgresql.jar" -d "..\bin" Main.java UI\*.java models\*.java DAOS\*.java services\*.java util\*.java exceptions\*.java
cd ..
java -cp "bin;lib\postgresql.jar" Main
```

### Linux / macOS

```bash
cd src
javac -cp "../lib/postgresql.jar" -d "../bin" Main.java UI/*.java models/*.java DAOS/*.java services/*.java util/*.java exceptions/*.java
cd ..
java -cp "bin:lib/postgresql.jar" Main
```

### Erreur `UnsupportedClassVersionError`

Si `java` échoue avec `UnsupportedClassVersionError`, le JRE du `PATH` est plus ancien
que la version de compilation ; pointer le `PATH` vers le JDK 17 (ou appeler `java.exe`
par son chemin complet) puis relancer :

```bat
set "PATH=C:\chemin\vers\jdk-17\bin;%PATH%"
java -cp "bin;lib\postgresql.jar" Main
```

### Depuis un IDE

- **VS Code** : `lib/postgresql.jar` est référencé via `.vscode/settings.json`
  (`java.project.referencedLibraries`). Ce dossier est ignoré par git : s'il est absent,
  créer le fichier avec `"java.project.referencedLibraries": ["lib/**/*.jar"]` puis exécuter `src/Main.java`.
- **IntelliJ IDEA** : importer le projet, ajouter `lib/postgresql.jar` au classpath
  puis exécuter `src/Main.java`.

## Architecture

Architecture en couches, sans framework, avec JDBC « à la main » :

```text
UI (src/UI)              saisie console et affichage
        |
Services (src/services)  regles metier et transactions SQL (commit / rollback)
        |
DAOS (src/DAOS)          acces aux donnees (JDBC, PreparedStatement)
        |
util.DatabaseConnection  connexion PostgreSQL
```

- L'`UI` ne parle jamais directement à la base : elle passe par les `services`.
- Les `services` valident, orchestrent et lèvent des exceptions métier
  (`MontantInvalideException`, `SoldeInsuffisantException`, `CompteIntrouvableException`,
  `ClientIntrouvableException`).
- Les `DAOS` implémentent l'interface générique `DAO<T>` (`create`, `findById`, `findAll`,
  `update`, `delete`).

## Structure

| Dossier / fichier | Rôle |
| --- | --- |
| `src/Main.java` | Point d'entrée (`MenuUI#demarrer`) |
| `src/models` | `Client`, `Compte` (abstraite), `CompteCourant`, `CompteEpargne`, `Transaction`, `TypeTransaction` (`VERSEMENT`, `RETRAIT`, `VIREMENT`) |
| `src/DAOS` | `DAO<T>`, `ClientDAO`, `CompteDAO`, `TransactionDAO` (accès JDBC) |
| `src/services` | `ClientService`, `CompteService`, `TransactionService`, `RapportService` (logique métier) |
| `src/UI` | `MenuUI`, `ClientCompteUI`, `TransactionUI`, `HistoriqueUI`, `AnalyseUI`, `AlerteUI` (menus console) |
| `src/util` | `DatabaseConnection` (connexion BD), `DateUtils`, `ValidationUtils` (réservés) |
| `src/exceptions` | Exceptions métier |
| `sql/shema.sql` | Schéma de la base + requêtes de contrôle |
| `lib/postgresql.jar` | Pilote JDBC PostgreSQL |

## Menus

### Menu principal

| Option | Action |
| --- | --- |
| 1 | Créer un client et ses comptes |
| 2 | Enregistrer une transaction (versement / retrait / virement) |
| 3 | Consulter l'historique des transactions d'un compte |
| 4 | Lancer une analyse (Top 5, rapport, inactifs, suspects) |
| 5 | Recevoir des alertes sur les comptes |
| 0 | Quitter |

### 1 - Clients et comptes

1. Créer un client (nom, email)
2. Créer un compte pour un client (ID client, type 1 = courant / 2 = épargne, numéro, découvert ou taux)
3. Afficher un client — le libellé du menu annonce « par ID ou email », mais la saisie ne
   demande que l'ID, qui est le seul critère réellement utilisé pour la recherche
4. Afficher un compte (par numéro)
5. Supprimer un client (suppression en cascade de ses comptes)
6. Supprimer un compte
0. Retour au menu principal

### 2 - Transactions

1. **Versement** : numéro du compte destinataire, montant, lieu
2. **Retrait** : numéro du compte source, montant, lieu
3. **Virement** : numéro du compte source, numéro du compte destinataire, montant, lieu

La date et l'heure sont enregistrées automatiquement (`LocalDateTime.now()`).

### 3 - Historique

Saisir un numéro de compte : affiche toutes les transactions où le compte est source ou
destination, de la plus récente à la plus ancienne.

### 4 - Analyses

1. Top 5 clients par solde (somme des soldes de tous leurs comptes)
2. Rapport mensuel des transactions 
3. Transactions suspectes 
4. Comptes inactifs (aucune opération depuis 12 mois) — listée avec type, titulaire, solde et
   date de dernière activité

### 5 - Alertes

 

## Modèle de données

| Table | Colonnes principales |
| --- | --- |
| `clients` | `id` (PK, UUID), `nom`, `email` (UNIQUE) |
| `comptes` | `id` (PK), `numero` (UNIQUE), `solde`, `type_compte` (`COURANT` \| `EPARGNE`), `decouvert_autorise`, `taux_interet`, `client_id` (FK → `clients`, `ON DELETE CASCADE`) |
| `transactions` | `id` (PK), `compte_source_id` (FK), `compte_destination_id` (FK), `montant`, `type` (`VERSEMENT` \| `RETRAIT` \| `VIREMENT`), `date_transaction` (TIMESTAMP), `lieu` |

Contraintes : `montant > 0`, type de transaction limité aux trois valeurs autorisées,
date et heure conservées (`TIMESTAMP`).
