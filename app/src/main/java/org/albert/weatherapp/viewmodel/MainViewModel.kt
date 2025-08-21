package org.albert.weatherapp.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.albert.weatherapp.api.WeatherService
import org.albert.weatherapp.api.toForecast
import org.albert.weatherapp.api.toWeather
import org.albert.weatherapp.model.City
import org.albert.weatherapp.model.User
import org.albert.weatherapp.monitor.ForecastMonitor
import org.albert.weatherapp.repo.Repository
import org.albert.weatherapp.ui.nav.Route

class MainViewModelFactory(
    private val repository: Repository,
    private val service: WeatherService,
    private val monitor: ForecastMonitor,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository, service, monitor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainViewModel(
    private val repository: Repository,
    private val service: WeatherService,
    private val monitor: ForecastMonitor,
) : ViewModel() {

    private val _cities = mutableStateMapOf<String, City>()
    val cities: List<City>
        get() = _cities.values.toList()

    private var _city = mutableStateOf<City?>(null)
    var city: City?
        get() = _city.value
        set(tmp) {
            _city.value = tmp?.copy()
        }

    private val _user = mutableStateOf<User?>(null)
    val user: User?
        get() = _user.value

    private var _page = mutableStateOf<Route>(Route.Home)
    var page: Route
        get() = _page.value
        set(tmp) {
            _page.value = tmp
        }

    init {
        viewModelScope.launch(Dispatchers.Main) {
            repository.user.collect { user ->
                _user.value = user.copy()
            }
        }
        viewModelScope.launch(Dispatchers.Main) {
            repository.cities.collect { list ->
                val names = list.map { it.name }
                val newCities = list.filter { it.name !in _cities.keys }
                val oldCities = list.filter { it.name in _cities.keys }
                _cities.keys.removeIf { it !in names } // remove cidades deletadas
                newCities.forEach { _cities[it.name] = it } // adiciona cidades novas
                oldCities.forEach { refresh(it) }
            }
        }
    }

    fun remove(city: City) {
        repository.remove(city)
    }

    fun add(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val location = service.getLocation(name) ?: return@launch
        repository.add(City(name = name, location = location))
    }

    fun add(location: LatLng) = viewModelScope.launch(Dispatchers.IO) {
        val name = service.getName(location.latitude, location.longitude)
        if (name != null) {
            repository.add(City(name = name, location = location))
        }
    }

    fun loadWeather(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val weather = service.getWeather(name)?.toWeather()
        _cities[name]?.let { refresh(it.copy(weather = weather)) }
    }

    fun loadForecast(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val apiForecast = service.getForecast(name)
        val newCity = _cities[name]!!.copy(forecast = apiForecast?.toForecast())
        _cities.remove(name)
        _cities[name] = newCity
        city = if (city?.name == name) newCity else city
        refresh(newCity)
    }

    fun loadBitmap(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val city = _cities[name]
        val bitmap = service.getBitmap(city?.weather!!.imgUrl)
        val newCity = city.copy(
            weather = city.weather?.copy(
                bitmap = bitmap
            )
        )
        _cities.remove(name)
        _cities[name] = newCity
        refresh(newCity)
    }

    fun update(city: City) {
        repository.update(city = city)
    }

    suspend fun refresh(city: City) = withContext(Dispatchers.Main) {
        val oldCity = _cities[city.name]
        _cities.remove(city.name)
        _cities[city.name] = city.copy(
            weather = city.weather ?: oldCity?.weather, // se novo = null, reusa antigo
            forecast = city.forecast ?: oldCity?.forecast, // se novo = null, reusa antigo
        )
        if (_city.value?.name == city.name) {
            _city.value = _cities[city.name]
        }
        monitor.updateCity(_cities[city.name]!!)
    }
}
