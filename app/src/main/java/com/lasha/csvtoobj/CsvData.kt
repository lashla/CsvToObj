package com.lasha.csvtoobj


data class CsvData(
    val billFromDb: String?,
    val billFromAiS: String?,
    val mobileNumber: String?,
    val homeNumber: String?,
    val lastVisit: String?,
    val improvementFromDb: String?,
    val fullNameFromDb: String?
)
