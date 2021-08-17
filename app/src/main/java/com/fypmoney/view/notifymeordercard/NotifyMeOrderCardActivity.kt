package com.fypmoney.view.notifymeordercard

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fypmoney.R
import com.fypmoney.util.Utility
import kotlinx.android.synthetic.main.activity_notify_me_order_card.*


class NotifyMeOrderCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notify_me_order_card)
        val uri: Uri =
            Uri.parse("android.resource://" + packageName + "/" + R.raw.notify_order_card)
        video.setMediaController(null)
        video.setVideoURI(uri)
        video.setOnPreparedListener {
            it.isLooping = true
            video.start()
        }
        notify_btn.setOnClickListener {
            Utility.showToast(resources.getString(R.string.thanks_we_will_keep_you_notify))
            finish()
        }
    }
}