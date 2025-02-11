package com.example.travelecoapp.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.travelecoapp.repo.AuthRepository
import com.example.travelecoapp.ui.auth.pref.AuthPreference
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

private const val NAME_DATASTORE = "traveleco"
private const val DESTINATION_DATABASE = "tegaldukuh-packages"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(NAME_DATASTORE)
object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val authPreference = AuthPreference.getInstance(context.dataStore)
        val firebaseAuth = FirebaseAuth.getInstance(FirebaseApp.getInstance())
        return AuthRepository.getInstance(authPreference, firebaseAuth)
    }
}