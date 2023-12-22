package com.swpp10.calendy

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.swpp10.calendy.view.editplanview.EditPlanViewModel
import com.swpp10.calendy.view.messagepage.MessagePageViewModel
import com.swpp10.calendy.view.messageview.CustomWorkerFactory
import com.swpp10.calendy.view.messageview.MessagePlanLogViewModel
import com.swpp10.calendy.view.monthlyview.MonthlyViewModel
import com.swpp10.calendy.view.voiceAssistance.VoiceAssistanceViewModel
import com.swpp10.calendy.view.todolistview.TodoListViewModel
import com.swpp10.calendy.view.weeklyview.WeeklyViewModel

object AppViewModelProvider {
    private var containerInstance: com.swpp10.calendy.IAppContainer? = null

    private fun CreationExtras.calendyApplication(): com.swpp10.calendy.CalendyApplication =
        (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as com.swpp10.calendy.CalendyApplication)

    private fun CreationExtras.getContainer(): com.swpp10.calendy.IAppContainer {
        return com.swpp10.calendy.AppViewModelProvider.containerInstance
            ?: calendyApplication().container.also { com.swpp10.calendy.AppViewModelProvider.containerInstance = it }
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
        initializer {
            getContainer().run {
                VoiceAssistanceViewModel(
                     messageRepository = messageRepository,
                     workManager = WorkManager.getInstance(calendyApplication())
                )
            }
        }
    }
}
