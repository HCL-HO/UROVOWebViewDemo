package gov.hkpost.urovo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import gov.hkpost.urovo.databinding.ActivityMainBinding
import gov.hkpost.urovo.UROVOWebView.emitBarcodeToWebView
import gov.hkpost.urovo.UROVOWebView.updateUROVOSetting
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val LANDING_PAGE_URL = "file:///android_asset/index.html"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val webView = binding.webview
        webView.updateUROVOSetting()
        webView.loadUrl(LANDING_PAGE_URL)
        binding.button.setOnClickListener {
            webView.emitBarcodeToWebView("This-is-the-barcode-received-by-urovo")
        }
    }

}