package com.instagram.mini.presenter.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.instagram.mini.databinding.FavoriteRowBinding
import com.instagram.mini.domain.entities.Art


class FavoriteAdapter :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {
    var arts = mutableListOf<Art>()
    var onClick: ((art: Art) -> Unit)? = null
    var onDelete: ((i: Int) -> Unit)? = null
    
    class ViewHolder(private val binding: FavoriteRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(art: Art, onClick: ((art: Art) -> Unit)?, onDelete: ((i: Int) -> Unit)?) {
            binding.tvName.text = art.title
            binding.tvInscription.text = art.inscriptions ?: "-"
            binding.tvScore.text = art.colorfulness

            val circularProgressDrawable = CircularProgressDrawable(binding.root.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()
            Glide.with(binding.root.context)
                .load("https://lakeimagesweb.artic.edu/iiif/2/${art.image_id}/full/843,/0/default.jpg")
                .placeholder(circularProgressDrawable)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .override(500,500)
                .into(binding.ivThumbnail)
            binding.ivThumbnail.setOnClickListener { onClick?.invoke(art) }
            binding.ivDelete.setOnClickListener { onDelete?.invoke(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = FavoriteRowBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(arts[position], onClick, onDelete)
    }

    override fun getItemCount() = arts.size
}