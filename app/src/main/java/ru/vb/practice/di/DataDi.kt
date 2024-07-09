package ru.vb.practice.di

import com.example.data.FakeRepositoryImpl
import com.example.domain.repository.FakeRepository
import org.koin.dsl.module

val dataModule = module {

    single<FakeRepository> { FakeRepositoryImpl() }
}