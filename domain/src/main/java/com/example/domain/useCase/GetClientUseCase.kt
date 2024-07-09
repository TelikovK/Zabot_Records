package com.example.domain.useCase

import com.example.domain.models.Client
import com.example.domain.repository.FakeRepository
import kotlinx.coroutines.flow.Flow

class GetClientUseCase(private val FakeRepository: FakeRepository) {

    fun execute(clientId: Int): Flow<Client> {
        return FakeRepository.getClient(clientId)
    }
}