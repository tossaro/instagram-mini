package com.instagram.mini.presenter.signin

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.biometric.BiometricManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.instagram.mini.R
import com.instagram.mini.databinding.SignInFragmentBinding
import com.instagram.mini.presenter.base.BaseFragment
import timber.log.Timber

class SignInFragment : BaseFragment<SignInFragmentBinding, SignInViewModel>(
    layoutResId = R.layout.sign_in_fragment,
    SignInViewModel::class
) {

    var doubleBackToExitPressedOnce = false

    override fun actionBarTitle(): String = getString(R.string.sign_in_title)

    override fun initBinding(binding: SignInFragmentBinding, viewModel: SignInViewModel) {
        super.initBinding(binding, viewModel)
        binding.viewModel = viewModel.also {
            it.loadingIndicator.observe(this, ::loadingIndicator)
            it.alertMessage.observe(this, ::showAlert)
            it.user.observe(this, ::isSignedIn)
        }
        activity?.let {
            it.application?.let { app ->
                if (BiometricManager.from(app).canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) binding.btnFingerPrint.isEnabled = false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == viewModel.RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                viewModel.onGetAccessToken("google", account.idToken.toString())
            } catch (e: ApiException) {
                Timber.e("signInResult:failed code=" + e.statusCode)
            }
        } else {
            viewModel.callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            activity?.finish()
            return
        }

        doubleBackToExitPressedOnce = true
        Toast.makeText(context, getString(R.string.sign_in_tap_to_minimize), Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

}