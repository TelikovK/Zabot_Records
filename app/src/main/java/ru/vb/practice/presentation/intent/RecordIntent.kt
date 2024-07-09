package ru.vb.practice.presentation.intent

import com.example.domain.models.Record

sealed class RecordIntent {
    object LoadRecords : RecordIntent()
    object hideModal : RecordIntent()
    data class onAcceptClick(val recordId: Int) : RecordIntent()
    data class ViewClientInfo(val record: Record) : RecordIntent()
    object ResultDay : RecordIntent()
}