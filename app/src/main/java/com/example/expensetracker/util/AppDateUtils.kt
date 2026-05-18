package com.example.expensetracker.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object AppDateUtils {

    private fun getFormatter(pattern: String): SimpleDateFormat {
        return SimpleDateFormat(pattern, Locale.getDefault())
    }

    fun formatDate(date: Date, pattern: String): String {
        return getFormatter(pattern).format(date)
    }

    fun getStartOfDay(date: Date): Date {
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun generateCalendarMonthGrid(year: Int, month: Int): List<Date> {
        val cal = Calendar.getInstance().apply {
            set(year, month, 1)
            val firstDayOfWeek = get(Calendar.DAY_OF_WEEK)
            add(Calendar.DAY_OF_MONTH, -(firstDayOfWeek - 1))
        }
        val dateList = mutableListOf<Date>()
        repeat(42) {
            dateList.add(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dateList
    }
}