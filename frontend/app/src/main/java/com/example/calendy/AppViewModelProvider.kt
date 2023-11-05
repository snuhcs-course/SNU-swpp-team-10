package com.example.calendy

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.calendy.data.CalendyDatabase
import com.example.calendy.view.messagepage.MessagePageViewModel
import com.example.calendy.view.messagepage.SqlExecutionViewModel
import com.example.calendy.data.emptydb.EmptyDatabase
import com.example.calendy.view.editplanview.EditPlanViewModel
import com.example.calendy.view.monthlyview.MonthlyViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            EditPlanViewModel(
                calendyApplication().container.scheduleRepository,
                calendyApplication().container.todoRepository,
                calendyApplication().container.categoryRepository,
                calendyApplication().container.repeatGroupRepository
            )
        }
        initializer {
            MonthlyViewModel(
                calendyApplication().container.planRepository,
            )
        }
        initializer {
            SqlExecutionViewModel(
                calendyDatabase = CalendyDatabase.getDatabase(calendyApplication().applicationContext),
                emptyDatabase = EmptyDatabase.getDatabase(calendyApplication().applicationContext)
            )
        }
        initializer {
            MessagePageViewModel(
                calendyApplication().container.messageRepository
            )
        }
    }

}

fun CreationExtras.calendyApplication(): CalendyApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CalendyApplication)