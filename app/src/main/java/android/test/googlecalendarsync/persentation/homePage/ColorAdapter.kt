package android.test.googlecalendarsync.persentation.homePage

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.test.googlecalendarsync.data.model.ColorModel
import android.test.googlecalendarsync.databinding.ItemColorBinding
import android.test.googlecalendarsync.domain.ItemColor
import android.test.googlecalendarsync.persentation.common.BaseAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible

class ColorAdapter : BaseAdapter<ItemColorBinding, ColorAdapter.Navigator, ItemColor>() {

    fun interface Navigator : BaseAdapter.Navigator {
        fun onSelected(item: ItemColor)
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ItemColorBinding
        get() = ItemColorBinding::inflate

    fun getSelected(): ItemColor = data.firstOrNull { it.isSelected } ?: data.first()
    override fun onLoadItem(
        binding: ItemColorBinding,
        item: ItemColor,
        position: Int,
        itemViewType: Int,
        context: Context
    ) {
        binding.layout.setOnClickListener {
            navigator.onSelected(item)
        }
        binding.outline.isVisible = item.isSelected
        binding.imgColor.imageTintList =
            ColorStateList.valueOf(Color.parseColor(item.color.background))
    }
}