package com.example.budgetbuddy.UserVerification

import android.util.Log


fun correctEmail(email: String): Boolean {
    val regex = Regex("""\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b""")
    return regex.matches(email)
}
fun correctName(name: String): Boolean{
    Log.d("NAME", "${name.isNotBlank() && name.any { it != ' ' }}")
    return name.isNotBlank() && name.any { it != ' ' }
}

fun correctPasswd(password: String, password2: String): Boolean{
    return password!="" && password == password2
}
