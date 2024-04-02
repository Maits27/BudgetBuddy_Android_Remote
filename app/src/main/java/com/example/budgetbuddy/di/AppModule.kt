package com.example.budgetbuddy.di

import android.content.Context
import androidx.room.Room
import com.example.budgetbuddy.Data.Room.Database
import com.example.budgetbuddy.Data.DAO.GastoDao
import com.example.budgetbuddy.Data.Repositories.GastoRepository
import com.example.budgetbuddy.Data.Repositories.IGastoRepository
import com.example.budgetbuddy.Data.Repositories.IUserRepository
import com.example.budgetbuddy.Data.DAO.UserDao
import com.example.budgetbuddy.Data.Remote.HTTPService
import com.example.budgetbuddy.Data.Repositories.UserRepository
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
            .createFromAsset("database/budgetBuddyDB2.db")
            .build()

    ////////////////////// DAO //////////////////////
    @Singleton
    @Provides
    fun providesUserDao(db: Database) = db.userDao()

    @Singleton
    @Provides
    fun provideGastoDao(db: Database) = db.gastoDao()


    //////////////   Repositorio Gastos   //////////////
    @Singleton
    @Provides
    fun providesUserRepository(userDao: UserDao, httpService: HTTPService): IUserRepository = UserRepository(userDao, httpService)

    @Singleton
    @Provides
    fun provideGastoRepository(gastoDao: GastoDao, httpService: HTTPService): IGastoRepository = GastoRepository(gastoDao, httpService)


    /////////// Repositorio de preferencias ///////////
    @Singleton
    @Provides
    fun provideUserPreferences(@ApplicationContext app: Context): IGeneralPreferences = PreferencesRepository(app)


}