package hk.hongkongpost.app.sample.barcodereader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.device.ScanManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import timber.log.Timber
import java.util.*


class UrovoScanner(activity: AppCompatActivity, barcodeListener: BarcodeListener) : LifecycleObserver {

    private val SCAN_ACTION = "urovo.rcv.message" //扫描结束action
    private val SOUND_SCAN = "key_sound_scan"
    private var mScanManager: ScanManager = ScanManager()
    private var soundpool: SoundPool = SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100)
    private var soundid = 0
    private var soundIdMap: HashMap<String, Int> = HashMap()
    private val activity = activity

    init {
        initBarcodeScanner()
        initSoundPool()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun registerScanner() {
        try {
            val filter = IntentFilter()
            filter.addAction(SCAN_ACTION)
            this.activity.registerReceiver(mScanReceiver, filter)
        } catch (e: java.lang.Exception) {
            Timber.e("[mScanReceiver][resumeAction] error:$e")
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun unregister(){
        try {
            mScanManager.stopDecode()
            mScanReceiver.let { this.activity.unregisterReceiver(it) }
        } catch (e: java.lang.Exception) {
            Timber.e("[mScanReceiver][onPause] err:$e")
        }
    }


    private val mScanReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                playScanSound()
                val barcode = intent.getByteArrayExtra("barocode")
                val barocodelen = intent.getIntExtra("length", 0)
                //                byte temp = intent.getByteExtra("barcodeType", (byte) 0);
                val barcodeStr = String(barcode, 0, barocodelen)
                barcodeListener.onScanned(barcodeStr)
            } catch (e: java.lang.Exception) {
                Timber.e("[mScanReceiver][onReceive] error:$e")
            }
        }
    }

    fun playScanSound() {
        val i = soundpool.play(soundIdMap[SOUND_SCAN]!!, 1F, 1F, 0, 0, 1F)
    }

    private fun initBarcodeScanner() {
        try {
            mScanManager.openScanner()
            mScanManager.switchOutputMode(0)
            soundid = soundpool.load("/etc/Scan_new.ogg", 1)
        } catch (e: Exception) {
            Timber.e("[mScanReceiver][initBarcodeScanner] error:$e")
        }
    }

    private fun initSoundPool() {
        val aa = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .build()
        soundpool = SoundPool.Builder().setMaxStreams(2).setAudioAttributes(aa).build()
        soundIdMap[SOUND_SCAN] = soundpool.load("/etc/Scan_new.ogg", 1)
    }
}

interface BarcodeListener {
    fun onScanned(code: String)
}
