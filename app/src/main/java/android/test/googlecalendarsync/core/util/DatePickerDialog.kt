package android.test.googlecalendarsync.core.util

import android.content.DialogInterface
import android.test.googlecalendarsync.databinding.DialogDatePickerBinding
import android.test.googlecalendarsync.persentation.common.DialogBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import java.time.LocalDate

class DatePickerDialog(
    val initialDate: LocalDate = LocalDate.now(),
) : DialogBinding<DialogDatePickerBinding, DatePickerDialog.Navigator>() {
    override fun setupView(binding: DialogDatePickerBinding) {
        binding.datePicker.updateDate(
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        )
        binding.datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            val selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
            navigator?.onDatePicked(selectedDate)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val year = binding.datePicker.year
        val monthOfYear = binding.datePicker.month
        val dayOfMonth = binding.datePicker.dayOfMonth
        val selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
        navigator?.onDatePicked(selectedDate)
    }

    interface Navigator {
        fun onDatePicked(date: LocalDate)
    }

    companion object {
        fun showPicker(fragmentManager: FragmentManager, initialDate: LocalDate?, onSelected: (DatePickerDialog, LocalDate) -> Unit) {
            DatePickerDialog(initialDate = initialDate ?: LocalDate.now()).apply {
                setNavigator(object : Navigator {
                    override fun onDatePicked(date: LocalDate) {
                        onSelected(this@apply, date)
                    }

                })
            }.show(fragmentManager, "DatePicker")
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> DialogDatePickerBinding
        get() = DialogDatePickerBinding::inflate
}