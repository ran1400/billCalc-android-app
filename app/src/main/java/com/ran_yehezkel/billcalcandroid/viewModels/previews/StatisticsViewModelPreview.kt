package com.ran_yehezkel.billcalcandroid.viewModels.previews

import android.util.Log
import com.ran_yehezkel.billcalcandroid.model.ReceiptRepository
import com.ran_yehezkel.billcalcandroid.model.ReceiptsStatistics
import com.ran_yehezkel.billcalcandroid.viewModels.StatisticsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StatisticsViewModelPreview(repository: ReceiptRepository) : StatisticsViewModel(repository)
{

    override val receiptsStatistics: StateFlow<ReceiptsStatistics?>
                = MutableStateFlow(Dummy.getDummyReceiptsStatistics())


    override fun onReceiptClicked(id: Int)
    {
        Log.d("HistoryViewModelPreview", "receiptClicked: [receipt index is $id]")
    }

}
