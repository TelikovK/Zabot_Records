package com.example.domain.repository

import com.example.domain.models.Client
import com.example.domain.models.Record
import com.example.domain.models.ResultDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface FakeRepository {

    fun getRecords(): Flow<List<Record>>

    fun getClient(clientId: Int): Flow<Client>

    fun getResultDay(): Flow<ResultDay>
}
