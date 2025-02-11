package com.example.travelecoapp.ui.auth.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.travelecoapp.*
import com.example.travelecoapp.database.Users
import com.example.travelecoapp.databinding.ActivityLoginBinding
import com.example.travelecoapp.model.AuthViewModel
import com.example.travelecoapp.model.ViewModelFactory
import com.example.travelecoapp.ui.auth.pref.AuthUser
import com.example.travelecoapp.ui.customview.EditButton
import com.example.travelecoapp.ui.customview.EditText
import com.example.travelecoapp.ui.detail.DetailActivity
import com.example.travelecoapp.util.ResponseMessage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: EditButton
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var usernameManual: String
    private lateinit var emailManual: String
    private lateinit var authViewModel: AuthViewModel
    private val database = FirebaseDatabase.getInstance().reference.child("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()

        setupModel()

        loginEmail = binding!!.edLoginEmail
        loginPassword = binding!!.edLoginPassword

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding?.btnIsRegister?.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val googleTextView: TextView = binding?.loginWithGoogle?.getChildAt(0) as TextView
        googleTextView.text = resources.getString(R.string.button_google_text)

        binding?.loginWithGoogle?.setOnClickListener {
            signInWithGoogle()
        }

        setupAction()
    }

    private fun setupAction() {
        binding?.apply {
            btnChangeLanguage.setOnClickListener {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
            }
            loginButton = btnLogin
            loginButton.setOnClickListener {
                if (loginEmail.error == null
                    && loginPassword.error == null
                    && loginEmail.text!!.isNotEmpty()
                    && loginPassword.text!!.isNotEmpty()) {
                    val email = binding?.edLoginEmail?.text.toString().trim()
                    val password = binding?.edLoginPassword?.text.toString().trim()
                    authViewModel.userLogin(email, password).observe(this@LoginActivity) { response ->
                        when (response) {
                            is ResponseMessage.Loading -> {}
                            is ResponseMessage.Success -> {
                                saveUserData(
                                    AuthUser(
                                        response.data?.user?.displayName.toString(),
                                        response.data?.user?.uid.toString(),
                                        true
                                    )
                                )
                                if (auth.currentUser!!.isEmailVerified) {
                                    database.child(response.data?.user?.uid.toString()).addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                val user = snapshot.getValue(Users::class.java)

                                                usernameManual = user?.name!!
                                                emailManual = user.email!!
                                            }
                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                            Log.d("ProfileActivity", "Failed")
                                        }
                                    })
                                    val intent = Intent(this@LoginActivity, DetailActivity::class.java)
                                    val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                                    val editor = sharedPref.edit()
                                    editor.putString("displayName", usernameManual)
                                    editor.putString("email", emailManual)
                                    intent.putExtra(FROM_LOGIN, true)
                                    editor.apply()
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        resources.getString(R.string.not_yet_verified),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            is ResponseMessage.Error -> {
                                Log.d(
                                    "OnErrorLogin: ",
                                    "Response: ${response.message.toString()}"
                                )
                                when (response.message) {
                                    "There is no user record corresponding to this identifier. The user may have been deleted" ->
                                        Toast.makeText(this@LoginActivity, resources.getString(R.string.not_found), Toast.LENGTH_SHORT).show()
                                    "The password is invalid or the user does not have a password" ->
                                        Toast.makeText(this@LoginActivity, resources.getString(R.string.wrong_pass_email), Toast.LENGTH_SHORT).show()
                                    "The email address is badly formatted" ->
                                        Toast.makeText(this@LoginActivity, resources.getString(R.string.wrong_pass_email), Toast.LENGTH_SHORT).show()
                                    else ->
                                        Toast.makeText(this@LoginActivity, resources.getString(R.string.not_registered), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        R.string.check_validity_field,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                authViewModel.googleSignIn(credential).observe(this@LoginActivity) { response ->
                    when (response) {
                        is ResponseMessage.Loading -> {}
                        is ResponseMessage.Success -> {
                            saveUserData(
                                AuthUser(
                                    response.data?.user?.displayName.toString(),
                                    response.data?.user?.uid.toString(),
                                    true
                                )
                            )
                            val currentUserUid = auth.currentUser?.uid
                            if (currentUserUid != null) {
                                val user = Users(account.displayName, account.email, "", "", currentUserUid, getCurrentDateTime())

                                database.child(currentUserUid).setValue(user).addOnCompleteListener { tasks ->
                                    if (tasks.isSuccessful) {
                                        Log.d("LoginActivity", "Start MainActivity")
                                        val intent = Intent(this@LoginActivity, DetailActivity::class.java)
                                        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                                        val editor = sharedPref.edit()
                                        editor.putString("displayName", account.displayName.toString())
                                        editor.putString("email", account.email.toString())
                                        editor.apply()
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Log.d("Login Activity", response.message.toString())
                                        Toast.makeText(this, resources.getString(R.string.error_register), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                        is ResponseMessage.Error -> {
                            Log.d("OnErrorLogin: ", "Response: ${response.message.toString()}")
                            Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    private fun saveUserData(user: AuthUser) {
        authViewModel.saveUser(user)
    }

    private fun setupModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            edLoginEmail.isEnabled = !isLoading
            edLoginPassword.isEnabled = !isLoading
            btnLogin.isEnabled = !isLoading
            progressBar.visibility = View.VISIBLE
        }
    }

    companion object {
        const val NAME_GOOGLE = "name_google"
        const val EMAIL_GOOGLE = "email_google"
        const val FROM_LOGIN = "from_login"
    }
}