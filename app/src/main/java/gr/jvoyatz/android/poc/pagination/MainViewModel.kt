package gr.jvoyatz.android.poc.pagination

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel constructor(private val mainRepository: MoviesRepository) : ViewModel() {

    private val _moviesFlow = MutableStateFlow<List<Movie>>(listOf())
    val moviesFlow = _moviesFlow.asStateFlow()

    fun getMovies(position: Int ? = null){
        viewModelScope.launch {
            delay(1000)
            val movies = mainRepository.getMovies(position ?: 1)
            _moviesFlow.emit(movies)
        }
    }

    companion object Factory {
        fun <T : ViewModel> create(repository: MoviesRepository, modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                MainViewModel(repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}