package com.example.data

import com.example.domain.models.Client
import com.example.domain.models.Visit
import com.example.domain.repository.FakeRepository
import com.example.domain.models.Record
import com.example.domain.models.ResultDay
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

class FakeRepositoryImpl : FakeRepository {

    private val clients = listOf(
        Client(
            "Егор", "+7 (961) 499-00-00", Visit(LocalDateTime.of(2024, 6, 15, 10, 0),
                "Александр",
                "Test",
                "Test",
                "Test"), emptyList()),
        Client(
            "Антон", "+7 (909) 752-31-21",Visit(LocalDateTime.of(2024, 6, 15, 10, 0),
                "Александр",
                "Test",
                "Test",
                "Test"), listOf(
                Visit(
                    LocalDateTime.of(2023, 5, 10, 12, 0),
                    "Яна",
                    "Test",
                    "Test",
                    "Test"
                )
            )
        )
        )



    override fun getRecords(): Flow<List<Record>> = flow {
        delay(5000) // Для теста
        emit(
            listOf(
                Record(
                    id = 0,
                    clientName = clients[0].name,
                    service = "Test",
                    comment = "Test",
                    lastVisit = clients[0].historyVisits.lastOrNull()?.date ?: LocalDateTime.now(),
                    currentVist = LocalDateTime.of(2024, 7, 3, 10, 0),
                    time = "10:00",
                    isAccepted = true,
                    clientId = 0
                ),
                Record(
                    id = 2,
                    clientName = clients[1].name,
                    service = "Test",
                    comment = "Test",
                    lastVisit = LocalDateTime.of(2024, 7, 3, 10, 0),
                    currentVist = LocalDateTime.of(2024, 7, 20, 10, 0),
                    time = "11:00",
                    isAccepted = false,
                    clientId = 1
                )

            )
        )
    }

    override fun getClient(clientId: Int): Flow<Client> = flow {
        delay(5000)
        emit(clients.get(clientId))
    }

    override fun getResultDay(): Flow<ResultDay> = flow {
      emit(ResultDay("24 500 р", "12 000 р", "7 500 р", "4 500 р", "5", "4 800 р", "40%, что составило 8 250 р" ))
    }
}
