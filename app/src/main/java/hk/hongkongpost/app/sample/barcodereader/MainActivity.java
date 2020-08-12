package hk.hongkongpost.app.sample.barcodereader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import org.jetbrains.annotations.NotNull;


import hk.hongkongpost.app.sample.barcodereader.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String LANDING_PAGE_URL = "file:///android_asset/index.html";

    private ActivityMainBinding binding;
    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        webView = binding.webview;
        UROVOWebView.INSTANCE.updateUROVOSetting(webView);
        getLifecycle().addObserver(new UrovoScanner(this, new BarcodeListener() {
            @Override
            public void onScanned(@NotNull String code) {
                UROVOWebView.INSTANCE.emitBarcodeToWebView(webView, code);
            }
        }));
        webView.loadUrl(LANDING_PAGE_URL);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl(LANDING_PAGE_URL);
            }
        });
    }


}