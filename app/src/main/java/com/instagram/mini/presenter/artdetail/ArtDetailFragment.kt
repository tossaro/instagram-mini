package com.instagram.mini.presenter.artdetail

import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.instagram.mini.R
import com.instagram.mini.databinding.ArtDetailFragmentBinding
import com.instagram.mini.presenter.artlist.ArtListFragmentArgs
import com.instagram.mini.presenter.base.BaseFragment

class ArtDetailFragment : BaseFragment<ArtDetailFragmentBinding, ArtDetailViewModel>(
    layoutResId = R.layout.art_detail_fragment,
    ArtDetailViewModel::class
) {

    override fun actionBarTitle(): String {
        var title = ""
        arguments?.let { b ->
            title = ArtListFragmentArgs.fromBundle(b).title
            if (title.length > 30) title = title.substring(0, 20) + "..."
        }
        return title
    }

    override fun initBinding(binding: ArtDetailFragmentBinding, viewModel: ArtDetailViewModel) {
        super.initBinding(binding, viewModel)
        binding.viewModel = viewModel.also {
            it.art.observe(this, { art ->
                with (binding) {
                    var credit = art.credit_line ?: "-"
                    if (credit.length > 30) credit = credit.substring(0, 20) + "..."
                    tvCredit.text = credit
                    tvInscription.text = art.inscriptions ?: "-"
                    tvProvenance.text = art.provenance_text ?: "-"
                    tvPublication.text = art.publication_history ?: "-"
                    tvExhibition.text = art.exhibition_history ?: "-"
                }

                val circularProgressDrawable = CircularProgressDrawable(binding.root.context)
                circularProgressDrawable.strokeWidth = 5f
                circularProgressDrawable.centerRadius = 30f
                circularProgressDrawable.start()
                Glide.with(binding.root.context)
                    .load("https://lakeimagesweb.artic.edu/iiif/2/${art.image_id.toString()}/full/843,/0/default.jpg")
                    .placeholder(circularProgressDrawable)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(200,200)
                    .fitCenter()
                    .into(binding.ivThumbnail)

                val drawId = if (art.favorite == 1) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                binding.ivFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), drawId))
            })
            arguments?.let { b ->
                it.getArtFromLocal(ArtListFragmentArgs.fromBundle(b).id)
            }
        }
    }
}