package com.example.travelecoapp.ui.auth.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travelecoapp.R
import com.example.travelecoapp.databinding.ActivityCountryBinding
import com.example.travelecoapp.ui.customview.EditButton
import com.example.travelecoapp.ui.customview.EditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit

class CountryActivity : AppCompatActivity() {

    private var _binding: ActivityCountryBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var ccp: CountryCodePicker
    private lateinit var signupPhoneNumber: EditText
    private lateinit var signupButton: EditButton
    private lateinit var verificationID: String
    private lateinit var phoneNumber: String
    private lateinit var country: String
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCountryBinding.inflate(layoutInflater)
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

        val nameWithGoogle = intent.getStringExtra(LoginActivity.NAME_GOOGLE)
        val emailWithGoogle = intent.getStringExtra(LoginActivity.EMAIL_GOOGLE)

        auth = FirebaseAuth.getInstance()
        ccp = binding!!.edRegisterCcp
        signupPhoneNumber = binding!!.edRegisterPhoneNumber
        ccp.registerCarrierNumberEditText(signupPhoneNumber)
        signupButton = binding!!.btnSignup
        binding?.apply {
            signupButton.setOnClickListener {
                if (edRegisterPhoneNumber.error == null
                    && edRegisterPhoneNumber.text!!.isNotEmpty()
                    && edRegisterCountry.error == null
                    && edRegisterCountry.text!!.isNotEmpty()
                ) {
                    btnSignup.isEnabled = true
                    phoneNumber = ccp.fullNumberWithPlus
                    sendVerificationCode(phoneNumber)
                } else {
                    Toast.makeText(
                        this@CountryActivity,
                        R.string.check_validity_field,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(Intent(applicationContext, RegisterActivity::class.java))
                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext, resources.getString(R.string.failed), Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("TAG","onCodeSent:$verificationId")
                verificationID = verificationId
                resendToken = token
                country = binding?.edRegisterCountry?.text.toString()
                val intent = Intent(applicationContext, OtpActivity::class.java)
                intent.putExtra(NAME_GOOGLE, nameWithGoogle)
                intent.putExtra(EMAIL_GOOGLE, emailWithGoogle)
                intent.putExtra(RESEND_TOKEN, resendToken)
                intent.putExtra(OTP_NUMBER, verificationID)
                intent.putExtra(PHONE_NUMBER, phoneNumber)
                intent.putExtra(COUNTRY_USER, country)
                intent.putExtra(FROM_LOGIN, true)
                startActivity(intent)
            }
        }

        playAnimation()

        binding?.txtIsLogin?.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun playAnimation() {
        val message = ObjectAnimator.ofFloat(binding?.messageTextView, View.ALPHA, 1F).setDuration(500)
        val phoneNumber = ObjectAnimator.ofFloat(binding?.phoneLayout, View.ALPHA, 1F).setDuration(500)
        val country = ObjectAnimator.ofFloat(binding?.countryLayout, View.ALPHA, 1F).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding?.btnSignup, View.ALPHA, 1F).setDuration(500)
        val bottomText = ObjectAnimator.ofFloat(binding?.tableLayout, View.ALPHA, 1F).setDuration(500)

        AnimatorSet().apply {
            playSequentially(message, phoneNumber, country, btnRegister, bottomText)
            startDelay = 500
        }.start()
    }

    companion object {
        const val COUNTRY_USER = "country_user"
        const val OTP_NUMBER = "otp_number"
        const val RESEND_TOKEN = "resend_token"
        const val PHONE_NUMBER = "phone_number"
        const val FROM_LOGIN = "from_login"
        const val NAME_GOOGLE = "name_google"
        const val EMAIL_GOOGLE = "email_google"
    }
}