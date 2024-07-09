package com.example.domain.models

data class Client(val name: String, val phoneNumber: String,val currentVisit: Visit, val historyVisits: List<Visit>)