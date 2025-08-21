package org.albert.weatherapp.api

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherService {
    private var weatherAPI: WeatherServiceAPI

    init {
        val retrofitAPI = Retrofit.Builder().baseUrl(WeatherServiceAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        weatherAPI = retrofitAPI.create(WeatherServiceAPI::class.java)
    }

    suspend fun getName(lat: Double, lng: Double): String? = withContext(Dispatchers.IO) {
        search("$lat,$lng")?.name // retorno
    }

//    fun getLocation(name: String, onResponse: (lat: Double?, long: Double?) -> Unit) {
//        search(name) { loc -> onResponse(loc?.lat, loc?.lon) }
//    }

    fun getLocation(name: String): LatLng? {
        val loc = search(name)
        return if (loc != null)
            loc.lon?.let { loc.lat?.let { it1 -> LatLng(it1, it) } }
        else
            null
    }

    private fun search(query: String): APILocation? {
        val call: Call<List<APILocation>?> = weatherAPI.search(query)
        val apiLoc = call.execute().body()
        return if (!apiLoc.isNullOrEmpty()) apiLoc[0] else null
    }

//    private fun search(query: String, onResponse: (APILocation?) -> Unit) {
//        val call: Call<List<APILocation>?> = weatherAPI.search(query)
//        call.enqueue(object : Callback<List<APILocation>?> {
//            override fun onResponse(
//                call: Call<List<APILocation>?>, response: Response<List<APILocation>?>
//            ) {
//                onResponse(response.body()?.let { if (it.isNotEmpty()) it[0] else null })
//            }
//
//            override fun onFailure(call: Call<List<APILocation>?>, t: Throwable) {
//                Log.w("WeatherApp WARNING", "" + t.message)
//                onResponse(null)
//            }
//        })
//    }

    private fun <T> enqueue(call: Call<T?>, onResponse: ((T?) -> Unit)? = null) {
        call.enqueue(object : Callback<T?> {
            override fun onResponse(call: Call<T?>, response: Response<T?>) {
                val obj: T? = response.body()
                onResponse?.invoke(obj)
            }

            override fun onFailure(call: Call<T?>, t: Throwable) {
                Log.w("WeatherApp WARNING", "" + t.message)
            }
        })
    }

    suspend fun getWeather(name: String): APICurrentWeather? = withContext(Dispatchers.IO) {
        val call: Call<APICurrentWeather?> = weatherAPI.weather(name)
        call.execute().body() // retorno
    }

//    fun getForecast(name: String, onResponse: (APIWeatherForecast?) -> Unit) {
//        val call: Call<APIWeatherForecast?> = weatherAPI.forecast(name)
//        enqueue(call) { onResponse.invoke(it) }
//    }

    fun getForecast(name: String): APIWeatherForecast? {
        val call: Call<APIWeatherForecast?> = weatherAPI.forecast(name)
        return call.execute().body()
    }

    suspend fun getBitmap(imgUrl: String) : Bitmap? = withContext(Dispatchers.IO) {
        Picasso.get().load(imgUrl).get() // retorno
    }

    fun getBitmap(imgUrl: String, onResponse: (Bitmap?) -> Unit) {
        Picasso.get().load(imgUrl).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(
                bitmap: Bitmap?, from: Picasso.LoadedFrom?
            ) {
                onResponse.invoke(bitmap)
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Log.w("WeatherApp WARNING", "" + e?.message)
                e?.printStackTrace()
            }
        })
    }
}