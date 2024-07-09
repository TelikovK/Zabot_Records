package ru.vb.practice.presentation.state

import com.example.domain.models.Client
import com.example.domain.models.Visit

sealed class ModalUiState {
    object Loading : ModalUiState()
    object hideModal : ModalUiState()
    data class HasClient(val client: Client, val historyVisits: List<Visit>) : ModalUiState()
}