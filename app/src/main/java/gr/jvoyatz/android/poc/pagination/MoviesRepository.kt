package gr.jvoyatz.android.poc.pagination

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

private const val API_KEY = "e8d648003bd11b5c498674fbd4905525"
class MoviesRepository(
    private val apiService: MoviesApiService
) {


    private val list = mutableListOf<Movie>()
    suspend fun getMovies(position: Int): List<Movie> {
        return apiService.getTopRatedMovies(API_KEY, "en-US", position).let {
            val body = it.body()
            body?.results!!.also {
                Timber.d("size ${it.size}")
                list.addAll(it)
                Timber.i("list.size = ${list.size} + listToSet.size = ${list.toSet().size}")
            }
        }
    }


    fun getMoviesPagination(): Flow<PagingData<Movie>>{
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = 2
            ),
            pagingSourceFactory = {
                MoviePagingSource(apiService)
            }
            , initialKey = 1
        ).flow
    }
}