package com.swpp10.calendy.view.editplanview

import com.swpp10.calendy.data.dummy.DummyCategoryRepository
import com.swpp10.calendy.data.dummy.DummyPlanRepository
import com.swpp10.calendy.data.dummy.DummyRepeatGroupRepository
import org.junit.Test

class EditPlanViewModelTest {
    val editPlanViewModel = EditPlanViewModel(
        DummyPlanRepository(),
        DummyCategoryRepository(),
        DummyRepeatGroupRepository()
    )

    @Test
    fun addPlan() {
        // Should Set RepeatGroup
        editPlanViewModel.addPlan()
    }
}