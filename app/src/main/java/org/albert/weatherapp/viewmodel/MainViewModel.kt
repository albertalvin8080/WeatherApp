package org.albert.weatherapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import org.albert.weatherapp.api.WeatherService
import org.albert.weatherapp.api.toWeather
import org.albert.weatherapp.db.fb.FBCity
import org.albert.weatherapp.db.fb.FBDatabase
import org.albert.weatherapp.db.fb.FBUser
import org.albert.weatherapp.db.fb.toFBCity
import org.albert.weatherapp.model.City
import org.albert.weatherapp.model.User

class MainViewModelFactory(private val db: FBDatabase, private val service: WeatherService) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(db, service) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainViewModel(
    private val db: FBDatabase,
    private val service: WeatherService
) : ViewModel(),
    FBDatabase.Listener {
    private val _cities = mutableStateMapOf<String, City>()
    val cities: List<City>
        get() = _cities.values.toList()
    private val _user = mutableStateOf<User?>(null)
    val user: User?
        get() = _user.value

    init {
        db.setListener(this)
    }

    fun remove(city: City) {
        db.remove(city.toFBCity())
    }

    fun add(name: String) {
        service.getLocation(name) { lat, lng ->
            if (lat != null && lng != null) {
                db.add(City(name = name, location = LatLng(lat, lng)).toFBCity())
            }
        }
    }

    fun add(location: LatLng) {
        service.getName(location.latitude, location.longitude) { name ->
            if (name != null) {
                db.add(City(name = name, location = location).toFBCity())
            }
        }
    }

    override fun onUserLoaded(user: FBUser) {
        _user.value = user.toUser()
    }

    override fun onUserSignOut() {
        TODO("Not yet implemented")
    }

    override fun onCityAdded(city: FBCity) {
        _cities[city.name!!] = city.toCity()
    }

    override fun onCityUpdated(city: FBCity) {
        _cities.remove(city.name)
        _cities[city.name!!] = city.toCity()
    }

    override fun onCityRemoved(city: FBCity) {
        _cities.remove(city.name)
    }

    fun loadWeather(name: String) {
        service.getWeather(name) { apiWeather ->
            val newCity = _cities[name]!!.copy(weather = apiWeather?.toWeather())
            _cities.remove(name)
            _cities[name] = newCity
        }
    }
}
