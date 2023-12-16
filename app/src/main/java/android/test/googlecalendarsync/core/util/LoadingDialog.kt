package android.test.googlecalendarsync.core.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.test.googlecalendarsync.R
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible

class LoadingDialog(
    context: Context,
) : Dialog(context) {
    var onBtnCancelClicked: () -> Unit = {}
    private lateinit var btnCancel: TextView
    private val looper: Looper = Looper.getMainLooper()
    private val handler = Handler(looper)
    private val hideLoadingRunnable = Runnable {
        btnCancel.isVisible = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        btnCancel = findViewById(R.id.btn_cancel)
        btnCancel.setOnClickListener {
            onBtnCancelClicked()
        }
    }

    override fun show() {
        super.show()
        btnCancel.isVisible = false
        handler.removeCallbacks(hideLoadingRunnable)
        handler.postDelayed(hideLoadingRunnable, 2500)
    }


}