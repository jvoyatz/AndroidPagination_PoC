package gr.jvoyatz.android.poc.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import timber.log.Timber

const val API_KEY2 = "e8d648003bd11b5c498674fbd4905525"
class MoviePagingSource(val apiService: MoviesApiService): PagingSource<Int, Movie>() {
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> = try{
        val page = params.key ?: 1
        delay(500)
        val response = apiService.getTopRatedMovies(API_KEY2, page = page)
        val body = response.body()!!
        LoadResult.Page(data = body.results, if(page == 1) null else page -1, page + 1)
    }catch (e: Exception){
        LoadResult.Error(e)
    }

}