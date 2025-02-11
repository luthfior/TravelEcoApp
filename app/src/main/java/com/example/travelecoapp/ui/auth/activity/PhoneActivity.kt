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
import com.example.travelecoapp.R
import com.example.travelecoapp.databinding.ActivityPhoneBinding
import com.example.travelecoapp.ui.customview.EditButton
import com.example.travelecoapp.ui.customview.EditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit

class PhoneActivity : AppCompatActivity()  {

    private var _binding: ActivityPhoneBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var ccp: CountryCodePicker
    private lateinit var signupPhoneNumber: EditText
    private lateinit var signupButton: EditButton
    private lateinit var verificationID: String
    private lateinit var phoneNumber: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPhoneBinding.inflate(layoutInflater)
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
        ccp = binding!!.edRegisterCcp
        signupPhoneNumber = binding!!.edRegisterPhoneNumber
        ccp.registerCarrierNumberEditText(signupPhoneNumber)
        signupButton = binding!!.btnSignup
        binding?.apply {
            signupButton.setOnClickListener {
                if (edRegisterPhoneNumber.error == null
                    && edRegisterPhoneNumber.text!!.isNotEmpty()
                ) {

                    btnSignup.isEnabled = true
                    phoneNumber = ccp.fullNumberWithPlus
                    sendVerificationCode(phoneNumber)

                } else {
                    Toast.makeText(
                        this@PhoneActivity,
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
                Toast.makeText(applicationContext, resources.getString(R.string.invalid_phone_number), Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                Log.d("TAG","onCodeSent:$verificationId")
                verificationID = verificationId
                resendToken = token
                val intent = Intent(applicationContext, OtpActivity::class.java)
                intent.putExtra(RESEND_TOKEN, resendToken)
                intent.putExtra(OTP_NUMBER, verificationID)
                intent.putExtra(PHONE_NUMBER, phoneNumber)
                startActivity(intent)
            }
        }

        binding?.txtIsLogin?.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding?.btnChangeLanguage?.setOnClickListener {
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
        }

        playAnimation()
    }

    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks!!) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun playAnimation() {
        val message = ObjectAnimator.ofFloat(binding?.messageTextView, View.ALPHA, 1F).setDuration(500)
        val phoneNumber = ObjectAnimator.ofFloat(binding?.phoneLayout, View.ALPHA, 1F).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding?.btnSignup, View.ALPHA, 1F).setDuration(500)
        val bottomText = ObjectAnimator.ofFloat(binding?.tableLayout, View.ALPHA, 1F).setDuration(500)

        AnimatorSet().apply {
            playSequentially(message, phoneNumber, btnRegister, bottomText)
            startDelay = 500
        }.start()
    }

    companion object {
        const val OTP_NUMBER = "otp_number"
        const val RESEND_TOKEN = "resend_token"
        const val PHONE_NUMBER = "phone_number"
    }
}