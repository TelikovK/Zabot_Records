package ru.vb.practice.presentation.state

import com.example.domain.models.Record

sealed class RecordUiState {
    object Loading : RecordUiState()
    object NoRecords : RecordUiState()
    data class HasRecords(val records: List<Record>) : RecordUiState()
}