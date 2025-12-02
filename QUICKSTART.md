# DÉMARRAGE RAPIDE - Mbongo Android

## 🚀 Lancer l'Application en 5 Minutes

### 1. Pré-requis
- ✅ Android Studio installé
- ✅ JDK 17 configuré

### 2. Ouvrir le Projet
```
File → Open → Sélectionner "android-project"
```

### 3. Attendre la Synchronisation Gradle
⏱️ 2-5 minutes (téléchargement des dépendances)

### 4. Créer un Émulateur
```
Device Manager → Create Device → Pixel 6 → API 34 → Finish
```

### 5. Lancer l'App
```
Click sur le bouton Run ▶️
ou
Shift + F10
```

## ✨ Ce Qui Est Prêt

### ✅ Fonctionnel
- Architecture MVVM avec Hilt
- Base de données Room (6 tables)
- Thème noir/or élégant
- Navigation avec Bottom Bar
- Dashboard avec cartes statistiques
- Actions rapides

### 🔨 À Développer
1. **Repositories** - Logique métier
2. **ViewModels** - État de l'UI
3. **Écrans** - Expenses, Incomes, Budgets, Loans, Stats
4. **Graphiques** - Visualisation des données
5. **Composants** - Boutons, Cards, TextField personnalisés

## 📱 Fonctionnalités Prévues

### Phase 1 (Urgent)
- [ ] Gestion des dépenses (CRUD)
- [ ] Gestion des revenus (CRUD)
- [ ] Liste des catégories
- [ ] Filtres par date

### Phase 2
- [ ] Budgets mensuels
- [ ] Prêts et remboursements
- [ ] Statistiques mensuelles
- [ ] Graphiques (Vico)

### Phase 3
- [ ] Épargne automatique
- [ ] Export CSV
- [ ] Notifications
- [ ] Widgets

## 🎨 Design

### Couleurs
- **Fond**: #1A1A1A (Noir profond)
- **Cartes**: #2A2A2A (Gris foncé)
- **Accent**: #D4AF37 (Or)
- **Texte**: #FFFFFF (Blanc)

### Composants Material 3
- Cards avec élévation
- Bottom Navigation Bar
- Floating Action Buttons
- TextFields outlined
- Dialogs plein écran

## 📂 Fichiers Importants

```
app/src/main/java/com/mbongo/app/
├── MainActivity.kt           # Point d'entrée
├── data/local/              # Base de données
│   ├── MbongoDatabase.kt
│   ├── dao/                 # DAOs (6 fichiers)
│   └── entity/              # Entités (6 fichiers)
├── di/                      # Hilt
│   └── DatabaseModule.kt
├── ui/
│   ├── theme/               # Couleurs, thème, typo
│   ├── navigation/          # Navigation
│   └── screens/
│       └── dashboard/       # Dashboard fonctionnel
```

## 🛠️ Commandes Utiles

### Nettoyer le Build
```bash
./gradlew clean
```

### Rebuild
```bash
./gradlew build
```

### Installer sur Device
```bash
./gradlew installDebug
```

### Logs en Temps Réel
```bash
adb logcat | grep Mbongo
```

## 🔍 Débogage

### Logcat
```
View → Tool Windows → Logcat
```

### Database Inspector
```
View → Tool Windows → App Inspection → Database Inspector
```

### Layout Inspector
```
Tools → Layout Inspector
```

## 📖 Documentation

- [README.md](README.md) - Vue d'ensemble complète
- [INSTALLATION.md](INSTALLATION.md) - Guide détaillé d'installation
- [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) - Structure du projet

## 💡 Prochaines Étapes

1. **Créer les Repositories**
   ```kotlin
   // Exemple: ExpenseRepository.kt
   class ExpenseRepository @Inject constructor(
       private val expenseDao: ExpenseDao
   ) {
       fun getAllExpenses() = expenseDao.getAllExpenses()
       suspend fun insertExpense(expense: Expense) = expenseDao.insertExpense(expense)
   }
   ```

2. **Créer les ViewModels**
   ```kotlin
   @HiltViewModel
   class ExpensesViewModel @Inject constructor(
       private val expenseRepository: ExpenseRepository
   ) : ViewModel() {
       val expenses = expenseRepository.getAllExpenses()
           .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
   }
   ```

3. **Développer les Écrans**
   - Listes avec LazyColumn
   - Formulaires avec Dialog
   - Filtres avec Chip

## 🎯 Objectif

Reproduire **à l'identique** toutes les fonctionnalités de l'application web Mbongo:

✓ Même thème noir/or
✓ Mêmes fonctionnalités
✓ Même ergonomie
✓ Même structure de données

## 🤝 Besoin d'Aide?

- Consulter la [documentation Android](https://developer.android.com)
- Chercher sur [Stack Overflow](https://stackoverflow.com/questions/tagged/android)
- Lire les commentaires dans le code

---

**Bon développement ! 🚀**
