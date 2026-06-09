package com.test.myapplication

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import `in`.digio.digiostateless.DigioStatelessConfig
import `in`.digio.digiostateless.DigioStatelessFlow
import `in`.digio.digiostateless.DigioStatelessUpiConfig
import `in`.digio.digiostateless.enums.DigioStatelessFeature
import `in`.digio.digiostateless.enums.DigioStatelessPspMode
import `in`.digio.digiostateless.enums.DigioStatelessUpiServiceMode
import `in`.digio.digiostateless.model.DigioStatelessError
import `in`.digio.digiostateless.model.DigioStatelessResponse

class StatelessActivity : ComponentActivity() {
    private lateinit var logTextView: TextView

    private val digioLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            when (result.resultCode) {
                RESULT_OK -> {
                    val success = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        result.data?.getParcelableExtra("result", DigioStatelessResponse::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        result.data?.getParcelableExtra<DigioStatelessResponse>("result")
                    }
                    Log.e("CheckResponse","success ")
                    appendLog(success.toString())
                }
                RESULT_CANCELED -> {
                    val error = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        result.data?.getParcelableExtra("result", DigioStatelessError::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        result.data?.getParcelableExtra<DigioStatelessError>("result")
                    }
                    Log.e("CheckResponse","faliure ")

                    appendLog(error.toString())

                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(16, 16, 16, 16)
        }

        val topSpacer = Space(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }
        val statelssUpiRpdBtn = Button(this).apply {
            text = "UPI RPD"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }
        logTextView = TextView(this).apply {
            text = "Logs will appear here..."
            movementMethod = ScrollingMovementMethod()
            isVerticalScrollBarEnabled = true
            setPadding(16, 16, 16, 16)

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                400 // fixed height for scrolling area
            )
        }

        val bottomSpacer = Space(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }
        statelssUpiRpdBtn.setOnClickListener {
            startStatelessUpi(
                serviceMode = DigioStatelessUpiServiceMode.REVERSE_PENNY_DROP,
                requestId = "KID260603162158400XEXUZA7AC187MF"
            )

            appendLog("UPI RPD initiated")
        }

        rootLayout.addView(topSpacer)

        rootLayout.addView(statelssUpiRpdBtn)
        rootLayout.addView(logTextView)
        rootLayout.addView(bottomSpacer)

        setContentView(rootLayout)
    }


    private fun startStatelessUpi(
        serviceMode: DigioStatelessUpiServiceMode,
        requestId: String,
        pspMode: DigioStatelessPspMode = DigioStatelessPspMode.SDK_PICKER,
        pspPackage: String? = null,
        upiIntentUri: String? = null,
        loaderColor: Int? = null
    ) {
        if (requestId.isBlank()) {
            appendLog("Set a valid requestId in TestCameraScreen for ${serviceMode.name} before launching.")
            return
        }
        val txnId = "TXN${System.currentTimeMillis()}${(1000..9999).random()}"
        val config = DigioStatelessConfig(
            environment = `in`.digio.digiostateless.enums.DigioEnvironment.PRODUCTION,
            clientId = "Your client ID",
            features = listOf(DigioStatelessFeature.UPI),
            upiConfig = DigioStatelessUpiConfig(
                serviceMode = serviceMode,
                requestId = requestId,
                pspMode = pspMode,
                pspPackage = pspPackage,
                upiIntentUri = upiIntentUri,
                logoUrl = "https://resources.groww.in/web-assets/img/website-logo/groww_logo.webp",
                loaderColor = loaderColor
            ),
            token =
                SdkJwtGenerator.generateToken(
                    "Your client Secret",
                    txnId,
//                "TEST_flow_Selfie_RPD",
                    "RPD",
                    "akash.kumar@digio.in",
                    864000
                )
        )
        Log.e("HostCLIENT", "UPI ${serviceMode.name} token: ${config.token}")

        DigioStatelessFlow.start(
            activity = this,
            config = config,
            launcher = digioLauncher,
            eventListener = { event ->
                // event = JSONObject {"screen": "...", "action": "..."}
                Log.e("CheckEvent","${event}")
            }
        )
    }


    private fun appendLog(message: String) {
        logTextView.append("\n$message")

        val scrollAmount = logTextView.layout?.getLineTop(logTextView.lineCount)
            ?.minus(logTextView.height) ?: 0

        if (scrollAmount > 0) {
            logTextView.scrollTo(0, scrollAmount)
        } else {
            logTextView.scrollTo(0, 0)
        }
    }
}
