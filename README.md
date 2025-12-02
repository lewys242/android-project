# Mbongo Android - Application de Gestion Financière

Application Android native développée avec Kotlin et Jetpack Compose, reprenant toutes les fonctionnalités de la version web avec le même thème élégant noir et or.

## 🎨 Caractéristiques

- **Thème Sombre Élégant**: Noir (#1a1a1a) avec accents dorés (#d4af37)
- **Architecture MVVM**: Séparation claire des responsabilités
- **Jetpack Compose**: UI moderne et réactive
- **Room Database**: Stockage local SQLite
- **Material Design 3**: Composants modernes

## 📱 Fonctionnalités

### 1. Gestion des Catégories
- 50+ catégories prédéfinies avec couleurs et icônes
- Création de catégories personnalisées
- Modification et suppression

### 2. Suivi des Dépenses
- Ajout rapide de dépenses
- Catégorisation automatique
- Filtrage par mois/année/catégorie
- Modification et suppression

### 3. Gestion des Revenus
- Suivi des revenus mensuels
- Historique complet
- Calculs automatiques

### 4. Budgets
- Définition de budgets par catégorie
- Suivi mensuel
- Alertes de dépassement

### 5. Prêts
- Gestion des prêts avec intérêts
- Calcul automatique des échéances
- Suivi des remboursements

### 6. Épargne
- Épargne automatique (5% ou 10%)
- Visualisation de l'épargne accumulée
- Objectifs d'épargne

### 7. Statistiques
- Graphiques mensuels et annuels
- Répartition par catégorie
- Évolution dans le temps

## 🛠️ Technologies Utilisées

- **Langage**: Kotlin 1.9.20
- **UI**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Base de données**: Room
- **Injection de dépendances**: Hilt
- **Navigation**: Navigation Compose
- **Graphiques**: Vico / MPAndroidChart
- **Async**: Kotlin Coroutines & Flow

## 📁 Structure du Projet

```
app/src/main/java/com/mbongo/app/
├── data/
│   ├── local/
│   │   ├── dao/           # Data Access Objects
│   │   ├── entity/        # Entités de base de données
│   │   └── MbongoDatabase.kt
│   └── repository/        # Repositories
├── di/                    # Modules d'injection Hilt
├── domain/
│   ├── model/            # Modèles du domaine
│   └── usecase/          # Use cases métier
├── ui/
│   ├── screens/          # Écrans de l'application
│   │   ├── dashboard/
│   │   ├── expenses/
│   │   ├── incomes/
│   │   ├── budgets/
│   │   ├── loans/
│   │   └── stats/
│   ├── components/       # Composants réutilisables
│   ├── navigation/       # Navigation
│   └── theme/            # Thème et styles
├── util/                 # Utilitaires
├── MainActivity.kt
└── MbongoApplication.kt
```

## 🚀 Installation

### Prérequis
- Android Studio Hedgehog | 2023.1.1 ou plus récent
- JDK 17
- Android SDK 34
- Gradle 8.2+

### Étapes

1. **Cloner le projet** (ou copier le dossier android-project dans votre workspace)

2. **Ouvrir dans Android Studio**
   - File → Open
   - Sélectionner le dossier `android-project`

3. **Synchroniser Gradle**
   - Android Studio le fera automatiquement
   - Ou: File → Sync Project with Gradle Files

4. **Configurer un émulateur**
   - Tools → Device Manager
   - Create Device
   - Sélectionner un appareil (ex: Pixel 6)
   - API 34 (Android 14)

5. **Lancer l'application**
   - Click sur le bouton Run (▶️)
   - Ou: Shift + F10

## 📝 Configuration

### Personnalisation des Couleurs

Modifier `ui/theme/Color.kt`:
```kotlin
val Gold = Color(0xFFD4AF37)  // Couleur or principale
val Black = Color(0xFF1A1A1A) // Fond noir
```

### Catégories par Défaut

Ajouter des catégories dans `data/repository/CategoryRepository.kt`:
```kotlin
val defaultCategories = listOf(
    Category(name = "Alimentation", color = "#10B981", icon = "🍽️"),
    // ...
)
```

## 🎯 Prochaines Étapes

1. **Implémenter les Repositories**
2. **Créer les ViewModels**
3. **Développer les écrans Compose**
4. **Ajouter la navigation**
5. **Intégrer les graphiques**
6. **Implémenter l'export de données**
7. **Ajouter les tests**

## 📱 Captures d'Écran

_(À ajouter après développement des écrans)_

## 🤝 Contribution

Ce projet est une réplique de l'application web Mbongo.

## 📄 Licence

Privé - Tous droits réservés

## 🔗 Liens Utiles

- [Documentation Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [Material Design 3](https://m3.material.io/)

---

**Développé avec ❤️ pour reproduire l'expérience web Mbongo sur Android**
