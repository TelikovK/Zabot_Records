package com.example.domain.models

import java.time.LocalDateTime

    class Visit(val date: LocalDateTime, val master: String, val service: String, val payment: String, val comment: String)