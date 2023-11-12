package com.example.calendy

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.calendy.view.editplanview.EditPlanViewModel
import com.example.calendy.view.messagepage.MessagePageViewModel
import com.example.calendy.view.messagepage.MessagePlanLogViewModel
import com.example.calendy.view.monthlyview.MonthlyViewModel
import com.example.calendy.view.todolistview.TodoListViewModel

object AppViewModelProvider {
    private var containerInstance: IAppContainer? = null

    private fun CreationExtras.getContainer(): IAppContainer {
        fun CreationExtras.calendyApplication(): CalendyApplication =
            (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CalendyApplication)

        return containerInstance ?: calendyApplication().container.also { containerInstance = it }
    }

    val Factory = viewModelFactory {
        initializer {
            getContainer().run {
                MonthlyViewModel(
                    planRepository = planRepository,
                )
            }
        }
        initializer {
            getContainer().run {
                TodoListViewModel(
                    todoRepository = todoRepository
                )
            }
        }
        initializer {
            getContainer().run {
                MessagePageViewModel(
                    planRepository = planRepository,
                    messageRepository = messageRepository,
                    categoryRepository = categoryRepository,
                    calendyServerApi = calendyServerApi,
                    rawSqlDatabase = rawSqlDatabase,
                    historyRepository = historyRepository,
                )
            }
        }
        initializer {
            getContainer().run {
                EditPlanViewModel(
                    scheduleRepository = scheduleRepository,
                    todoRepository = todoRepository,
                    categoryRepository = categoryRepository,
                    repeatGroupRepository = repeatGroupRepository
                )
            }
        }
        initializer {
            getContainer().run {
                MessagePlanLogViewModel(
                    messageRepository = messageRepository,
                    planRepository = planRepository,
                    historyRepository = historyRepository,
                )
            }
        }
    }
}
