package com.example.calendy

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.calendy.view.editplanview.EditPlanViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            EditPlanViewModel(calendyApplication().container.scheduleRepository,calendyApplication().container.todoRepository)
        }
    }

}
fun CreationExtras.calendyApplication() : CalendyApplication =
        (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CalendyApplication)