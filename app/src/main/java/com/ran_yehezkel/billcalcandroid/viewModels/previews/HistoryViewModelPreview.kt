package com.ran_yehezkel.billcalcandroid.viewModels.previews

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ran_yehezkel.billcalcandroid.model.ReceiptDetails
import com.ran_yehezkel.billcalcandroid.model.ReceiptRepository
import com.ran_yehezkel.billcalcandroid.model.SplitReceiptDetails
import com.ran_yehezkel.billcalcandroid.model.TimePeriod
import com.ran_yehezkel.billcalcandroid.viewModels.HistoryViewModel
import com.ran_yehezkel.billcalcandroid.viewModels.Utils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

class HistoryViewModelPreview(repository: ReceiptRepository) : HistoryViewModel(repository)
{
    
    val dummyReceiptDetails = Dummy.getDummyReceiptsDetails()
    
    override val splitReceipts: StateFlow<List<SplitReceiptDetails>> = _selectedFilter
        .map { period ->
            val filtered = filterDummyReceipts(period)
            Utils.splitReceiptsByMonth(filtered)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    private fun filterDummyReceipts(period: TimePeriod): List<ReceiptDetails>
    {
        if (period == TimePeriod.ALL) return dummyReceiptDetails
        if (period == TimePeriod.CUSTOM) return emptyList()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        when (period) {
            TimePeriod.LAST_WEEK -> calendar.add(Calendar.DAY_OF_YEAR, -7)
            TimePeriod.LAST_MONTH -> calendar.add(Calendar.MONTH, -1)
            TimePeriod.START_OF_WEEK -> calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            TimePeriod.START_OF_MONTH -> calendar.set(Calendar.DAY_OF_MONTH, 1)
            TimePeriod.LAST_6_MONTHS -> calendar.add(Calendar.MONTH, -6)
            TimePeriod.LAST_12_MONTHS -> calendar.add(Calendar.MONTH, -12)
            else -> {}
        }
        val startTime = calendar.timeInMillis
        return dummyReceiptDetails.filter { it.time >= startTime }
    }

    override fun onReceiptClicked(id: Int)
    {
        Log.d("HistoryViewModelPreview", "receiptClicked: [receipt index is $id]")
    }
}
