package com.example.calendy

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.calendy.view.editplanview.EditPlanViewModel
import com.example.calendy.view.messagepage.MessagePageViewModel
import com.example.calendy.view.messageview.CustomWorkerFactory
import com.example.calendy.view.messageview.MessagePlanLogViewModel
import com.example.calendy.view.monthlyview.MonthlyViewModel
import com.example.calendy.view.todolistview.TodoListViewModel
import com.example.calendy.view.weeklyview.WeeklyViewModel

object AppViewModelProvider {
    private var containerInstance: IAppContainer? = null

    private fun CreationExtras.calendyApplication(): CalendyApplication =
        (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CalendyApplication)

    private fun CreationExtras.getContainer(): IAppContainer {
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
                WeeklyViewModel(
                    scheduleRepository = scheduleRepository, todoRepository = todoRepository
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
                    workManager = WorkManager.getInstance(calendyApplication())
                )
            }
        }
        initializer {
            getContainer().run {
                EditPlanViewModel(
                    planRepository = planRepository,
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
