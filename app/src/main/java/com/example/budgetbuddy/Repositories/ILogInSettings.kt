package com.example.budgetbuddy.Repositories

interface ILoginSettings {
    suspend fun getLastLoggedUser(): String?
    suspend fun setLastLoggedUser(user: String)
}