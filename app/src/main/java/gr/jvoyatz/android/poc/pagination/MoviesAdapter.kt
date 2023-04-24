package gr.jvoyatz.android.poc.pagination

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import gr.jvoyatz.android.poc.pagination.databinding.ItemMovieBinding
import gr.jvoyatz.android.poc.pagination.databinding.ProgressbarBinding
import timber.log.Timber


class MoviesAdapter(val loadMore: () -> Unit): ListAdapter<Movie, RecyclerView.ViewHolder>(MovieComparator) {


    companion object {
        val TYPE_DATA = 0
        val TYPE_PROGRESS = 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is MovieViewHolder) {
            val movie = getItem(position)!!
            holder.view.name.text = movie.original_title
            Glide.with(holder.itemView.context)
                .load("https://image.tmdb.org/t/p/w300" + movie.poster_path)
                .into(holder.view.imageview)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if(viewType == TYPE_DATA) {
            val binding = ItemMovieBinding.inflate(inflater, parent, false)
            return MovieViewHolder(binding)
        }
        val loadingBinding = ProgressbarBinding.inflate(inflater, parent, false)
        return ProgressViewHolder(loadingBinding)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                var layoutManager = recyclerView.layoutManager as GridLayoutManager
                val isLoading = currentList.isNotEmpty() && currentList.last().id == TYPE_PROGRESS
                val isLastPage = false
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount
                Timber.d("lastVisibleItemPosition $lastVisibleItemPosition && isLoading = $isLoading & totalItemCount=$totalItemCount")
                if(/*newState == 0 &&*/ !isLoading && totalItemCount == lastVisibleItemPosition + 1){
                    Timber.d("show loading and get more!!!!!!!!")
                    showLoading()
                    loadMore()
                }
            }

//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                var layoutManager = recyclerView.layoutManager as GridLayoutManager
//
////                val visibleItemCount = layoutManager.childCount
////                val totalItemCount = layoutManager.itemCount
////                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
////                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
////                val isLoading = currentList.isNotEmpty() && currentList.last().id == TYPE_PROGRESS
////                val isLastPage = false
////
////                Timber.d("visibleItemCount = [$visibleItemCount], totalItemCount = [$totalItemCount], firstVisibleItemPosition=$firstVisibleItemPosition, lastVisibleItemPosition = $lastVisibleItemPosition, isLoading = $isLoading  ")
////
////                val condition1 = (totalItemCount <= lastVisibleItemPosition + 10)
////                val condition2 = (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
////
////                if (!isLoading && !isLastPage) {
////                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 /*&& totalItemCount >= PAGE_SIZE*/) {
////                  //  if(condition1){
////                        Timber.d("show loading and get more!!!!!!!!")
////                        showLoading()
////                        loadMore()
////                    }
////                }
//
////                if (!isLoading) {
////                    if (layoutManager.findLastCompletelyVisibleItemPosition() == (currentList.size - 1)){
////                        Timber.d("show loading and get more!!!!!!!!")
////                        showLoading()
////                        loadMore()
////                    }
////                }
//            }
        })
    }

    fun showLoading(){
        val mutableList = currentList.toMutableList()
        mutableList.add(Movie("", "", "").apply {
            id = TYPE_PROGRESS
        })
        submitList(mutableList)
    }

    override fun getItemViewType(position: Int): Int {
        if(currentList[position].id == TYPE_PROGRESS){
            return TYPE_PROGRESS
        }
        return TYPE_DATA
    }

    class MovieViewHolder(val view: ItemMovieBinding): RecyclerView.ViewHolder(view.root) {

    }

    inner class ProgressViewHolder(val view: ProgressbarBinding): ViewHolder(view.root){

    }

    object MovieComparator: DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            // Id is unique.
            return oldItem.original_title == newItem.original_title
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }
}
