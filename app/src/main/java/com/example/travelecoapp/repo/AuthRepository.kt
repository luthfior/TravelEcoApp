package com.example.travelecoapp.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.example.travelecoapp.ui.auth.pref.AuthPreference
import com.example.travelecoapp.ui.auth.pref.AuthUser
import com.example.travelecoapp.util.ResponseMessage
import com.google.android.gms.common.api.ApiException
import com.google.android.recaptcha.RecaptchaException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val authPreference: AuthPreference,
    private val firebaseAuth: FirebaseAuth
) {
    fun loginUser(email: String, password: String): LiveData<ResponseMessage<AuthResult>> =
        liveData { emit(ResponseMessage.Loading())
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(ResponseMessage.Success(result))
        } catch(e: FirebaseAuthInvalidCredentialsException) {
            val errorMessage = when (e.errorCode) {
                "ERROR_INVALID_EMAIL" -> "The email address is badly formatted"
                "ERROR_USER_NOT_FOUND" -> "There is no user record corresponding to this identifier. The user may have been deleted"
                "ERROR_WRONG_PASSWORD" -> "The password is invalid or the user does not have a password"
                // Tambahkan penanganan kesalahan lainnya sesuai kebutuhan
                else -> "Invalid credentials"
            }
            emit(ResponseMessage.Error(errorMessage))
        } catch(e: RecaptchaException) {
            emit(ResponseMessage.Error(e.localizedMessage ?: "An error occurred"))
        } catch(e: Exception) {
            emit(ResponseMessage.Error(e.localizedMessage ?: "An error occurred"))
        }
    }

    fun registerUser(email: String, password: String): LiveData<ResponseMessage<AuthResult>> =
        liveData { emit(ResponseMessage.Loading())
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            emit(ResponseMessage.Success(result))
        } catch(e: FirebaseAuthInvalidCredentialsException) {
            val errorMessage = when (e.errorCode) {
                "ERROR_INVALID_EMAIL" -> "The email address is badly formatted"
                "ERROR_EMAIL_ALREADY_IN_USE" -> "The email address is already in use by another account"
                "ERROR_WRONG_PASSWORD" -> "The password is invalid"
                "ERROR_USER_DISABLED" -> "The user account has been disabled"
                // Tambahkan penanganan kesalahan lainnya sesuai kebutuhan
                else -> "Invalid credentials"
            }
            emit(ResponseMessage.Error(errorMessage))
        } catch(e: RecaptchaException) {
            emit(ResponseMessage.Error(e.localizedMessage ?: "An error occurred"))
        } catch(e: Exception) {
            emit(ResponseMessage.Error(e.localizedMessage ?: "An error occurred"))
        }
    }

    fun googleSignIn(credential: AuthCredential): LiveData<ResponseMessage<AuthResult>> =
        liveData { emit(ResponseMessage.Loading())
        try {
            val result = firebaseAuth.signInWithCredential(credential).await()
            emit(ResponseMessage.Success(result))
        } catch(e: ApiException) {
            emit(ResponseMessage.Error(e.localizedMessage as String))
        } catch(e: FirebaseAuthInvalidCredentialsException) {
            val errorMessage = when (e.errorCode) {
                "ERROR_INVALID_EMAIL" -> "The email address is badly formatted"
                "ERROR_EMAIL_ALREADY_IN_USE" -> "The email address is already in use by another account"
                "ERROR_WRONG_PASSWORD" -> "The password is invalid"
                "ERROR_USER_DISABLED" -> "The user account has been disabled"
                // Tambahkan penanganan kesalahan lainnya sesuai kebutuhan
                else -> "Invalid credentials"
            }
            emit(ResponseMessage.Error(errorMessage))
        } catch(e: RecaptchaException) {
            emit(ResponseMessage.Error(e.localizedMessage ?: "An error occurred"))
        } catch(e: Exception) {
            emit(ResponseMessage.Error(e.localizedMessage ?: "An error occurred"))
        }
    }

    suspend fun login() {
        authPreference.login()
    }

    suspend fun logout() {
        authPreference.logout()
    }

    suspend fun saveUser(user: AuthUser) {
        authPreference.saveAuthToken(user)
    }

    fun getUser(): LiveData<AuthUser> {
        return authPreference.getAuthToken().asLiveData()
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            authPreference: AuthPreference,
            firebaseAuth: FirebaseAuth
        ): AuthRepository = instance ?: synchronized(this) {
            instance ?: AuthRepository(authPreference, firebaseAuth)
        }.also {
            instance = it
        }
    }
}