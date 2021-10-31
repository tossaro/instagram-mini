package com.instagram.mini.presenter.base

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.instagram.mini.R
import com.instagram.mini.databinding.BaseFragmentBinding
import com.instagram.mini.domain.entities.User
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

open class BaseFragment<Binding: ViewDataBinding, ViewModel: BaseViewModel>(
    @LayoutRes val layoutResId: Int,
    viewModelClass: KClass<ViewModel>
): Fragment() {

    private var _basebinding: BaseFragmentBinding? = null
    private val basebinding get() = _basebinding!!

    open val viewModel: ViewModel by viewModel(viewModelClass)
    open lateinit var binding: Binding
    protected open fun initBinding(binding: Binding, viewModel: ViewModel) {}

    private var onBackPressedCallback: OnBackPressedCallback? = null

    protected open fun isActionBarShown() = true
    protected open fun getActionToolbar(): Toolbar? = _basebinding?.toolbar
    protected open fun actionBarTitle(): String? = null
    protected open fun isOnBackPressedEnabled() = true
    protected open fun doesFitSystemWindows() = false
    protected open fun isStatusBarTranslucent() = false
    protected open fun isFullScreen() = false
    protected open fun isNavigationBarTranslucent() = false
    protected open fun getSoftInputMode() = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
    protected open fun getBottomNav(): BottomNavigationView? = _basebinding?.bottomNav

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _basebinding = BaseFragmentBinding.inflate(inflater, container, false).apply {
            binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
            fragmentContent.removeAllViews()
            fragmentContent.addView(binding.root)
        }
        return basebinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _basebinding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        takeIf { isActionBarShown() }?.run { getActionToolbar() }?.run {
            (activity as? AppCompatActivity)?.setSupportActionBar(this)
            (activity as? AppCompatActivity)?.let { NavigationUI.setupActionBarWithNavController(it, findNavController()) }
            (activity as? AppCompatActivity)?.supportActionBar?.let(this@BaseFragment::configureActionBar)
        }
        getBottomNav()?.let {
            NavigationUI.setupWithNavController(it, findNavController())
        }
        initViews()
    }

    @SuppressLint("RestrictedApi")
    protected open fun configureActionBar(actionBar: ActionBar) {
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(false)
    }

    protected open fun onBackPressed() {
        findNavController().popBackStack()
    }

    open fun initViews() {
        activity?.window?.run {
            if (isFullScreen()) addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            else clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

            if (isStatusBarTranslucent()) {
                setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                )
            } else clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            if (isNavigationBarTranslucent()) {
                setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                )
            } else clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)

            setSoftInputMode(getSoftInputMode())
        }

        onBackPressedCallback?.remove()
        onBackPressedCallback = object : OnBackPressedCallback(isOnBackPressedEnabled()) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        onBackPressedCallback?.let {
            activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, it)
        }

        _basebinding?.apply {
            fragmentContent.fitsSystemWindows = doesFitSystemWindows()
            toolbarContainer.isVisible = isActionBarShown()
            takeIf { !actionBarTitle().isNullOrEmpty() }?.run {
                toolbar.apply { tvTitle.text = actionBarTitle() }
            }
        }

        initBinding(binding, viewModel)
    }

    fun loadingIndicator(visibility: Boolean = true) {
        _basebinding?.apply {
            progressView.visibility = if (visibility) View.VISIBLE else View.GONE
        }
    }

    fun showAlert(messageString: String) {
        Toast.makeText(context, messageString, Toast.LENGTH_LONG).show()
    }

    fun isSignedIn(user: User?) {
        user?.let {
            view?.let { v -> Navigation.findNavController(v).navigate(R.id.actionToArtListFragment) }
        } ?: run {
            view?.let { v -> Navigation.findNavController(v).navigate(R.id.actionToSignInFragment) }
        }
    }

    fun hideKeyboard() {
        val currentFocus = if (this is DialogFragment) dialog?.currentFocus else activity?.currentFocus
        val imm = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

}