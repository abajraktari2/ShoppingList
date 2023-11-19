package hu.bme.aut.D4104N.shoppinglist.viewitems


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

data class CurrencyResponse(
    val rates: Map<String, Double>
)

interface CurrencyApiService {
    @GET("/latest")
    suspend fun getRates(
        @Query("from") fromCurrency: String,
        @Query("to") toCurrencies: String
    ): Response<CurrencyResponse>
}
