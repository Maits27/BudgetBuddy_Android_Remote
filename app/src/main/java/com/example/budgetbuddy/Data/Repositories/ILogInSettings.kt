package com.example.budgetbuddy.Data.Repositories

import com.example.budgetbuddy.Data.Room.AuthUser

interface ILoginSettings {
    suspend fun getLastLoggedUser(): String?
    suspend fun setLastLoggedUser(user: String)
}