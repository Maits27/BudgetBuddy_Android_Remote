package com.example.budgetbuddy.Data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/******************************************************************
 * Interfaz que define la API del listado de gastos - Repositorio
 * Los métodos definidos son las acciones posibles para interactuar
 * con la BBDD
 *******************************************************************/
interface IUserRepository {
    suspend fun insertUsuario(user: User)
    suspend fun deleteUsuario(user: User): Int
    fun todosLosUsuarios(): Flow<List<User>>
//    fun userNamePassword(email: String): List<String>
    fun userName(email: String): String
    fun editarUsuario(user: User): Int
}

/**
 * Implementación de [IGastoRepository] que usa Hilt para inyectar los
 * parámetros necesarios. Desde aquí se accede a [GastoDao], que se encarga
 * de la conexión a la BBDD de Room.
 * */
@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) : IUserRepository {
    override suspend fun insertUsuario(user: User) {
        return userDao.insertUsuario(user)
    }

    override suspend fun deleteUsuario(user: User): Int {
        return userDao.deleteUsuario(user)
    }

    override fun todosLosUsuarios(): Flow<List<User>> {
        return userDao.todosLosUsuarios()
    }


//    override fun userNamePassword(email: String):List<String> {
//        return userDao.usernamePassword(email)?.get(0) ?: emptyList()
//    }

    override fun userName(email: String): String {
        return userDao.userName(email)?:""
    }

    override fun editarUsuario(user: User): Int {
        return userDao.editarUsuario(user)
    }

}

