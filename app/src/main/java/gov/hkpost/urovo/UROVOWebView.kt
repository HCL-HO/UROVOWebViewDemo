package gov.hkpost.urovo

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import timber.log.Timber

object UROVOWebView {

    @SuppressLint("SetJavaScriptEnabled")
    fun WebView.updateUROVOSetting() {
        val webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Timber.i("UROVOWebView: $url Loaded")
            }
        }

        this.webViewClient = webViewClient
        this.settings.javaScriptEnabled = true
    }

    fun WebView.emitBarcodeToWebView(code: String) {
        Timber.i("UROVOWebView: emit barcode - $code")
        this.evaluateJavascript(
            "(function(){" +
                    "var evt = new CustomEvent('urovoBarcodeScanned', { detail: '" + code + "' });" +
                    "window.dispatchEvent(evt);" +
                    "})();"
        ) {}
    }

}