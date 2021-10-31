package com.instagram.mini.presenter.artlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.instagram.mini.databinding.ArtRowBinding
import com.instagram.mini.domain.entities.Art


class ArtAdapter :
    RecyclerView.Adapter<ArtAdapter.ViewHolder>() {
    var arts = mutableListOf<Art>()
    var onClick: ((art: Art) -> Unit)? = null
    
    class ViewHolder(private val binding: ArtRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(art: Art, onClick: ((art: Art) -> Unit)?) {
            val circularProgressDrawable = CircularProgressDrawable(binding.root.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()
            Glide.with(binding.root.context)
                .load("https://lakeimagesweb.artic.edu/iiif/2/${art.image_id}/full/843,/0/default.jpg")
                .placeholder(circularProgressDrawable)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(200,200)
                .fitCenter()
                .into(binding.ivThumbnail)
            binding.ivThumbnail.setOnClickListener { onClick?.invoke(art) }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ArtRowBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(arts[position], onClick)
    }

    override fun getItemCount() = arts.size
}