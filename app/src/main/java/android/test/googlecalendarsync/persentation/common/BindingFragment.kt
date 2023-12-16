package android.test.googlecalendarsync.persentation.common

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding

abstract class BindingFragment<T : ViewBinding> : Fragment() {
    private var _binding: T? = null
    val binding get() = _binding!!
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> T
    lateinit var activity: BindingActivity<*>
    lateinit var navController: NavController
    private lateinit var lifecycleOwner: LifecycleOwner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        activity = requireActivity() as BindingActivity<*>
        lifecycleOwner = activity.lifecycleOwner
        _binding = bindingInflater.invoke(inflater, container, false)
        navController = findNavController()
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(binding, savedInstanceState)
    }

    abstract fun setupView(binding: T, savedInstanceState: Bundle?)
    fun Fragment.createActivityLauncher(onResult: (ActivityResult) -> Unit = {}): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult(), onResult)
    }

}