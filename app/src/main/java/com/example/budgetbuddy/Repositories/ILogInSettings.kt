package com.example.budgetbuddy.Repositories


/**
 * Interfaz dedicada a los métodos de gestión del [LastLoggedUser]
 */
interface ILoginSettings {
    suspend fun getLastLoggedUser(): String?
    suspend fun setLastLoggedUser(user: String)
}