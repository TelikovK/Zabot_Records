package ru.vb.practice.presentation.state

import com.example.domain.models.Record
import com.example.domain.models.ResultDay

sealed class ResultDayUiState {
    object NoResult : ResultDayUiState()
    data class HasResult(val reuslt: ResultDay) : ResultDayUiState()
}