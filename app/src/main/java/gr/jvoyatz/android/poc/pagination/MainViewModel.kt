package gr.jvoyatz.android.poc.pagination

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel constructor(private val moviesRepository: MoviesRepository) : ViewModel() {

    private val _moviesFlow = MutableStateFlow<List<Movie>>(listOf())
    val moviesFlow = _moviesFlow.asStateFlow()

    fun getMovies(position: Int ? = null){
        viewModelScope.launch {
            delay(1000)
            val movies = moviesRepository.getMovies(position ?: 1)
            _moviesFlow.emit(movies)
        }
    }

    fun getMovies(): Flow<PagingData<Movie>> {
        return moviesRepository.getMoviesPagination()
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