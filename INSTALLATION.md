# Mbongo Android - Guide d'Installation Détaillé

## 📋 Prérequis

### Logiciels Nécessaires

1. **Android Studio Hedgehog (2023.1.1) ou plus récent**
   - Télécharger depuis: https://developer.android.com/studio
   - Installation recommandée: "Standard Setup"

2. **Java Development Kit (JDK) 17**
   - Inclus avec Android Studio
   - Ou télécharger depuis: https://www.oracle.com/java/technologies/downloads/

3. **Git** (optionnel, pour le contrôle de version)
   - Télécharger depuis: https://git-scm.com/

### Configuration Système Minimale

- **OS**: Windows 10/11, macOS 10.14+, ou Linux
- **RAM**: 8 GB minimum (16 GB recommandé)
- **Espace disque**: 8 GB pour Android Studio + 4 GB pour le SDK Android
- **Résolution**: 1280 x 800 minimum

## 🚀 Installation Étape par Étape

### Étape 1: Installer Android Studio

1. Télécharger Android Studio
2. Lancer l'installateur
3. Suivre l'assistant d'installation
4. Lors du premier lancement:
   - Choisir "Standard" installation
   - Accepter les licences
   - Laisser télécharger le SDK Android

### Étape 2: Préparer le Projet

#### Option A: Déplacer le Dossier

1. Copier le dossier `android-project` depuis:
   ```
   C:\Users\HP\Documents\mbongo\android-project
   ```

2. Coller dans votre emplacement de projets Android:
   ```
   C:\Users\HP\AndroidStudioProjects\mbongo-android
   ```

3. Renommer si nécessaire

#### Option B: Utiliser le Dossier Existant

Vous pouvez ouvrir directement le dossier existant.

### Étape 3: Ouvrir le Projet dans Android Studio

1. Lancer Android Studio
2. Click sur "Open" sur l'écran d'accueil
3. Naviguer vers le dossier du projet
4. Sélectionner le dossier contenant `build.gradle.kts`
5. Click sur "OK"

### Étape 4: Synchronisation Gradle (Automatique)

Android Studio va automatiquement:
1. Télécharger les dépendances Gradle
2. Télécharger toutes les bibliothèques nécessaires
3. Compiler le projet

⏱️ **Durée**: 5-15 minutes selon votre connexion internet

#### Si la Synchronisation Échoue

```bash
# Dans le terminal d'Android Studio:
./gradlew clean
./gradlew build
```

Ou:
- File → Invalidate Caches / Restart
- Puis: File → Sync Project with Gradle Files

### Étape 5: Configurer un Émulateur Android

#### Créer un Nouveau Device

1. Click sur l'icône "Device Manager" (côté droit)
2. Click sur "Create Device"
3. Choisir un appareil:
   - **Recommandé**: Pixel 6 ou Pixel 7
   - Ou tout appareil récent

4. Sélectionner une System Image:
   - **API Level**: 34 (Android 14) - Recommandé
   - Ou minimum API 24 (Android 7.0)
   - Click sur l'icône de téléchargement si nécessaire

5. Configurer l'AVD:
   - Nom: `Pixel_6_API_34`
   - Startup orientation: Portrait
   - **RAM**: 2048 MB minimum

6. Click sur "Finish"

#### Démarrer l'Émulateur

1. Sélectionner l'émulateur dans la liste
2. Click sur le bouton Play (▶️)
3. Attendre le démarrage (1-2 minutes)

### Étape 6: Lancer l'Application

1. Vérifier que l'émulateur est démarré
2. Sélectionner l'émulateur dans la liste des devices (en haut)
3. Click sur le bouton Run (▶️) ou:
   - **Raccourci Windows/Linux**: Shift + F10
   - **Raccourci macOS**: Control + R

4. L'application va:
   - Se compiler (1-2 minutes la première fois)
   - S'installer sur l'émulateur
   - Se lancer automatiquement

## 🔧 Tester sur un Appareil Physique

### Activer le Mode Développeur

#### Sur Android:

1. Aller dans **Paramètres**
2. **À propos du téléphone**
3. Taper 7 fois sur **Numéro de build**
4. Retour aux Paramètres
5. **Options pour les développeurs**
6. Activer **Débogage USB**

### Connecter l'Appareil

1. Brancher le téléphone en USB
2. Autoriser le débogage sur le téléphone
3. Le device apparaîtra dans Android Studio
4. Sélectionner le device
5. Lancer l'app

## 📱 Initialiser les Données de Test

Pour avoir des données de démonstration:

1. Ouvrir `app/src/main/java/com/mbongo/app/data/repository/CategoryRepository.kt`
2. Ajouter la fonction d'initialisation des catégories par défaut
3. Appeler cette fonction au premier lancement

## 🐛 Résolution des Problèmes

### Erreur "SDK not found"

**Solution**:
1. File → Project Structure
2. SDK Location
3. Vérifier que le chemin du SDK Android est correct
4. Par défaut: `C:\Users\[USERNAME]\AppData\Local\Android\Sdk`

### Erreur "Gradle sync failed"

**Solution**:
```bash
# Vérifier la version Java
java -version  # Doit être 17

# Nettoyer et rebuilder
./gradlew clean
./gradlew build --refresh-dependencies
```

### L'émulateur est lent

**Solutions**:
1. Activer "Intel HAXM" ou "AMD Hypervisor"
2. Augmenter la RAM de l'AVD (4096 MB)
3. Activer "Graphics: Hardware - GLES 2.0"

### Erreur de compilation Hilt

**Solution**:
1. Vérifier que `@HiltAndroidApp` est sur `MbongoApplication`
2. Vérifier que `@AndroidEntryPoint` est sur `MainActivity`
3. Rebuild: Build → Clean Project → Rebuild Project

### Erreur Room "Cannot find implementation"

**Solution**:
```kotlin
// Dans build.gradle.kts (app):
plugins {
    id("kotlin-kapt")  // Vérifier cette ligne
}

kapt {
    correctErrorTypes = true
}
```

## 📊 Prochaines Étapes du Développement

Maintenant que le projet est configuré:

1. **Implémenter les Repositories**
   - `CategoryRepository`
   - `ExpenseRepository`
   - `IncomeRepository`
   - etc.

2. **Créer les ViewModels**
   - `DashboardViewModel`
   - `ExpensesViewModel`
   - etc.

3. **Développer les Écrans**
   - Screen Expenses
   - Screen Incomes
   - Screen Budgets
   - Screen Loans
   - Screen Statistics

4. **Ajouter les Fonctionnalités**
   - Graphiques avec Vico
   - Export CSV
   - Notifications
   - Widgets

5. **Tests**
   - Tests unitaires (ViewModels)
   - Tests d'intégration (Repository)
   - Tests UI (Compose)

## 📚 Ressources Utiles

- **Documentation Android**: https://developer.android.com/docs
- **Jetpack Compose**: https://developer.android.com/jetpack/compose/documentation
- **Room Database**: https://developer.android.com/training/data-storage/room
- **Hilt**: https://developer.android.com/training/dependency-injection/hilt-android
- **Material Design 3**: https://m3.material.io/

## 💡 Conseils de Développement

1. **Utiliser Logcat** pour déboguer:
   - View → Tool Windows → Logcat

2. **Live Preview** pour Compose:
   - Ajouter `@Preview` sur les composables
   - Activer "Split" ou "Design" view

3. **Raccourcis utiles**:
   - `Ctrl + Shift + A`: Command palette
   - `Ctrl + /`: Commenter/Décommenter
   - `Ctrl + Alt + L`: Formater le code
   - `Shift + F10`: Run
   - `Shift + F9`: Debug

4. **Hot Reload**: Les modifications Compose se reflètent automatiquement

## ✅ Checklist de Vérification

- [ ] Android Studio installé et à jour
- [ ] JDK 17 configuré
- [ ] Projet ouvert sans erreur
- [ ] Gradle sync réussi
- [ ] Émulateur créé et fonctionnel
- [ ] Application lancée avec succès
- [ ] Thème noir/or visible
- [ ] Navigation fonctionnelle
- [ ] Aucune erreur dans Logcat

## 🎉 Félicitations !

Votre environnement de développement est prêt. Vous pouvez maintenant développer l'application Mbongo Android et reproduire toutes les fonctionnalités de la version web !

---

**Pour toute question ou problème, consultez la documentation Android ou créez une issue.**
