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


import java.util.HashMap;
import java.util.Map;


import hk.hongkongpost.app.sample.barcodereader.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String LANDING_PAGE_URL = "file:///android_asset/index.html";

    private final static String SCAN_ACTION = "urovo.rcv.message";//扫描结束action
    private static final String SOUND_SCAN = "key_sound_scan";

    protected ScanManager mScanManager;
    protected SoundPool soundpool = null;
    protected int soundid;
    protected Map<String, Integer> soundIdMap;

    private ActivityMainBinding binding;
    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initBarcodeScanner();
        initSoundPool();

        webView = binding.webview;
        UROVOWebView.INSTANCE.updateUROVOSetting(webView);
        webView.loadUrl(LANDING_PAGE_URL);
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UROVOWebView.INSTANCE.emitBarcodeToWebView(webView, "This-is-the-barcode-received-by-urovo");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerScanner(mScanReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            if (mScanManager != null) {
                mScanManager.stopDecode();
            }

            unregisterScanner(mScanReceiver);
        } catch (Exception e) {
            Log.e(TAG, "[mScanReceiver][onPause] err:" + e);
        }
    }

    private void initBarcodeScanner() {
        try {
            mScanManager = new ScanManager();
            mScanManager.openScanner();
            mScanManager.switchOutputMode(0);
            soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
            soundid = soundpool.load("/etc/Scan_new.ogg", 1);

        } catch (Exception e) {
            Log.e(TAG, "[mScanReceiver][initBarcodeScanner] error:" + e);

        }
    }

    private void initSoundPool() {
        AudioAttributes aa = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .build();
        soundpool = new SoundPool.Builder().setMaxStreams(2).setAudioAttributes(aa).build();
        soundIdMap = new HashMap<>();
        soundIdMap.put(SOUND_SCAN, soundpool.load("/etc/Scan_new.ogg", 1));
    }

    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                playScanSound();

                byte[] barcode = intent.getByteArrayExtra("barocode");
                int barocodelen = intent.getIntExtra("length", 0);
//                byte temp = intent.getByteExtra("barcodeType", (byte) 0);

                String barcodeStr = new String(barcode, 0, barocodelen);

                UROVOWebView.INSTANCE.emitBarcodeToWebView(webView, barcodeStr);

            } catch (Exception e) {
                Log.e(TAG, "[mScanReceiver][onReceive] error:" + e);
            }
        }
    };

    public void playScanSound() {
        if (soundpool != null && soundIdMap != null) {
            int i = soundpool.play(soundIdMap.get(SOUND_SCAN), 1, 1, 0, 0, 1);
        }
    }

    public void registerScanner(BroadcastReceiver mScanReceiver) {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(SCAN_ACTION);
            registerReceiver(mScanReceiver, filter);

        } catch (Exception e) {
            Log.e(TAG, "[mScanReceiver][resumeAction] error:" + e);
        }
    }

    public void unregisterScanner(BroadcastReceiver mScanReceiver) {
        if (mScanReceiver != null) {
            unregisterReceiver(mScanReceiver);
        }

    }


}