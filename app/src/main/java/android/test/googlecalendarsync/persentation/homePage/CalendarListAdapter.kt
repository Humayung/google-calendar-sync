package android.test.googlecalendarsync.persentation.homePage

import android.annotation.SuppressLint
import android.content.Context
import android.test.googlecalendarsync.databinding.ItemDateChipBinding
import android.test.googlecalendarsync.domain.ItemDate
import android.test.googlecalendarsync.persentation.common.BaseAdapter
import android.view.LayoutInflater
import android.view.ViewGroup

class CalendarListAdapter : BaseAdapter<ItemDateChipBinding, CalendarListAdapter.CalendarListNavigator, ItemDate>() {
    var selectedItem: ItemDate? = null
        private set
    interface CalendarListNavigator : Navigator {
        fun onItemClicked(index: Int, itemDate: ItemDate)
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ItemDateChipBinding
        get() = ItemDateChipBinding::inflate

    override fun getItemViewType(position: Int): Int = 0

    override fun getItemCount(): Int = data.size
    override fun onLoadItem(
        binding: ItemDateChipBinding,
        data: ItemDate,
        position: Int,
        itemViewType: Int,
        context: Context
    ) {
        binding.tvDate.text = (data.date.dayOfMonth).toString()
        binding.tvDay.text = getCurrentDay(data.date.dayOfWeek.value)
        binding.cardBackground.isChecked = data.isSelected
        binding.tvDate.isEnabled = data.isSelected
        binding.tvDay.isEnabled = data.isSelected
        binding.cardBackground.setOnClickListener {
            navigator.onItemClicked(position, data)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelected(index: Int) {
        selectedItem = data[index]
        data.onEachIndexed { i, data ->
            data.isSelected = index == i
        }
        notifyDataSetChanged()
    }


    private fun getCurrentDay(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            1 -> "MON"
            2 -> "TUE"
            3 -> "WED"
            4 -> "THU"
            5 -> "FRI"
            6 -> "SAT"
            7 -> "SUN"
            else -> "UNKNOWN"
        }
    }
}