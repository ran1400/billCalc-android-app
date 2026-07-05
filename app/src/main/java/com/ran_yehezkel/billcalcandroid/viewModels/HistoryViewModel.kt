package com.ran_yehezkel.billcalcandroid.viewModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ran_yehezkel.billcalcandroid.model.DateHelper
import com.ran_yehezkel.billcalcandroid.model.ReceiptDetailsUi
import com.ran_yehezkel.billcalcandroid.model.ReceiptRepository
import com.ran_yehezkel.billcalcandroid.model.SplitReceiptDetails
import com.ran_yehezkel.billcalcandroid.model.TimePeriod
import com.ran_yehezkel.billcalcandroid.ui.moveToPage.MoveToImmutableReceipt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
open class HistoryViewModel(private val repository: ReceiptRepository) : ViewModel()
{

    sealed class UiEvent
    {
        object MoveToImmutableReceiptScreen : UiEvent()
    }
    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()


    protected val _selectedFilter = MutableStateFlow<TimePeriod>(TimePeriod.ALL)
    val selectedFilter = _selectedFilter.asStateFlow()

    private val _showCustomDatesPopup = MutableStateFlow<Boolean>(false)
    val showCustomDatesPopup = _showCustomDatesPopup.asStateFlow()

    private val _customDatesString = MutableStateFlow<String>(TimePeriod.ALL.title)
    val customDatesString = _customDatesString.asStateFlow()

    private val _customDateRange = MutableStateFlow(Pair(DateHelper.now(),DateHelper.now()))

    open val splitReceipts: StateFlow<List<SplitReceiptDetails>> = combine(_selectedFilter, _customDateRange)
        { filter, range -> filter to range }
        .flatMapLatest { (filter, range) ->
            if (filter == TimePeriod.CUSTOM)
                repository.getReceiptDetailsBetween(range.first, range.second)
            else
                repository.getReceiptDetailsForPeriod(filter)
        }
        .map { list ->
            Utils.splitReceiptsByMonth(list)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    fun setFilter(period: TimePeriod)
    {
        if (period != TimePeriod.CUSTOM)
        {
            _selectedFilter.value = period
            _customDateRange.value = Utils.getDateRangeForPeriod(period)
            _customDatesString.value = getCustomDateRangeString()
        }
        else
            showCustomDatesPopup(true)
    }

    fun showCustomDatesPopup(bool : Boolean)
    {
        _showCustomDatesPopup.value = bool
    }

    fun onCustomDatesSelected(startDate: Long, endDate: Long)
    {
        val startCal = Calendar.getInstance().apply { timeInMillis = startDate }
        val startInt = DateHelper.toInt(
            day = startCal.get(Calendar.DAY_OF_MONTH),
            month = startCal.get(Calendar.MONTH) + 1,
            year = startCal.get(Calendar.YEAR)
        )

        val endCal = Calendar.getInstance().apply { timeInMillis = endDate }
        val endInt = DateHelper.toInt(
            day = endCal.get(Calendar.DAY_OF_MONTH),
            month = endCal.get(Calendar.MONTH) + 1,
            year = endCal.get(Calendar.YEAR)
        )

        _customDateRange.value = startInt to endInt
        _selectedFilter.value = TimePeriod.CUSTOM
        _customDatesString.value = getCustomDateRangeString()
        showCustomDatesPopup(false)
    }

    open fun onReceiptClicked(id: Int)
    {
        viewModelScope.launch{
            val receipt = repository.getReceipt(id)
            if (receipt == null)
                return@launch
            MoveToImmutableReceipt.create(receipt.totalPrice,receipt.tip,receipt.items)
            _uiEvents.emit(UiEvent.MoveToImmutableReceiptScreen)
        }
    }

    fun getCustomDateRangeString() : String
    {
        if (selectedFilter.value == TimePeriod.ALL)
            return TimePeriod.ALL.title
        else
        {
            val start = _customDateRange.value.first
            val end = _customDateRange.value.second
            val startDate = ReceiptDetailsUi.formatDate(start)
            val endDate = ReceiptDetailsUi.formatDate(end)
            return "$endDate - $startDate"
        }
    }

}
