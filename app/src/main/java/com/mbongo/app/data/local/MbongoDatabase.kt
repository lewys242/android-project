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
    version = 2,
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
            // Catégories de dépenses
            val expenseCategories = listOf(
                Category(name = "Alimentation", type = "expense", icon = "🍔"),
                Category(name = "Transport", type = "expense", icon = "🚗"),
                Category(name = "Logement", type = "expense", icon = "🏠"),
                Category(name = "Santé", type = "expense", icon = "⚕️"),
                Category(name = "Éducation", type = "expense", icon = "📚"),
                Category(name = "Loisirs", type = "expense", icon = "🎮"),
                Category(name = "Vêtements", type = "expense", icon = "👕"),
                Category(name = "Électricité", type = "expense", icon = "💡"),
                Category(name = "Internet", type = "expense", icon = "🌐"),
                Category(name = "Téléphone", type = "expense", icon = "📱"),
                Category(name = "Autres", type = "expense", icon = "📦")
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
