package android.test.googlecalendarsync.core.util

import android.content.DialogInterface
import android.test.googlecalendarsync.databinding.DialogTimePickerBinding
import android.test.googlecalendarsync.persentation.common.DialogBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import java.time.LocalTime

class TimePickerDialog(
    val initialTime: LocalTime = LocalTime.now()
) : DialogBinding<DialogTimePickerBinding, TimePickerDialog.Navigator>() {
    override fun setupView(binding: DialogTimePickerBinding) {
        binding.timePicker.apply {
            hour = initialTime.hour
            minute = initialTime.minute
            setIs24HourView(true)
        }
        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            val selectedDate = LocalTime.of(hourOfDay, minute)
            navigator?.onTimePicked(selectedDate)
        }
        binding.timePicker.setIs24HourView(true);


    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val hourOfDay = binding.timePicker.hour
        val minute = binding.timePicker.minute
        val selectedDate = LocalTime.of(hourOfDay, minute)
        navigator?.onTimePicked(selectedDate)
    }

    interface Navigator {
        fun onTimePicked(time: LocalTime)
    }

    companion object {
        fun showDialog(fragmentManager: FragmentManager, initialTime: LocalTime?, onSelected: (TimePickerDialog, LocalTime) -> Unit) {
            TimePickerDialog(initialTime = initialTime ?: LocalTime.now()).apply {
                setNavigator(object : Navigator {
                    override fun onTimePicked(time: LocalTime) {
                        onSelected(this@apply, time)
                    }

                })
            }.show(fragmentManager, "TimePicker")
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> DialogTimePickerBinding
        get() = DialogTimePickerBinding::inflate
}