package com.example.budgetbuddy.di

import android.content.Context
import androidx.room.Room
import com.example.budgetbuddy.Data.Database
import com.example.budgetbuddy.Data.GastoDao
import com.example.budgetbuddy.Data.GastoRepository
import com.example.budgetbuddy.Data.IGastoRepository
import com.example.budgetbuddy.Data.IUserRepository
import com.example.budgetbuddy.Data.UserDao
import com.example.budgetbuddy.Data.UserRepository
import com.example.budgetbuddy.preferences.IGeneralPreferences
import com.example.budgetbuddy.preferences.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/*******************************************************************************
 ****                                 Hilt                                  ****
 *******************************************************************************/

/**
 * Este módulo se instala en [SingletonComponent] por lo que todas las instancias
 * que se definan aquí, estarán definidas a nivel de aplicación. Esto implica que
 * no se destruirán hasta que lo haga la aplicación y que puedan ser compartidas
 * entre actividades (de haberlas).
 *
 * Hilt se encarga de la inyección de estos elementos donde sea necesario.
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Con el @Singleton nos aseguramos de que haya una única instancia
     * de cada uno de los siguientes elementos en toda la APP
     */


    //////////////   Instancia de ROOM   //////////////
    @Singleton
    @Provides
    fun providesBudgetBuddyDatabase(@ApplicationContext app: Context) =
        Room.databaseBuilder(app, Database::class.java, "budgetBuddyDB2")
//            .createFromAsset("database/database.db")
            .build()

    ////////////////////// DAO //////////////////////
    @Singleton
    @Provides
    fun providesUserDao(db:Database) = db.userDao()

    @Singleton
    @Provides
    fun provideGastoDao(db: Database) = db.gastoDao()


    //////////////   Repositorio Gastos   //////////////
    @Singleton
    @Provides
    fun providesUserRepository(userDao: UserDao): IUserRepository = UserRepository(userDao)

    @Singleton
    @Provides
    fun provideGastoRepository(gastoDao: GastoDao): IGastoRepository = GastoRepository(gastoDao)


    /////////// Repositorio de preferencias ///////////
    @Singleton
    @Provides
    fun provideUserPreferences(@ApplicationContext app: Context): IGeneralPreferences = PreferencesRepository(app)


}