package com.mbongo.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mbongo.app.data.local.dao.*
import com.mbongo.app.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

@Database(
    entities = [
        Category::class,
        Expense::class,
        Income::class,
        Budget::class,
        Loan::class,
        Repayment::class
    ],
    version = 4,
    exportSchema = false
)
abstract class MbongoDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
    abstract fun budgetDao(): BudgetDao
    abstract fun loanDao(): LoanDao
    abstract fun repaymentDao(): RepaymentDao

    companion object {
        // Migration de la version 1 à 2: ajout de la colonne 'type' à la table incomes
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Ajouter la colonne 'type' avec valeur par défaut 'other'
                db.execSQL("ALTER TABLE incomes ADD COLUMN type TEXT NOT NULL DEFAULT 'other'")
            }
        }
        
        // Migration de la version 2 à 3: ajout des colonnes de synchronisation
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Ajouter les colonnes de synchronisation à la table incomes
                db.execSQL("ALTER TABLE incomes ADD COLUMN isSynced INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE incomes ADD COLUMN remoteId INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE incomes ADD COLUMN pendingDelete INTEGER NOT NULL DEFAULT 0")
            }
        }
        
        // Migration de la version 3 à 4: mise à jour des catégories (ajout de nouvelles catégories)
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Cette migration ne change pas le schéma, juste les données
                // Les nouvelles catégories seront ajoutées au prochain onCreate ou manuellement
            }
        }
    }

    class Callback(
        private val database: Provider<MbongoDatabase>
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Pré-charger les catégories par défaut
            CoroutineScope(Dispatchers.IO).launch {
                populateDatabase(database.get().categoryDao())
            }
        }

        private suspend fun populateDatabase(categoryDao: CategoryDao) {
            // Catégories de dépenses (complètes comme l'application web)
            val expenseCategories = listOf(
                Category(name = "Alimentation", type = "expense", icon = "🍔"),
                Category(name = "Assurance vie", type = "expense", icon = "🛡️"),
                Category(name = "Assurance véhicule", type = "expense", icon = "🚙"),
                Category(name = "Autres", type = "expense", icon = "📦"),
                Category(name = "Cadeaux", type = "expense", icon = "🎁"),
                Category(name = "Carburant", type = "expense", icon = "⛽"),
                Category(name = "Chaussures", type = "expense", icon = "👞"),
                Category(name = "Cinéma/Spectacles", type = "expense", icon = "🎬"),
                Category(name = "Coiffure/Esthétique", type = "expense", icon = "💇"),
                Category(name = "Consultation médicale", type = "expense", icon = "🏥"),
                Category(name = "Crédit/Emprunt", type = "expense", icon = "🏦"),
                Category(name = "Cérémonies", type = "expense", icon = "🎊"),
                Category(name = "Déjeuner bureau", type = "expense", icon = "🥪"),
                Category(name = "Eau", type = "expense", icon = "💧"),
                Category(name = "Frais professionnels", type = "expense", icon = "💼"),
                Category(name = "Gaz", type = "expense", icon = "🔥"),
                Category(name = "Internet/Data", type = "expense", icon = "📶"),
                Category(name = "Investissement", type = "expense", icon = "📈"),
                Category(name = "Livres/Journaux", type = "expense", icon = "📰"),
                Category(name = "Logement", type = "expense", icon = "🏠"),
                Category(name = "Loisirs", type = "expense", icon = "🎮"),
                Category(name = "Marché/Courses", type = "expense", icon = "🛒"),
                Category(name = "Médicaments", type = "expense", icon = "💊"),
                Category(name = "Parking/Péage", type = "expense", icon = "🅿️"),
                Category(name = "Partage/Aide famille", type = "expense", icon = "🤝"),
                Category(name = "Pharmacie", type = "expense", icon = "💉"),
                Category(name = "Poubelle/Assainissement", type = "expense", icon = "🗑️"),
                Category(name = "Produits beauté", type = "expense", icon = "💄"),
                Category(name = "Remboursement de prêt", type = "expense", icon = "💳"),
                Category(name = "Restaurant/Maquis", type = "expense", icon = "🍽️"),
                Category(name = "Réparation auto", type = "expense", icon = "🔩"),
                Category(name = "Santé", type = "expense", icon = "💊"),
                Category(name = "Shopping", type = "expense", icon = "🛍️"),
                Category(name = "Sport/Gym", type = "expense", icon = "⚽"),
                Category(name = "Transport", type = "expense", icon = "🚗"),
                Category(name = "Transport travail", type = "expense", icon = "🚌"),
                Category(name = "Téléphone/Internet", type = "expense", icon = "📱"),
                Category(name = "Urgences/Imprévus", type = "expense", icon = "🚨"),
                Category(name = "Vidange", type = "expense", icon = "🔧"),
                Category(name = "Voyage/Vacances", type = "expense", icon = "✈️"),
                Category(name = "Vêtements", type = "expense", icon = "👕"),
                Category(name = "Éducation", type = "expense", icon = "📚"),
                Category(name = "Électricité", type = "expense", icon = "⚡"),
                Category(name = "Épargne", type = "expense", icon = "💰")
            )

            // Catégories de revenus
            val incomeCategories = listOf(
                Category(name = "Salaire", type = "income", icon = "💰"),
                Category(name = "Freelance", type = "income", icon = "💻"),
                Category(name = "Business", type = "income", icon = "🏢"),
                Category(name = "Investissement", type = "income", icon = "📈"),
                Category(name = "Cadeau", type = "income", icon = "🎁"),
                Category(name = "Bonus", type = "income", icon = "🎉"),
                Category(name = "Autres", type = "income", icon = "💵")
            )

            categoryDao.insertCategories(expenseCategories + incomeCategories)
        }
    }
}
