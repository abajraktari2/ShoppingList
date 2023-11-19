package hu.bme.aut.D4104N.shoppinglist.viewitems

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    val apiService: CurrencyApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.frankfurter.app")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApiService::class.java)
    }
}