package gr.jvoyatz.android.poc.pagination

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
        binding.recyclerview.adapter = moviesAdapter
        moviesAdapter.showLoading()

        viewModel = MainViewModel.create(mainRepository, MainViewModel::class.java)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.moviesFlow.collect{
                    Timber.i("adapterlist.size =  ${moviesAdapter.currentList.size}")
                    val updatedList = moviesAdapter.currentList.filter { movie ->
                        movie.id != MoviesAdapter.TYPE_PROGRESS
                    }
                    //Timber.d("removed!!!!!!!!!!!!!!!!!!!! ----> $updatedList")
                    moviesAdapter.submitList(updatedList + it)
                    binding.progressDialog.visibility = View.GONE
                }
            }
        }

        viewModel.getMovies()
    }
}