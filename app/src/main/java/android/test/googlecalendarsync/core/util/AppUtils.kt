package android.test.googlecalendarsync.core.util

import android.content.Context
import android.widget.Toast

object AppUtils {
    private var loadingOverlay: LoadingDialog? = null
    fun showLoading(context: Context) {
        loadingOverlay = LoadingDialog(context)
        loadingOverlay?.apply {
            onBtnCancelClicked = {
                dismiss()
            }
            show()
        }
    }

    fun dismissLoading() {
        loadingOverlay?.apply {
            if (isShowing) {
                loadingOverlay?.dismiss()
            }
        }
    }

    fun toast(context: Context,message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}