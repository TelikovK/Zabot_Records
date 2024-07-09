package ru.vb.practice.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Client
import com.example.domain.models.Record
import com.example.domain.models.Visit
import com.example.domain.useCase.GetClientUseCase
import com.example.domain.useCase.GetRecordsUseCase
import com.example.domain.useCase.GetResultDayUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.vb.practice.presentation.intent.RecordIntent
import ru.vb.practice.presentation.state.ModalUiState
import ru.vb.practice.presentation.state.RecordUiState
import ru.vb.practice.presentation.state.ResultDayUiState
import java.time.LocalDateTime
import java.time.LocalTime;

class MainViewModel(private val getRecordsUseCase: GetRecordsUseCase, private val getClientUseCase: GetClientUseCase, private val getResultDayUseCase: GetResultDayUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow<RecordUiState>(RecordUiState.Loading)
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    private val _modalUiState = MutableStateFlow<ModalUiState>(ModalUiState.hideModal)
    val modalUiState: StateFlow<ModalUiState> = _modalUiState.asStateFlow()

    private val _showModal = MutableStateFlow(false)
    val showModal: StateFlow<Boolean> = _showModal.asStateFlow()

    private val _resultDay = MutableStateFlow<ResultDayUiState>(ResultDayUiState.NoResult)
    val resultDay: StateFlow<ResultDayUiState> = _resultDay.asStateFlow()

    init {
        processIntent(RecordIntent.LoadRecords)
        processIntent(RecordIntent.ResultDay)
    }

    fun processIntent(intent: RecordIntent) {
        when (intent) {
            is RecordIntent.LoadRecords -> fetchRecords()
            is RecordIntent.ViewClientInfo -> fetchClient(intent.record)
            is RecordIntent.hideModal -> hideModal()
            is RecordIntent.onAcceptClick -> onAcceptClick(intent.recordId)
            is RecordIntent.ResultDay -> resultDay()
        }
    }

    private fun resultDay() {
        if (LocalDateTime.now().getHour() >= 20 && LocalDateTime.now().getHour() < 24) {
            viewModelScope.launch {
                getResultDayUseCase.execute().collect { result ->
                    _resultDay.value = ResultDayUiState.HasResult(result)
                }
            }
        }
    }

    private fun fetchRecords() {
        viewModelScope.launch {
            getRecordsUseCase.execute().collect { records ->
                _uiState.value = if (records.isEmpty()) {
                    RecordUiState.NoRecords
                } else {
                    RecordUiState.HasRecords(records)
                }
            }
        }
    }

    private fun fetchClient(record: Record) {
        _showModal.value = true
        _modalUiState.value = ModalUiState.Loading
        val clientId = record.clientId
        viewModelScope.launch {
            getClientUseCase.execute(clientId).collect { client ->
                val sortedVisits = client.historyVisits.sortedByDescending { it.date }
                _modalUiState.value = ModalUiState.HasClient(client, sortedVisits.take(3))
            }
        }
    }

    private fun onAcceptClick(recordId: Int) {
        val currentState = _uiState.value
        if (currentState is RecordUiState.HasRecords) {
            val updatedRecords = currentState.records.map { record ->
                if (record.id == recordId) record.copy(isAccepted = true) else record
            }
            _uiState.value = RecordUiState.HasRecords(updatedRecords)
        }
    }

    private fun hideModal(){
        _showModal.value = false
        _modalUiState.value = ModalUiState.hideModal
    }
}
