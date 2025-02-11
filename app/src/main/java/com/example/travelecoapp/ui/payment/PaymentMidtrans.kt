package com.example.travelecoapp.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.travelecoapp.database.Receipt
import com.example.travelecoapp.databinding.ActivityOrderBinding
import com.example.travelecoapp.ui.ReceiptActivity
import com.example.travelecoapp.ui.customview.EditButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class PaymentMidtrans : AppCompatActivity(), TransactionFinishedCallback {

    private var _binding: ActivityOrderBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userName : String
    private lateinit var userEmail : String
    private lateinit var phoneNumber : String
    private lateinit var userCountry: String
    private lateinit var person: String
    private lateinit var orderId: String
    private lateinit var orderList: ArrayList<Receipt>
    private lateinit var formattedPrice: String
    private lateinit var nameProgram: String
    private var convertPerson: Int = 0
    private var times: Double? = null
    private lateinit var orderButton: EditButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        orderList = arrayListOf()

        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-EV6tyZrK3OzoMgJ3") // client_key is mandatory
            .setContext(applicationContext) // context is mandatory
            .setTransactionFinishedCallback(this) // <-- Perbaikan: Set callback ke kelas ini
            .setMerchantBaseUrl("http://travelecoapp.org.preview.services/ppresponse.php/") //set merchant url (required)
            .enableLog(true) // enable sdk log (optional)
            .setColorTheme(
                CustomColorTheme(
                    "#7ADE7A",
                    "#1E641E",
                    "#37FF37"
                )
            ) // set theme. it will replace theme on snap theme on MAP ( optional)
            .setLanguage("id") //`en` for English and `id` for Bahasa
            .buildSDK()

        setupFormValidation()

        auth = FirebaseAuth.getInstance()

        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val displayName = sharedPref.getString("displayName", "")
        val email = sharedPref.getString("email", "")

        userName = displayName!!
        userEmail = email!!
        binding?.etName?.setText(userName)
        binding?.etEmail?.setText(userEmail)
        binding?.etPerson?.setText("2")

        val nameOrder = intent.getStringExtra("Program")
        val imageOrder = intent.getStringExtra("Photo")

        binding?.tvProgram?.text = nameOrder
        Glide.with(this).load(imageOrder).into(binding!!.ivPreview)

        val decimalFormatSymbols = DecimalFormatSymbols(Locale("id", "ID"))
        decimalFormatSymbols.groupingSeparator = '.' // Set separator menjadi titik
        val formatter = DecimalFormat("#,###", decimalFormatSymbols)
        formattedPrice = if (times != null) formatter.format(times) else "0"

        orderButton = binding?.btnSend!!
        orderButton.setOnClickListener {
            database = FirebaseDatabase.getInstance().reference

            val price = intent.getStringExtra("Price")
            val convertPrice = price?.toDouble()
            person = binding?.etPerson?.text.toString()
            convertPerson = person.toInt()
            times = convertPrice?.times(convertPerson)


            userName = binding?.etName?.text.toString()
            userEmail = binding?.etEmail?.text.toString()
            userCountry = binding?.etCountry?.text.toString()
            phoneNumber = binding?.etPhone?.text.toString()
            orderId = "TravelECO-"+System.currentTimeMillis().toString() + ""

            val sharedPrefs = getSharedPreferences("orderId", Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.putString("orderId", orderId)
            editor.apply()

            nameProgram = intent.getStringExtra("Program").toString()
            val transactionRequest = TransactionRequest(orderId, times!!)
            val detail = com.midtrans.sdk.corekit.models.ItemDetails(System.currentTimeMillis().toString(), convertPrice, convertPerson, nameProgram)
            val itemDetails = ArrayList<com.midtrans.sdk.corekit.models.ItemDetails>()
            itemDetails.add(detail)
            uiKitDetails(transactionRequest, userName, userEmail, phoneNumber)
            transactionRequest.itemDetails = itemDetails
            MidtransSDK.getInstance().transactionRequest = transactionRequest
            MidtransSDK.getInstance().startPaymentUiFlow(this)
        }
    }

    // Pindahkan fungsi ini keluar dari onCreate()
    override fun onTransactionFinished(result: TransactionResult?) {
        if (result != null) {
            Log.d("PaymentMidtrans", "Transaction Finished: ${result.response}")
            val intent = Intent(this, ReceiptActivity::class.java).apply {
                putExtra("orderEmail", userEmail)
                putExtra("orderName", userName)
                putExtra("orderPhone", phoneNumber)
                putExtra("orderId", orderId)
                putExtra("orderProgram", intent.getStringExtra("Program") ?: "Unknown Program")
                putExtra("orderOfPeople", person)
                putExtra("orderPrice", formattedPrice)
                putExtra("orderStatus", result.response.transactionId)
            }
            startActivity(intent)
            finish()
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val userOrderRef = database.child("users").child(userId).child("order")
                userOrderRef.get().addOnSuccessListener { dataSnapshot ->
                    val orderList = mutableListOf<Map<String, String?>>()
                    if (dataSnapshot.exists()) {
                        val currentFavorites = dataSnapshot.value as? List<Map<String, String>>
                        currentFavorites?.let { orderList.addAll(it) }
                    }

                    val decimalFormatSymbols = DecimalFormatSymbols(Locale("id", "ID"))
                    decimalFormatSymbols.groupingSeparator = '.' // Set separator menjadi titik
                    val formatter = DecimalFormat("#,###", decimalFormatSymbols)
                    formattedPrice = formatter.format(times)

                    val order = mapOf(
                        "orderEmail" to userEmail,
                        "orderName" to userName,
                        "orderPhone" to phoneNumber,
                        "orderId" to orderId,
                        "orderProgram" to nameProgram,
                        "orderOfPeople" to person,
                        "orderPrice" to formattedPrice,
                        "orderStatus" to result.response?.transactionStatus
                    )

                    if (!orderList.any { it["orderId"] == orderId }) {
                        orderList.add(order as Map<String, String?>)

                        userOrderRef.setValue(orderList)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("PaymentActivity", "Successfully added to the database")
                                } else {
                                    Log.d("PaymentActivity", "Failed to add to database")
                                }
                            }

                    } else {
                        Log.d("PaymentActivity", "Data already exists in the database")
                    }
                }
            }
            when (result.status) {
                TransactionResult.STATUS_SUCCESS -> Log.d("PaymentMidtrans", "Transaction Success: ${result.response.transactionId}")
                TransactionResult.STATUS_PENDING -> Log.d("PaymentMidtrans", "Transaction Pending: ${result.response.transactionId}")
                TransactionResult.STATUS_FAILED -> Log.d("PaymentMidtrans", "Transaction Failed")
                TransactionResult.STATUS_INVALID -> Log.d("PaymentMidtrans", "Transaction Invalid")
                else -> Log.d("PaymentMidtrans", "Unknown Transaction Status")
            }
        } else {
            Log.d("PaymentMidtrans", "Transaction result is null")
        }
    }

    private fun setupFormValidation() {
        val editTexts = listOf(
            binding?.etName,
            binding?.etEmail,
            binding?.etCountry,
            binding?.etPhone,
            binding?.etPerson
        )

        val inputWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateForm()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        // Tambahkan listener ke semua EditText
        editTexts.forEach { editText ->
            editText?.addTextChangedListener(inputWatcher)
        }

        validateForm()
    }

    private fun validateForm() {
        val isNameFilled = !binding?.etName?.text.isNullOrEmpty()
        val isEmailFilled = !binding?.etEmail?.text.isNullOrEmpty()
        val isCountryFilled = !binding?.etCountry?.text.isNullOrEmpty()
        val isPhoneFilled = !binding?.etPhone?.text.isNullOrEmpty()
        val isPersonFilled = !binding?.etPerson?.text.isNullOrEmpty()

        val isFormValid = isNameFilled && isEmailFilled && isCountryFilled && isPhoneFilled && isPersonFilled

        binding?.btnSend?.isEnabled = isFormValid
    }


    private fun uiKitDetails(
        transactionRequest: TransactionRequest,
        name: String,
        email: String,
        phone: String)
    {
        val customerDetails = CustomerDetails()
        customerDetails.firstName = name
        customerDetails.email = email
        customerDetails.phone = phone
        transactionRequest.customerDetails = customerDetails
    }
}
