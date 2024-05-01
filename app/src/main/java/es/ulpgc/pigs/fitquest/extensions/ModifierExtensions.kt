package es.ulpgc.pigs.fitquest.extensions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import es.ulpgc.pigs.fitquest.ui.theme.FitQuestBackground
import es.ulpgc.pigs.fitquest.ui.theme.FitquestGradientBottom
import es.ulpgc.pigs.fitquest.ui.theme.FitquestGradientTop

fun Modifier.fitquestLoginBackground(): Modifier = this
        .fillMaxSize()
        .background(
                brush = Brush.verticalGradient(
                        colors = listOf(
                                FitquestGradientTop,
                                FitquestGradientBottom
                        )
                )
        )

fun Modifier.fitquestBackground(): Modifier = this
        .fillMaxSize()
        .background(
                brush = Brush.verticalGradient(
                        colors = listOf(
                                FitQuestBackground,
                                FitQuestBackground
                        )
                )
        )