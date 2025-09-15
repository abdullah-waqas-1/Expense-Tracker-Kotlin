package com.example.expensetracker.data.network


import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("latest")
    suspend fun getExchangeRates(
        @Query("base") base: String = "USD"
    ): CurrencyResponse
}

data class CurrencyResponse(
    val base: String,
    val rates: Map<String, Double>
)