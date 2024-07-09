package com.example.domain.useCase

import com.example.domain.models.Record
import com.example.domain.models.ResultDay
import com.example.domain.repository.FakeRepository
import kotlinx.coroutines.flow.Flow

class GetResultDayUseCase(private val FakeRepository: FakeRepository) {

    fun execute(): Flow<ResultDay> {
        return FakeRepository.getResultDay()
    }
}