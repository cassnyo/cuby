package com.cassnyo.cuby.common.ui

object TimeFormatter {
    fun formatMilliseconds(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val remainingMilliseconds = milliseconds % 1000

        val formattedMinutes = if (minutes > 0) "$minutes:" else ""
        val formattedSeconds = if (minutes > 0) String.format("%02d", seconds) else seconds
        val formattedMilliseconds = String.format("%02d", remainingMilliseconds / 10)

        return "$formattedMinutes$formattedSeconds.$formattedMilliseconds"
    }
}