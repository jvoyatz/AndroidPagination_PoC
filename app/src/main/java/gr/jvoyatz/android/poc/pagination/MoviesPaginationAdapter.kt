package gr.jvoyatz.android.poc.pagination

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import gr.jvoyatz.android.poc.pagination.databinding.ItemMovieBinding

class MoviesPaginationAdapter : PagingDataAdapter<Movie, MoviesPaginationAdapter.MovieViewHolder>(MOVIE_DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)!!
        holder.view.name.text = movie.original_title
        Glide.with(holder.itemView.context).load("https://image.tmdb.org/t/p/w300"+movie.poster_path).into(holder.view.imageview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMovieBinding.inflate(inflater, parent, false)
        return MovieViewHolder(binding)
    }

    class MovieViewHolder(val view: ItemMovieBinding): RecyclerView.ViewHolder(view.root) {

    }

    object MOVIE_DIFF_CALLBACK: DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.original_title == newItem.original_title
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }
}