package com.instagram.mini.external.utils

import android.os.Build
import android.text.Html
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import org.koin.core.KoinComponent
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

object BindingAdapters: KoinComponent {

    @BindingAdapter("app:errorText")
    @JvmStatic fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
        view.error = errorMessage
    }

    @BindingAdapter("onRefreshListener")
    @JvmStatic fun onRefreshListener(swiper: SwipeRefreshLayout, listener: () -> Unit){
        swiper.setOnRefreshListener { listener.invoke() }
    }

    @BindingAdapter("textHtml")
    @JvmStatic fun textHtml(textView: TextView, html: String?) {
        if (!html.isNullOrEmpty()) {
            textView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            else Html.fromHtml(html)
        }
    }

}