package com.example.calendy.view.editplanview

import com.example.calendy.data.dummy.DummyCategoryRepository
import com.example.calendy.data.dummy.DummyPlanRepository
import com.example.calendy.data.dummy.DummyRepeatGroupRepository
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