# 🚀 Guide de Test de l'Application Mbongo

## Prérequis

### 1. Installer Android Studio
1. Téléchargez Android Studio depuis : https://developer.android.com/studio
2. Installez-le avec les composants par défaut
3. Lors de la première ouverture, laissez-le télécharger le SDK Android

### 2. Configuration après installation
- Android Studio installera automatiquement :
  - Android SDK
  - Android SDK Platform-Tools
  - Android Emulator
  - Gradle

## 📱 Tester l'Application

### Méthode 1 : Avec Android Studio (Recommandé)

1. **Ouvrir le projet**
   ```
   - Lancez Android Studio
   - Cliquez sur "Open"
   - Sélectionnez le dossier : c:\Users\HP\Documents\android-project
   - Attendez que Gradle synchronise le projet (barre de progression en bas)
   ```

2. **Créer un émulateur**
   ```
   - Cliquez sur "Device Manager" (icône téléphone en haut à droite)
   - Cliquez sur "Create Device"
   - Sélectionnez un appareil (ex: Pixel 6)
   - Sélectionnez une image système (ex: Android 13 "Tiramisu")
   - Téléchargez l'image si nécessaire
   - Cliquez sur "Finish"
   ```

3. **Lancer l'application**
   ```
   - Sélectionnez votre émulateur dans la liste déroulante en haut
   - Cliquez sur le bouton "Run" (▶️) ou appuyez sur Shift+F10
   - L'émulateur va démarrer et l'app sera installée automatiquement
   ```

### Méthode 2 : En ligne de commande (après installation Android Studio)

1. **Démarrer un émulateur**
   ```powershell
   # Lister les émulateurs disponibles
   %LOCALAPPDATA%\Android\Sdk\emulator\emulator -list-avds
   
   # Démarrer un émulateur (remplacez AVD_NAME par le nom de votre émulateur)
   %LOCALAPPDATA%\Android\Sdk\emulator\emulator -avd AVD_NAME
   ```

2. **Compiler et installer l'app**
   ```powershell
   cd c:\Users\HP\Documents\android-project
   .\gradlew installDebug
   ```

### Méthode 3 : Sur un appareil physique

1. **Activer le mode développeur sur votre téléphone**
   - Paramètres → À propos du téléphone
   - Appuyez 7 fois sur "Numéro de build"
   - Retour → Options de développement
   - Activez "Débogage USB"

2. **Connecter votre téléphone**
   - Connectez via USB
   - Autorisez le débogage USB sur le téléphone
   - Dans Android Studio, sélectionnez votre appareil et cliquez sur Run

## 🎯 Fonctionnalités à Tester

### ✅ Dashboard
- [ ] Affichage du solde actuel
- [ ] Affichage des statistiques (Revenus, Dépenses, Épargne, Prêts)
- [ ] Navigation vers les autres écrans

### ✅ Dépenses
- [ ] Ajouter une nouvelle dépense
- [ ] Sélectionner une catégorie
- [ ] Voir la liste des dépenses
- [ ] Supprimer une dépense

### ✅ Revenus
- [ ] Ajouter un nouveau revenu
- [ ] Sélectionner une catégorie
- [ ] Voir la liste des revenus
- [ ] Supprimer un revenu

### ✅ Prêts
- [ ] Ajouter un nouveau prêt
- [ ] Voir la progression des prêts
- [ ] Supprimer un prêt

### ✅ Statistiques
- [ ] Affichage de l'aperçu mensuel

### ✅ Navigation
- [ ] Barre de navigation inférieure fonctionnelle
- [ ] Navigation entre tous les écrans

## 🐛 Résolution de problèmes

### Erreur de synchronisation Gradle
- Attendez que Android Studio télécharge toutes les dépendances
- Si ça échoue, allez dans File → Invalidate Caches → Invalidate and Restart

### L'émulateur ne démarre pas
- Assurez-vous que la virtualisation est activée dans le BIOS
- Ou utilisez un appareil physique

### Erreur de compilation
- Vérifiez que vous avez installé Android SDK API 34
- Tools → SDK Manager → cochez Android 14.0 (API 34)

## 📝 Notes

- La première compilation peut prendre plusieurs minutes (téléchargement des dépendances)
- L'émulateur peut être lent au premier démarrage
- Les catégories par défaut sont créées automatiquement au premier lancement
- Toutes les données sont stockées localement avec Room Database

## 🎨 Captures d'écran attendues

Vous devriez voir :
- **Dashboard** : Cartes avec solde et statistiques à 0 FCFA
- **Dépenses/Revenus** : Message "Aucune transaction" avec bouton +
- **Prêts** : Message "Aucun prêt" avec bouton +
- **Catégories** : 18 catégories pré-chargées (11 dépenses + 7 revenus)

Bon test ! 🚀
