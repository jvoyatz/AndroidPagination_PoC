package gr.jvoyatz.android.poc.pagination

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import gr.jvoyatz.android.poc.pagination.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    lateinit var binding: ActivityMainBinding

    private var currentPosition = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val retrofitService = MoviesApiService.getInstance()
        val mainRepository = MoviesRepository(retrofitService)
        val moviesAdapter = MoviesAdapter {
            Timber.d("load more pls")
            viewModel.getMovies(++currentPosition)

        }
//        binding.recyclerview.adapter = moviesAdapter
//        moviesAdapter.showLoading()
        val moviesPaginationAdapter = MoviesPaginationAdapter()
        binding.recyclerview.adapter = moviesPaginationAdapter
        moviesPaginationAdapter.addLoadStateListener { loadState ->
            // show empty list
            if (loadState.refresh is LoadState.Loading ||
                loadState.append is LoadState.Loading)
                binding.progressDialog.isVisible = true
            else {
                binding.progressDialog.isVisible = false
                // If we have an error, show a toast
                val errorState = when {
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.prepend is LoadState.Error ->  loadState.prepend as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                errorState?.let {
                    Toast.makeText(this, it.error.toString(), Toast.LENGTH_LONG).show()
                }

            }
        }

        viewModel = MainViewModel.create(mainRepository, MainViewModel::class.java)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
//                viewModel.moviesFlow.collect{
//                    Timber.i("adapterlist.size =  ${moviesAdapter.currentList.size}")
//                    val updatedList = moviesAdapter.currentList.filter { movie ->
//                        movie.id != MoviesAdapter.TYPE_PROGRESS
//                    }
//                    //Timber.d("removed!!!!!!!!!!!!!!!!!!!! ----> $updatedList")
//                    moviesAdapter.submitList(updatedList + it)
//                    binding.progressDialog.visibility = View.GONE
//                }

                viewModel.getMovies().collect{
                    moviesPaginationAdapter.submitData(it)
                }
            }
        }

        viewModel.getMovies()
    }
}