package ru.vb.practice.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.vb.practice.presentation.MainViewModel

val appModule = module {

    viewModel<MainViewModel>{
        MainViewModel(
            getRecordsUseCase = get(),
            getClientUseCase = get(),
            getResultDayUseCase = get()
        )
    }
}