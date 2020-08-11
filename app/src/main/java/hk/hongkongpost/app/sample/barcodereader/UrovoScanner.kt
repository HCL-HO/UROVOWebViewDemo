package hk.hongkongpost.app.sample.barcodereader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import timber.log.Timber
import java.util.HashMap


class UrovoScanner(activity: AppCompatActivity, barcodeListener: BarcodeListener) : LifecycleObserver {
    private val SCAN_ACTION = "urovo.rcv.message" //扫描结束action
    private val SOUND_SCAN = "key_sound_scan"
    private val activity: AppCompatActivity

    private val soundpool: SoundPool
    private var soundid = 0
    private val soundIdMap: Map<String, Int>

    init {
        activity.lifecycle.addObserver(this)
        this.activity = activity

        val aa = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .build()
        soundpool = SoundPool.Builder().setMaxStreams(2).setAudioAttributes(aa).build()
        soundIdMap = HashMap<String, Int>()
        soundIdMap[SOUND_SCAN] = soundpool.load("/etc/Scan_new.ogg", 1)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun register() {
        try {
            val filter = IntentFilter()
            filter.addAction(SCAN_ACTION)
            this.activity.registerReceiver(mScanReceiver, filter)
        } catch (e: Exception) {
            Timber.e("[mScanReceiver][resumeAction] error:$e")
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun unregister() {
        this.activity.unregisterReceiver(mScanReceiver)
    }


    fun playScanSound() {
        if (soundpool != null && soundIdMap != null) {
            val i = soundpool!!.play(soundIdMap!![SOUND_SCAN] ?: error(""), 1f, 1f, 0, 0, 1f)
        }
    }

    private val mScanReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                playScanSound()
                val barcode = intent.getByteArrayExtra("barocode")
                val barocodelen = intent.getIntExtra("length", 0)
                //                byte temp = intent.getByteExtra("barcodeType", (byte) 0);
                val code = String(barcode, 0, barocodelen)
                barcodeListener.onScanned(code)
                Timber.i(code)
            } catch (e: java.lang.Exception) {
                Timber.e("[mScanReceiver][onReceive] error:$e")
            }
        }
    }
}

interface BarcodeListener {
    fun onScanned(code: String)
}
