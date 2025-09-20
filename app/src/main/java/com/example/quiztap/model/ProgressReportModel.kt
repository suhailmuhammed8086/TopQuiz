package com.example.quiztap.model

import com.example.quiztap.data.Grade

data class ProgressReportModel(
    val player: PlayerModel,
    var score: Int,
    val percentage: Int,
    val grade: Grade,
    val position: Int,
) {
}