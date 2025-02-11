package com.example.travelecoapp.ui.auth.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travelecoapp.R
import com.example.travelecoapp.databinding.ActivityOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {

    private var _binding: ActivityOtpBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var inputOTP1: EditText
    private lateinit var inputOTP2: EditText
    private lateinit var inputOTP3: EditText
    private lateinit var inputOTP4: EditText
    private lateinit var inputOTP5: EditText
    private lateinit var inputOTP6: EditText
    private lateinit var otpNumber: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOtpBinding.inflate(layoutInflater)
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
        signupButton = binding!!.btnSignup
        inputOTP1 = binding!!.otpEditText1
        inputOTP2 = binding!!.otpEditText2
        inputOTP3 = binding!!.otpEditText3
        inputOTP4 = binding!!.otpEditText4
        inputOTP5 = binding!!.otpEditText5
        inputOTP6 = binding!!.otpEditText6

        otpNumber = intent.getStringExtra(PhoneActivity.OTP_NUMBER).toString()

        @Suppress("DEPRECATION")
        resendToken = intent.getParcelableExtra(PhoneActivity.RESEND_TOKEN)!!
        phoneNumber = intent.getStringExtra(PhoneActivity.PHONE_NUMBER)!!

        playAnimation()
        addTextChangeListener()

        binding?.btnResendOtp?.setOnClickListener {
            resendVerificationCode()
            resendOTPVisibility()
        }
        setupAction()
    }

    private fun setupAction() {
        signupButton.setOnClickListener {
            //collect otp from all the edit texts
            val typedOTP =
                (inputOTP1.text.toString()
                        + inputOTP2.text.toString()
                        + inputOTP3.text.toString()
                        + inputOTP4.text.toString()
                        + inputOTP5.text.toString()
                        + inputOTP6.text.toString())

            if (typedOTP.isNotEmpty()) {
                signupButton.isEnabled = true
                if (typedOTP.length == 6) {
                    val credential = PhoneAuthProvider.getCredential(otpNumber, typedOTP)
                    signInWithPhoneAuthCredential(credential)
                } else {
                    Toast.makeText(this, resources.getString(R.string.invalid_otp_number), Toast.LENGTH_SHORT).show()
                }
            } else {
                signupButton.isEnabled = false
                Toast.makeText(this, resources.getString(R.string.empty_otp), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // OTP verification is successful
                    // Proceed to RegisterActivity
                    val intent = Intent(this, RegisterActivity::class.java)
                    intent.putExtra(OTP_NUMBER, otpNumber)
                    intent.putExtra(PHONE_NUMBER, phoneNumber)
                    startActivity(intent)
                } else {
                    // OTP verification failed
                    Toast.makeText(this, resources.getString(R.string.invalid_otp_number), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun resendOTPVisibility() {
        inputOTP1.setText("")
        inputOTP2.setText("")
        inputOTP3.setText("")
        inputOTP4.setText("")
        inputOTP5.setText("")
        inputOTP6.setText("")
        binding?.btnResendOtp?.visibility = View.INVISIBLE
        binding?.btnResendOtp?.isEnabled = false

        Handler(Looper.myLooper()!!).postDelayed({
            binding?.btnResendOtp?.visibility = View.VISIBLE
            binding?.btnResendOtp?.isEnabled = true
        }, 60000)
    }

    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
//            setupAction()
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG", "onVerificationFailed: $e")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG", "onVerificationFailed: $e")
            }
            binding?.progressBar?.visibility = View.VISIBLE
            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later
            otpNumber = verificationId
            resendToken = token
        }
    }

    private fun addTextChangeListener() {
        inputOTP1.addTextChangedListener(EditTextWatcher(inputOTP1))
        inputOTP2.addTextChangedListener(EditTextWatcher(inputOTP2))
        inputOTP3.addTextChangedListener(EditTextWatcher(inputOTP3))
        inputOTP4.addTextChangedListener(EditTextWatcher(inputOTP4))
        inputOTP5.addTextChangedListener(EditTextWatcher(inputOTP5))
        inputOTP6.addTextChangedListener(EditTextWatcher(inputOTP6))
    }

    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {

            val text = p0.toString()
            when (view.id) {
                R.id.otpEditText1 -> if (text.length == 1) inputOTP2.requestFocus()
                R.id.otpEditText2 -> if (text.length == 1) inputOTP3.requestFocus() else if (text.isEmpty()) inputOTP1.requestFocus()
                R.id.otpEditText3 -> if (text.length == 1) inputOTP4.requestFocus() else if (text.isEmpty()) inputOTP2.requestFocus()
                R.id.otpEditText4 -> if (text.length == 1) inputOTP5.requestFocus() else if (text.isEmpty()) inputOTP3.requestFocus()
                R.id.otpEditText5 -> if (text.length == 1) inputOTP6.requestFocus() else if (text.isEmpty()) inputOTP4.requestFocus()
                R.id.otpEditText6 -> if (text.isEmpty()) inputOTP5.requestFocus() else binding?.btnSignup?.isEnabled = true
            }
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding?.titleTextView, View.ALPHA, 1F).setDuration(500)
        val message = ObjectAnimator.ofFloat(binding?.messageTextView, View.ALPHA, 1F).setDuration(500)
        val isResend = ObjectAnimator.ofFloat(binding?.resendOtpText1, View.ALPHA, 1F).setDuration(500)
        val btnResend = ObjectAnimator.ofFloat(binding?.btnResendOtp, View.ALPHA, 1F).setDuration(500)
        val otpLayout = ObjectAnimator.ofFloat(binding?.otpLayout, View.ALPHA, 1F).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding?.btnSignup, View.ALPHA, 1F).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, message, isResend, btnResend, otpLayout, btnRegister)
            startDelay = 500
        }.start()
    }

    companion object {
        const val PHONE_NUMBER = "phone_number"
        const val OTP_NUMBER = "otp_number"
    }
}