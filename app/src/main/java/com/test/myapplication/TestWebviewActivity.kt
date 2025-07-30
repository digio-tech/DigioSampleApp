package com.test.myapplication

import android.app.Activity
import android.Manifest

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


class TestWebviewActivity : ComponentActivity() {

    val YOUR_REDIRECTION_URL = "your-redirect-url.in"
    private lateinit var webView: WebView
    private var pendingPermissionRequest: PermissionRequest? = null
    var geoLocationPermissionCallback: GeolocationPermissions.Callback? = null
    var geoLocationOrigin: String? = null
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null
    private var cameraImageUri: Uri? = null

    /** file chooser **/
    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val results = when {
                data == null || data.data == null -> arrayOf(cameraImageUri)
                else -> WebChromeClient.FileChooserParams.parseResult(result.resultCode, data)
            }
            Log.e("CheckUri","path $results")
            fileChooserCallback?.onReceiveValue(results)
        }else {
            fileChooserCallback?.onReceiveValue(null)
        }
        fileChooserCallback = null

    }

    /**  Required permission handler **/
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Check if all permissions are granted
        val allPermissionsGranted = permissions.values.all { it }

        if (allPermissionsGranted) {
            pendingPermissionRequest?.grant(pendingPermissionRequest?.resources)
        } else {
            pendingPermissionRequest?.deny()
        }

        pendingPermissionRequest = null // Clear the pending request
    }

    /** geo permission handler **/
    private val geoPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){ permission ->
        val allPermissionsGranted = permission.values.all { it }

        if (allPermissionsGranted) {
            geoLocationPermissionCallback?.invoke(geoLocationOrigin,true,false)
            geoLocationOrigin = null
        }else{
            geoLocationPermissionCallback?.invoke(geoLocationOrigin,false,false)
            geoLocationOrigin = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val doc_id = intent.getStringExtra("doc_id")
        val identifier = intent.getStringExtra("identifier")
        val token = intent.getStringExtra("token")
        val environment = intent.getStringExtra("environment")
        Log.e("Check_environment","$environment")
        setContentView(setupWebView(
            documentId = doc_id.toString(),
            identifier = identifier.toString(),
            tokenId = token.toString(),
            environment = environment.toString()
        ))
    }


    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        webView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        webView.apply {
            clearCache(true)
            clearHistory()
            stopLoading()
            destroy()
        }
        super.onDestroy()
    }

    /** web view configuration/setup **/
    fun setupWebView(
        documentId: String,
        identifier: String,
        tokenId: String,
        environment: String
    ):WebView {
        webView = WebView(this).apply {
            setupWebViewClient()
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                saveFormData = false
                mediaPlaybackRequiresUserGesture = false
                loadWithOverviewMode = true
                useWideViewPort = true
                javaScriptCanOpenWindowsAutomatically = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                setSupportMultipleWindows(true)
                setGeolocationEnabled(true)
                setGeolocationDatabasePath(context.filesDir.path)
            }
            loadUrl(getUrl(
                environment = environment,
                documentId = documentId, // documentId/NachId/KycId
                identifier = identifier, // identifier --> email/phone number
                tokenId = tokenId // tokenId to by pass 1st factor authentication
            )) // Load a URL
        }

        webView.clearFormData()
        webView.clearHistory()
        webView.clearCache(true)
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView.requestFocus(View.FOCUS_DOWN)
        return webView
    }

    /** Handle web view callbacks **/
    private fun WebView.setupWebViewClient(){
        webViewClient = object :WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                if (url.contains(YOUR_REDIRECTION_URL)){
                    val uri = Uri.parse(request?.url.toString())
                    parseResult(uri)
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }

        webChromeClient = object :WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                geoLocationOrigin = origin
                geoLocationPermissionCallback = callback
                if (
                    ContextCompat.checkSelfPermission(this@TestWebviewActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(this@TestWebviewActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                ) {
                    val permissionsList = arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    geoPermissionLauncher.launch(permissionsList)
                } else {
                    callback?.invoke(origin, true, false)
                }
            }

            override fun onPermissionRequest(request: PermissionRequest?) {
                pendingPermissionRequest = request
                // Check if all necessary permissions are granted
                if (hasAllPermissions()) {
                    request?.grant(request.resources)
                } else {
                    // Request permissions from the user
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                fileChooserCallback?.onReceiveValue(null) // Cancel any previous callbacks
                fileChooserCallback = filePathCallback

                /** create chooser option for camera or file manager **/
                val takePictureIntent = getCameraIntent()

                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*" // Allow all file types
                }
                // Combine intents in a chooser
                val intentArray = takePictureIntent?.let { arrayOf(it) }
                val chooserIntent = Intent(Intent.ACTION_CHOOSER).apply {
                    putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                    putExtra(Intent.EXTRA_TITLE, "Choose an option")
                    putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                }

                fileChooserLauncher.launch(chooserIntent)
                return true
            }
        }
    }


    /**
     *  Url construction pass required data
     */
    fun getUrl(environment: String, documentId:String,identifier:String, tokenId:String): String {

        val baseUrl = if (environment.equals("PRODUCTION", true)) {
            "https://app.digio.in"
        } else {
            "https://ext.digio.in"
        }
        val logo = "https://www.digio.in/images/digio_blue.png" // corporate logo
        val primaryColor = "#0261B0"
        val secondaryColor = "#141414"
        val fontUrl = "https://fonts.googleapis.com/css2?family=Unbounded:wght@200&display=swap"
        val fontFamily = "Unbounded"
        val fontFormat = ""

        val txnId = Math.random().toString() // random number

        val urlBuilder = StringBuilder(baseUrl)
        urlBuilder.append("/#/gateway/login/").append(documentId).append("/")
            .append(txnId).append("/").append(identifier)
        val params = HashMap<String, String>()
        if (!TextUtils.isEmpty(logo)) {
            params["logo"] = logo
        }
        if (!TextUtils.isEmpty(tokenId)) {
            params["token_id"] = tokenId
        }
        val colorObject = JSONObject()

        if (!TextUtils.isEmpty(primaryColor)) {
            colorObject.put(
                "PRIMARY_COLOR", primaryColor
            )
        }
        if (!TextUtils.isEmpty(secondaryColor) ) {
            colorObject.put(
                "SECONDARY_COLOR", secondaryColor
            )
        }
        if (!TextUtils.isEmpty(fontFamily)) {
            colorObject.put(
                "FONT_FAMILY", fontFamily
            )
        }
        if (!TextUtils.isEmpty(fontUrl)) {
            colorObject.put(
                "FONT_URL", fontUrl
            )
        }
        if (!TextUtils.isEmpty(fontFormat)) {
            colorObject.put(
                "FONT_FORMAT", fontFormat
            )
        }
        try {
            params["theme"] =
                URLEncoder.encode(colorObject.toString(), StandardCharsets.UTF_8.toString())
        } catch (ignore: Exception) {

        }

        params["redirect_url"] = "https://${YOUR_REDIRECTION_URL}"

        var initialised = false
        for ((key, value) in params) {
            urlBuilder.append(if (initialised) "&" else "?")
            initialised = true
            urlBuilder.append(key).append("=").append(value)
        }
        Log.d("Construct_Url","$urlBuilder")

        return urlBuilder.toString()
    }

    /**
     *  Parse result and redirect your screen as per your requirement
     */
    fun parseResult(uri: Uri){
        val status = uri.getQueryParameter("status")
        val digioDocId = uri.getQueryParameter("digio_doc_id")
        val message = uri.getQueryParameter("message")
        println("DigioResponse: status $status message $message")

        // From here you can navigate to your required screen
        val resultIntent = Intent()
        resultIntent.putExtra("result_key", uri.toString())
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    /**
     *  Helper method to check if all permissions are granted.
     *  Add all required permission as per your requirement
     * */
    private fun hasAllPermissions(): Boolean {
        val permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /** Create file in app inter cache **/
    private fun createImageFile(): File {
        // Use the internal cache directory for temporary storage
        val storageDir = cacheDir
        return File.createTempFile(
            "IMG_${System.currentTimeMillis()}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    /** Configure camera intent to capture image **/
    private fun getCameraIntent(): Intent? {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                val photoFile = createImageFile()
                cameraImageUri = FileProvider.getUriForFile(
                    this,
                    "${applicationContext.packageName}.fileprovider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
                return takePictureIntent
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }
}
