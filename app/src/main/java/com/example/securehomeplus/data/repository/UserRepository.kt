package com.example.securehomeplus.data.repository

import com.example.securehomeplus.data.database.dao.UserDao
import com.example.securehomeplus.data.database.entities.User

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun loginUser(email: String, password: String): User? {
        return userDao.loginUser(email, password)
    }

    suspend fun isUserRegistered(email: String): Boolean {
        return userDao.getUserByEmail(email) != null
    }
}
