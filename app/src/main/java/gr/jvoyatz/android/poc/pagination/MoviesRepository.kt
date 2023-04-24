package gr.jvoyatz.android.poc.pagination

private const val API_KEY = "e8d648003bd11b5c498674fbd4905525"
class MoviesRepository(
    private val apiService: MoviesApiService
) {


    suspend fun getMovies(position: Int): List<Movie> {
        return apiService.getTopRatedMovies(API_KEY, "en-US", position).let {
            val body = it.body()
            body?.results!!
        }
    }
}