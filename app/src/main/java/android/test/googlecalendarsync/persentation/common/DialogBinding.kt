package android.test.googlecalendarsync.persentation.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

abstract class DialogBinding<VB : ViewBinding, Navigator> : DialogFragment() {
    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    var runOnBinding = { binding: VB -> }
    protected var navigator: Navigator? = null
        private set

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = bindingInflater.invoke(layoutInflater, null, false)
        setupView(binding)
        dialog?.window?.decorView?.background = null
        runOnBinding(binding)

        return binding.root

    }

    fun setNavigator(navigator: Navigator) {
        this.navigator = navigator
    }

    abstract fun setupView(binding: VB)
}