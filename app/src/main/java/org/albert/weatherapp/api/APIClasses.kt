package org.albert.weatherapp.api

import org.albert.weatherapp.model.Forecast
import org.albert.weatherapp.model.Weather

data class APICondition(
    var text: String? = null,
    var icon: String? = null
)

data class APIWeather(
    var last_updated: String? = null,
    var temp_c: Double? = 0.0,
    var maxtemp_c: Double? = 0.0,
    var mintemp_c: Double? = 0.0,
    var condition: APICondition? = null
)

data class APICurrentWeather(
    var location: APILocation? = null,
    var current: APIWeather? = null
)

fun APICurrentWeather.toWeather(): Weather {
    return Weather(
        date = current?.last_updated ?: "...",
        desc = current?.condition?.text ?: "...",
        temp = current?.temp_c ?: -1.0,
        imgUrl = "https:" + current?.condition?.icon
    )
}

data class APIWeatherForecast(
    var location: APILocation? = null,
    var current: APIWeatherForecast? = null,
    var forecast: APIForecast? = null
)

data class APIForecast(var forecastday: List<APIForecastDay>? = null)
data class APIForecastDay(var date: String? = null, var day: APIWeather? = null)

fun APIWeatherForecast.toForecast(): List<Forecast>? {
    return forecast?.forecastday?.map {
        Forecast(
            date = it.date ?: "00-00-0000",
            weather = it.day?.condition?.text ?: "Erro carregando!",
            tempMin = it.day?.mintemp_c ?: -1.0,
            tempMax = it.day?.maxtemp_c ?: -1.0,
            imgUrl = ("https:" + it.day?.condition?.icon)
        )
    }
}