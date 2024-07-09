package com.example.domain.useCase

import com.example.domain.models.Record
import com.example.domain.repository.FakeRepository
import kotlinx.coroutines.flow.Flow

class GetRecordsUseCase(private val FakeRepository: FakeRepository) {

    fun execute(): Flow<List<Record>> {
        return FakeRepository.getRecords()
    }
}
