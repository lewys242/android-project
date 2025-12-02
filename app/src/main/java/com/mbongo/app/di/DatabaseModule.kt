package com.mbongo.app.di

import android.content.Context
import androidx.room.Room
import com.mbongo.app.data.local.MbongoDatabase
import com.mbongo.app.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        databaseProvider: Provider<MbongoDatabase>
    ): MbongoDatabase {
        return Room.databaseBuilder(
            context,
            MbongoDatabase::class.java,
            "mbongo_database"
        )
            .addCallback(MbongoDatabase.Callback(databaseProvider))
            .addMigrations(MbongoDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideCategoryDao(database: MbongoDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideExpenseDao(database: MbongoDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    fun provideIncomeDao(database: MbongoDatabase): IncomeDao {
        return database.incomeDao()
    }

    @Provides
    fun provideBudgetDao(database: MbongoDatabase): BudgetDao {
        return database.budgetDao()
    }

    @Provides
    fun provideLoanDao(database: MbongoDatabase): LoanDao {
        return database.loanDao()
    }

    @Provides
    fun provideRepaymentDao(database: MbongoDatabase): RepaymentDao {
        return database.repaymentDao()
    }
}
