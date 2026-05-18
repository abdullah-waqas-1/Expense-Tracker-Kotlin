package com.example.expensetracker.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.expensetracker.data.model.Expense
import java.io.File
import java.util.Date

object CsvExportHelper {
    fun exportExpensesToCsv(context: Context, expenses: List<Expense>) {

        val csvHeader = "Title,Amount,Category,Date,Type,Note\n"
        val csvContent = StringBuilder(csvHeader)

        var totalIncome = 0.0
        var totalExpenses = 0.0

        val sortedExpenses = expenses.sortedWith(
            compareBy<Expense> { it.date }.thenBy { it.createdAt }
        )

        sortedExpenses.forEach { expense ->
            if (expense.isIncome) {
                totalIncome += expense.amount
            } else {
                totalExpenses += expense.amount
            }

            val line = "${expense.title.replace(",", " ")}," +
                    "${expense.amount}," +
                    "${expense.category}," +
                    "${AppDateUtils.formatDate(expense.date, "yyyy-MM-dd")}," +
                    "${if (expense.isIncome) "Income" else "Expense"}," +
                    "${expense.description?.replace(",", " ") ?: ""}\n"
            csvContent.append(line)
        }

        csvContent.append("\n")

        val netBalance = totalIncome - totalExpenses
        csvContent.append("Total Income,$totalIncome,,,,\n")
        csvContent.append("Total Expenses,$totalExpenses,,,,\n")
        csvContent.append("Net Balance,$netBalance,,,,\n")

        try {
            val fileSuffix = AppDateUtils.formatDate(Date(), "MMMyy").lowercase()
            val fileName = "expenses_$fileSuffix.csv"

            val file = File(context.cacheDir, fileName)
            file.writeText(csvContent.toString())

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, "Expenses Export ($fileSuffix)")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Export Data"))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}