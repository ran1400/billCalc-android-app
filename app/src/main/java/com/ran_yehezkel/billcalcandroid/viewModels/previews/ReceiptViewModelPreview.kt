package com.ran_yehezkel.billcalcandroid.viewModels.previews

import com.ran_yehezkel.billcalcandroid.model.ReceiptRepository
import com.ran_yehezkel.billcalcandroid.viewModels.ReceiptViewModel

class ReceiptViewModelPreview(repository: ReceiptRepository) : ReceiptViewModel(repository)
{
    override fun initPage(): Boolean
    {
        _receiptItems.value = Dummy.getDummyReceiptItems()
        _receiptImage.value = Dummy.getDummyImage()
        _shareUrl.value = "https://ran-y.com"

        return true
    }
}
