package ru.vb.practice.di

import com.example.domain.repository.FakeRepository
import com.example.domain.useCase.GetClientUseCase
import com.example.domain.useCase.GetRecordsUseCase
import com.example.domain.useCase.GetResultDayUseCase
import org.koin.dsl.module

val domainModule = module {
    factory <GetRecordsUseCase>{
        GetRecordsUseCase(FakeRepository = get())
    }

    factory <GetClientUseCase>{
        GetClientUseCase(FakeRepository = get())
    }

    factory <GetResultDayUseCase>{
        GetResultDayUseCase(FakeRepository = get())
    }
}