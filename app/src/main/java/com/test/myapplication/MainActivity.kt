package com.test.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity

import com.test.myapplication.databinding.ActivityMainBinding
import `in`.digio.sdk.gateway.Digio
import `in`.digio.sdk.gateway.enums.DigioEnvironment
import `in`.digio.sdk.gateway.enums.DigioServiceMode
import `in`.digio.sdk.gateway.event.model.GatewayEvent
import `in`.digio.sdk.gateway.interfaces.DigioSuccessFailureInterface
import `in`.digio.sdk.gateway.model.DigioConfig
import `in`.digio.sdk.gateway.model.DigioSdkResponse
import `in`.digio.sdk.gateway.model.DigioTheme
import `in`.digio.sdk.gateway.model.OtherParams

class MainActivity : ComponentActivity(), DigioSuccessFailureInterface {
    val digio = Digio()
    val digioConfig = DigioConfig()
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//ENA250818185521930SJ2CS2MNBP9FAP
        // Set default values
        binding.documentIdEdit.setText("DID26010612575936695V1S189XJ18ZU") // enter your Request ID
        binding.emailMobileEdit.setText("akash.kumar@digio.in") // identifier email/phone
        binding.gwtEdit.setText("GWT260105170017251CLGKYRDL4AHNZS") // token optional

        binding.serviceModeEdit.setText(DigioServiceMode.OTP.toString())
        binding.envEdit.setText(DigioEnvironment.PRODUCTION.toString())
        setServiceModeDropDown()
        setEnvironmentDropDown()

        binding.startWebview.setOnClickListener{
            val documentId = binding.documentIdEdit.text?.toString().orEmpty().trim()
            val emailMobile = binding.emailMobileEdit.text?.toString().orEmpty().trim()
            val tokenId =  binding.gwtEdit.text.toString().trim()
            var environment =  digioConfig.environment.name.trim()
            val intent = Intent(this, TestWebviewActivity::class.java).apply {
                putExtra("doc_id", documentId)
                putExtra("identifier", emailMobile)
                putExtra("token", tokenId)
                putExtra("environment", environment)
            }
            startActivity(intent)

        }
        binding.signNowBtm.setOnClickListener {
            val documentId = binding.documentIdEdit.text?.toString().orEmpty()
            val emailMobile = binding.emailMobileEdit.text?.toString().orEmpty()

            if (documentId.isEmpty() || emailMobile.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please fill in all forms",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                signNow()
            }
        }

        val otherParams = OtherParams().apply {
            // whitelabelType = "footer"
        }
        digioConfig.otherParams = otherParams

        val theme = DigioTheme().apply {
            // primaryColor = android.R.color.holo_red_dark
            primaryColorHex = "#0261B0"
            fontFamily = "Unbounded"
            secondaryColorHex = "#141414"
            fontUrl = "https://fonts.googleapis.com/css2?family=Unbounded:wght@200&display=swap"
            // fontFormat = ""
        }
        digioConfig.theme = theme
        digioConfig.faqButton = android.R.drawable.ic_menu_help
        digioConfig.closeButton = android.R.drawable.ic_menu_close_clear_cancel
        digioConfig.logo = "https://www.digio.in/images/digio_blue.png" // Your company logo URL
        digioConfig.linkApproach = false

        digioConfig.environment = DigioEnvironment.SANDBOX // SANDBOX or PRODUCTION
        digioConfig.serviceMode = DigioServiceMode.OTP

        digio.initSession(this@MainActivity, this)

    }

    private fun signNow() {
        try {
            digioConfig.gToken = binding.gwtEdit.text.toString()
            digioConfig.requestId = binding.documentIdEdit.text.toString()
            digioConfig.userIdentifier = binding.emailMobileEdit.text.toString()

            digio.startSession(
                digioConfig,
                this
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setServiceModeDropDown() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.service_modes,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.serviceModeSpiner.adapter = adapter

        binding.serviceModeSpiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                binding.serviceModeEdit.setText(DigioServiceMode.valueOf(selectedItem).toString())
                digioConfig.serviceMode = DigioServiceMode.valueOf(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.serviceModeEdit.setText(DigioServiceMode.OTP.toString())
                digioConfig.serviceMode = DigioServiceMode.OTP
            }
        }
    }

    private fun setEnvironmentDropDown() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.environment,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.environmentSpiner.adapter = adapter

        binding.environmentSpiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                val env = DigioEnvironment.valueOf(selectedItem)
                binding.envEdit.setText(env.toString())
                digioConfig.environment = env

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.envEdit.setText(DigioEnvironment.PRODUCTION.toString())
                digioConfig.environment = DigioEnvironment.PRODUCTION
            }
        }
    }



    override fun onFailure(sdkResponse: DigioSdkResponse?) {
        binding.response.setText(sdkResponse.toString())
        Log.d(TAG, "onFailure: $sdkResponse")
    }

    override fun onSuccess(sdkResponse: DigioSdkResponse?) {
        binding.response.setText(sdkResponse.toString())
        Log.d(TAG, "onSuccess: " + sdkResponse);
    }

    override fun onUpdateEvent(event: GatewayEvent?) {
        binding.update.setText(event.toString())
        Log.d(TAG, "onUpdateEvent: " + event);
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
