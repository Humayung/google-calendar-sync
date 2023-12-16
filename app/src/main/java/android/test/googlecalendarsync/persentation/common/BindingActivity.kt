package android.test.googlecalendarsync.persentation.common

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

abstract class BindingActivity<T : ViewBinding> : AppCompatActivity() {
    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater) -> T
    lateinit var lifecycleOwner: LifecycleOwner

    @Suppress("UNCHECKED_CAST")
    protected val binding: T
        get() = _binding as T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(requireNotNull(_binding).root)
        lifecycleOwner = this
        setupView(binding)
    }

    abstract fun setupView(binding: T)

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}