package org.albert.weatherapp.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

import org.albert.weatherapp.BuildConfig

interface WeatherServiceAPI {
    companion object {
        const val BASE_URL = "https://api.weatherapi.com/v1/"
        const val API_KEY = BuildConfig.WEATHER_API_KEY
    }
    @GET("search.json?key=$API_KEY&lang=pt_br")
    fun search(@Query("q") query: String): Call<List<APILocation>?>
}
