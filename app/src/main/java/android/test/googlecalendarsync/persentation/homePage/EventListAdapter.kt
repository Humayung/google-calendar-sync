package android.test.googlecalendarsync.persentation.homePage

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.test.googlecalendarsync.R
import android.test.googlecalendarsync.data.model.ColorModel
import android.test.googlecalendarsync.data.model.EventModel
import android.test.googlecalendarsync.databinding.ItemCardEventBinding
import android.test.googlecalendarsync.persentation.common.BaseAdapter
import android.test.googlecalendarsync.core.util.DateTimeFormatter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

class EventListAdapter :
    BaseAdapter<ItemCardEventBinding, EventListAdapter.Navigator, EventModel>() {
    interface Navigator : BaseAdapter.Navigator {
        fun onItemClicked(item: EventModel)
        fun getColor(key: String?, item: EventModel): ColorModel?
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ItemCardEventBinding
        get() = ItemCardEventBinding::inflate

    override fun onLoadItem(
        binding: ItemCardEventBinding,
        item: EventModel,
        position: Int,
        itemViewType: Int,
        context: Context
    ) {
        binding.tvTitle.text = item.title ?: "No title"
        item.title ?: binding.tvTitle.setTypeface(binding.tvTitle.typeface, Typeface.ITALIC);


        val startDateTime = item.start
        val endDateTime = item.end
        val formattedStartTime = startDateTime.format(DateTimeFormatter.HHmm)
        val formattedEndTime = endDateTime.format(DateTimeFormatter.HHmm)
        binding.tvTime.text = "$formattedStartTime -  $formattedEndTime"
        binding.tvDescription.text = item.description ?: "No description"
        item.description ?: binding.tvDescription.setTypeface(binding.tvDescription.typeface, Typeface.ITALIC)
        binding.card.setOnClickListener {
            binding.tvDescription.isVisible = binding.tvDescription.isVisible.not()
        }
        navigator.getColor(item.colorId, item)?.let {
            binding.tvTitle.setTextColor(Color.parseColor(it.foreground))
            binding.card.setCardBackgroundColor(Color.parseColor(it.background))
        } ?: run{
            binding.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.alpha_20_purple))
            binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.black))
        }
        val durationMinutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime)
        binding.tvDuration.text = formatMinuteToHoursDuration(durationMinutes)
    }


    private fun formatMinuteToHoursDuration(totalMinutes: Long): String {
        val remainder = totalMinutes % 60
        val unit = if ((totalMinutes / 60f).roundToInt() > 1) "hours" else "hour"
        if (totalMinutes / (60).toLong() == (24).toLong()) return "All day"
        if (remainder == (0).toLong()) return (totalMinutes / 60).toString() + " " + unit
        val roundedHours = ((((totalMinutes * 10) / 60f).toInt() ) / 10f)
        return "$roundedHours $unit"
    }
}