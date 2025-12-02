# Structure du Projet Mbongo Android

```
mbongo-android/
├── app/
│   ├── build.gradle.kts              # Configuration Gradle de l'app
│   ├── proguard-rules.pro            # Règles ProGuard
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml   # Manifest de l'application
│       │   ├── java/com/mbongo/app/
│       │   │   ├── MbongoApplication.kt        # Application Hilt
│       │   │   ├── MainActivity.kt             # Activité principale
│       │   │   │
│       │   │   ├── data/                       # Couche de données
│       │   │   │   ├── local/
│       │   │   │   │   ├── MbongoDatabase.kt  # Base de données Room
│       │   │   │   │   ├── dao/               # Data Access Objects
│       │   │   │   │   │   ├── CategoryDao.kt
│       │   │   │   │   │   ├── ExpenseDao.kt
│       │   │   │   │   │   ├── IncomeDao.kt
│       │   │   │   │   │   ├── BudgetDao.kt
│       │   │   │   │   │   ├── LoanDao.kt
│       │   │   │   │   │   └── RepaymentDao.kt
│       │   │   │   │   └── entity/            # Entités Room
│       │   │   │   │       ├── Category.kt
│       │   │   │   │       ├── Expense.kt
│       │   │   │   │       ├── Income.kt
│       │   │   │   │       ├── Budget.kt
│       │   │   │   │       ├── Loan.kt
│       │   │   │   │       └── Repayment.kt
│       │   │   │   └── repository/            # [À CRÉER]
│       │   │   │       ├── CategoryRepository.kt
│       │   │   │       ├── ExpenseRepository.kt
│       │   │   │       ├── IncomeRepository.kt
│       │   │   │       ├── BudgetRepository.kt
│       │   │   │       ├── LoanRepository.kt
│       │   │   │       └── RepaymentRepository.kt
│       │   │   │
│       │   │   ├── di/                        # Dependency Injection
│       │   │   │   ├── DatabaseModule.kt      # Module pour la DB
│       │   │   │   └── [RepositoryModule.kt]  # [À CRÉER]
│       │   │   │
│       │   │   ├── domain/                    # [À CRÉER]
│       │   │   │   ├── model/                 # Modèles métier
│       │   │   │   │   ├── ExpenseWithCategory.kt
│       │   │   │   │   ├── BudgetStatus.kt
│       │   │   │   │   ├── LoanDetails.kt
│       │   │   │   │   └── MonthlyStats.kt
│       │   │   │   └── usecase/               # Use cases
│       │   │   │       ├── GetMonthlyExpenses.kt
│       │   │   │       ├── CalculateBudgetStatus.kt
│       │   │   │       ├── GetDashboardData.kt
│       │   │   │       └── CalculateSavings.kt
│       │   │   │
│       │   │   ├── ui/                        # Interface utilisateur
│       │   │   │   ├── theme/                 # Thème de l'app
│       │   │   │   │   ├── Color.kt           # Couleurs (noir/or)
│       │   │   │   │   ├── Theme.kt           # Thème Material3
│       │   │   │   │   └── Type.kt            # Typographie
│       │   │   │   │
│       │   │   │   ├── navigation/            # Navigation
│       │   │   │   │   ├── Screen.kt          # Routes
│       │   │   │   │   └── MbongoNavigation.kt # NavHost
│       │   │   │   │
│       │   │   │   ├── components/            # [À CRÉER]
│       │   │   │   │   ├── MbongoButton.kt
│       │   │   │   │   ├── MbongoCard.kt
│       │   │   │   │   ├── MbongoTextField.kt
│       │   │   │   │   ├── CategoryChip.kt
│       │   │   │   │   ├── AmountDisplay.kt
│       │   │   │   │   └── DatePicker.kt
│       │   │   │   │
│       │   │   │   └── screens/               # Écrans
│       │   │   │       ├── dashboard/
│       │   │   │       │   ├── DashboardScreen.kt
│       │   │   │       │   └── [DashboardViewModel.kt] # [À CRÉER]
│       │   │   │       │
│       │   │   │       ├── expenses/          # [À CRÉER]
│       │   │   │       │   ├── ExpensesScreen.kt
│       │   │   │       │   ├── AddExpenseScreen.kt
│       │   │   │       │   └── ExpensesViewModel.kt
│       │   │   │       │
│       │   │   │       ├── incomes/           # [À CRÉER]
│       │   │   │       │   ├── IncomesScreen.kt
│       │   │   │       │   ├── AddIncomeScreen.kt
│       │   │   │       │   └── IncomesViewModel.kt
│       │   │   │       │
│       │   │   │       ├── budgets/           # [À CRÉER]
│       │   │   │       │   ├── BudgetsScreen.kt
│       │   │   │       │   ├── SetBudgetScreen.kt
│       │   │   │       │   └── BudgetsViewModel.kt
│       │   │   │       │
│       │   │   │       ├── loans/             # [À CRÉER]
│       │   │   │       │   ├── LoansScreen.kt
│       │   │   │       │   ├── AddLoanScreen.kt
│       │   │   │       │   ├── LoanDetailsScreen.kt
│       │   │   │       │   └── LoansViewModel.kt
│       │   │   │       │
│       │   │   │       ├── statistics/        # [À CRÉER]
│       │   │   │       │   ├── StatisticsScreen.kt
│       │   │   │       │   └── StatisticsViewModel.kt
│       │   │   │       │
│       │   │   │       └── categories/        # [À CRÉER]
│       │   │   │           ├── CategoriesScreen.kt
│       │   │   │           └── CategoriesViewModel.kt
│       │   │   │
│       │   │   └── util/                      # [À CRÉER]
│       │   │       ├── DateUtils.kt           # Formatage de dates
│       │   │       ├── CurrencyUtils.kt       # Formatage monnaie
│       │   │       ├── Constants.kt           # Constantes
│       │   │       └── Extensions.kt          # Extensions Kotlin
│       │   │
│       │   └── res/                           # Ressources
│       │       ├── values/
│       │       │   ├── strings.xml            # Chaînes de texte
│       │       │   ├── colors.xml             # Couleurs
│       │       │   └── themes.xml             # Thème XML
│       │       ├── xml/
│       │       │   ├── backup_rules.xml
│       │       │   └── data_extraction_rules.xml
│       │       └── mipmap-*/                  # Icônes de l'app
│       │
│       ├── androidTest/                       # [À CRÉER]
│       │   └── java/com/mbongo/app/
│       │       └── DatabaseTest.kt
│       │
│       └── test/                              # [À CRÉER]
│           └── java/com/mbongo/app/
│               ├── repository/
│               │   └── CategoryRepositoryTest.kt
│               └── viewmodel/
│                   └── DashboardViewModelTest.kt
│
├── gradle/                                    # Wrapper Gradle
├── build.gradle.kts                           # Configuration Gradle root
├── settings.gradle.kts                        # Settings Gradle
├── gradle.properties                          # Propriétés Gradle
├── README.md                                  # Documentation principale
├── INSTALLATION.md                            # Guide d'installation
└── .gitignore                                 # [À CRÉER]
```

## 📊 Légende

- ✅ **Créé**: Fichiers déjà générés
- 🔨 **[À CRÉER]**: Fichiers à implémenter

## 🎯 Priorités de Développement

### Phase 1: Foundation (Complété)
- ✅ Configuration Gradle
- ✅ Base de données Room (Entités + DAOs)
- ✅ Thème noir/or
- ✅ Navigation de base
- ✅ Dashboard initial

### Phase 2: Core Features
1. **Repositories** (Important)
2. **ViewModels** (Important)
3. **Écrans principaux**:
   - Expenses (Priorité 1)
   - Incomes (Priorité 1)
   - Budgets (Priorité 2)
   - Loans (Priorité 2)

### Phase 3: Advanced Features
- Statistiques et graphiques
- Composants réutilisables
- Use cases complexes
- Export de données

### Phase 4: Polish
- Tests unitaires
- Tests d'intégration
- Performance optimization
- Animations et transitions

## 📝 Notes Importantes

- **MVVM Pattern**: Respecter la séparation des responsabilités
- **Single Source of Truth**: Room DB comme source de vérité
- **Reactive**: Utiliser Flow partout
- **Hilt**: Injection de dépendances pour tous les composants
- **Material 3**: Utiliser les composants Material Design 3
- **Dark Theme Only**: Thème sombre uniquement (noir/or)
