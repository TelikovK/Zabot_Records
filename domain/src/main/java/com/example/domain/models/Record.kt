package com.example.domain.models

import java.time.LocalDateTime

data class Record(val id: Int, val clientName: String, val service: String, val comment: String, val lastVisit: LocalDateTime, val currentVist: LocalDateTime, val time: String, var isAccepted: Boolean, var clientId: Int)

