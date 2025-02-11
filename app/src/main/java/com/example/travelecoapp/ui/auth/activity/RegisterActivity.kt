package com.example.travelecoapp.ui.auth.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.travelecoapp.*
import com.example.travelecoapp.database.Users
import com.example.travelecoapp.databinding.ActivityRegisterBinding
import com.example.travelecoapp.model.AuthViewModel
import com.example.travelecoapp.model.ViewModelFactory
import com.example.travelecoapp.ui.customview.EditButton
import com.example.travelecoapp.ui.customview.EditText
import com.example.travelecoapp.util.ResponseMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding

    private val database = FirebaseDatabase.getInstance().reference.child(USERS_CHILD)
    private lateinit var auth: FirebaseAuth
    private lateinit var signupName: EditText
    private lateinit var signupCountry: EditText
    private lateinit var signupEmail: EditText
    private lateinit var phoneNumber: String
    private lateinit var otpNumber: String
    private lateinit var signupPassword: EditText
    private lateinit var signupButton: EditButton
    private lateinit var verificationId: String
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()

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

        signupName = binding!!.edRegisterName
        signupCountry = binding!!.edRegisterCountry
        signupEmail = binding!!.edRegisterEmail
//        phoneNumber = intent.getStringExtra(PhoneActivity.PHONE_NUMBER)!!
//        otpNumber = intent.getStringExtra(OtpActivity.OTP_NUMBER).toString()
//        verificationId = intent.getStringExtra(PhoneActivity.OTP_NUMBER).toString()

//        Log.d(TAG, "Phone number: $phoneNumber")
        signupPassword = binding!!.edRegisterPassword
        signupButton = binding!!.btnSignup

        setupModel()
        setupAction()

        binding?.btnIsLogin?.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupAction() {
        binding?.let { binding ->
            binding.btnChangeLanguage.setOnClickListener {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
            }
            signupButton = binding.btnSignup
            signupButton.setOnClickListener {
                if (binding.edRegisterName.error == null
                    && binding.edRegisterName.text!!.isNotEmpty()
                    && binding.edRegisterCountry.error == null
                    && binding.edRegisterCountry.text!!.isNotEmpty()
                    && binding.edRegisterEmail.error == null
                    && binding.edRegisterPassword.text!!.isNotEmpty()
                    && binding.edRegisterPassword.error == null
                    && binding.edRegisterPassword.text!!.isNotEmpty()
                ) {
                    val name = binding.edRegisterName.text.toString().trim()
                    val country = binding.edRegisterCountry.text.toString().trim()
                    val email = binding.edRegisterEmail.text.toString().trim()
                    val password = binding.edRegisterPassword.text.toString().trim()
                    val phoneNumber = "1234567890"
                    authViewModel.userSignup(email, password).observe(this@RegisterActivity) { response ->
                        when(response) {
                            is ResponseMessage.Loading -> {}
                            is ResponseMessage.Success -> {
                                val currentUserUid = auth.currentUser?.uid
                                if (currentUserUid != null) {
                                    val user = Users(name, email, phoneNumber, country, currentUserUid, getCurrentDateTime())
                                    database.child(currentUserUid).setValue(user).addOnCompleteListener { tasks ->
                                        if (tasks.isSuccessful) {
                                            val currentUser = auth.currentUser
                                            currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(this, resources.getString(R.string.email_ferif), Toast.LENGTH_SHORT).show()
                                                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                                    startActivity(intent)
                                                } else {
                                                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } else {
                                            Log.d(TAG, response.message.toString())
                                            Toast.makeText(this, resources.getString(R.string.failed_acc), Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                            is ResponseMessage.Error -> {
                                Log.d("OnErrorRegister: ", "Response: ${response.message.toString()}")
                                when (response.message) {
                                    "The email address is badly formatted" ->
                                        Toast.makeText(this@RegisterActivity, resources.getString(R.string.failed_acc_format), Toast.LENGTH_SHORT).show()
                                    "The email address is already in use by another account." ->
                                        Toast.makeText(this@RegisterActivity, resources.getString(R.string.failed_acc_email), Toast.LENGTH_SHORT).show()
                                    else ->  Toast.makeText(this@RegisterActivity, R.string.error_register, Toast.LENGTH_SHORT).show()
                                }
                            }

                            else -> {}
                        }
                    }
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        R.string.check_validity_field,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }


    private fun setupModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            edRegisterName.isEnabled = !isLoading
            edRegisterEmail.isEnabled = !isLoading
            edRegisterCountry.isEnabled = !isLoading
            edRegisterPassword.isEnabled = !isLoading
            btnSignup.isEnabled = !isLoading
            progressBar.isVisible = isLoading
        }
    }

    companion object {
        private const val TAG = "Register_Activity"
        private const val USERS_CHILD = "users"
    }
}