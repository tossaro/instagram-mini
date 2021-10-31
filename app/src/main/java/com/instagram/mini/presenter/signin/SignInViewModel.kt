package com.instagram.mini.presenter.signin

import android.app.Activity
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Base64
import android.view.View
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.instagram.mini.BuildConfig
import com.instagram.mini.R
import com.instagram.mini.domain.entities.User
import com.instagram.mini.domain.interactors.SetUserLocalIteractor
import com.instagram.mini.presenter.base.BaseViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.security.*
import javax.crypto.*
import javax.security.cert.CertificateException

class SignInViewModel(private val setUserLocal: SetUserLocalIteractor) : BaseViewModel() {
    val RC_SIGN_IN = 1001
    private lateinit var keyStore: KeyStore
    private lateinit var keyGenerator: KeyGenerator
    private val ANDROID_KEY_STORE = "AndroidKeyStore"
    val user = MutableLiveData<User>()
    val userIdString = MutableLiveData<String>()
    val passwordString = MutableLiveData<String>()
    val errorUserIdString = MutableLiveData<String>()
    val errorPasswordString = MutableLiveData<String>()
    val callbackManager: CallbackManager by lazy {
        CallbackManager.Factory.create()
    }

    fun signInByGoogle(view: View) {
        loadingIndicator.value = true
        val account = GoogleSignIn.getLastSignedInAccount(view.context)
        account?.let {
            onGetAccessToken("google", it.idToken.toString())
        } ?: run {
            loadingIndicator.value = false
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(view.context.getString(R.string.GOOGLE_WEB_CLIENT_ID))
                .requestEmail()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(view.context, gso)
            val signInIntent = mGoogleSignInClient.signInIntent
            (view.context as Activity).startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    fun signInByFacebook(view: View) {
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true)
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
        }

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    onGetAccessToken("facebook", loginResult.accessToken.token)
                }
                override fun onCancel() {
                    loadingIndicator.value = false
                    Timber.d("Login cancelled")
                }
                override fun onError(error: FacebookException) {
                    onGetAccessTokenFail(error.message)
                }
            })

        val accessToken = AccessToken.getCurrentAccessToken()
        if (accessToken != null && !accessToken.isExpired) {
            onGetAccessToken("facebook", accessToken.toString())
        } else {
            loadingIndicator.value = false
            LoginManager.getInstance()
                .logInWithReadPermissions(view.context as Activity, listOf("public_profile", "email"))
            LoginManager.getInstance()
                .retrieveLoginStatus(view.context as Activity, object : LoginStatusCallback {
                    override fun onCompleted(accessToken: AccessToken) {
                        onGetAccessToken("facebook", accessToken.token)
                    }
                    override fun onFailure() {
                        loadingIndicator.value = false
                        Timber.d("Login cancelled")
                    }
                    override fun onError(exception: Exception) {
                        onGetAccessTokenFail(exception.message)
                    }
                })
        }
    }

    fun onGetAccessToken(provider: String, accessToken: String) = viewModelScope.launch {
        loadingIndicator.value = false
        setUserToLocal(User(id = "ABC123", name = provider, email = "email@$provider.com", token = accessToken))
    }

    fun onGetAccessTokenFail(error: String?) {
        loadingIndicator.value = false
        alertMessage.value = error
    }

    fun goToForgotPassword(view: View) {
        alertMessage.value = "Forgot Password Screen"
    }

    fun goToRegister(view: View) {
        alertMessage.value = "Register Screen"
    }

    fun setUserToLocal(mUser: User) = viewModelScope.launch {
        setUserLocal(mUser)
        user.value = mUser
    }

    fun signIn(view: View) {
        loadingIndicator.value = true
        errorUserIdString.value = ""
        if (userIdString.value.isNullOrEmpty()) {
            loadingIndicator.value = false
            errorUserIdString.value = view.context?.getString(R.string.sign_in_error_empty_field) ?: "Error"
            return
        }
        errorPasswordString.value = ""
        if (passwordString.value.isNullOrEmpty()) {
            loadingIndicator.value = false
            errorPasswordString.value = view.context?.getString(R.string.sign_in_error_empty_field) ?: "Error"
            return
        }
        viewModelScope.launch {
            loadingIndicator.value = false
            setUserToLocal(User(id = "ABC123", name = "Dummy", email = userIdString.value.toString(), token = Base64.encodeToString("dummy-123!".toByteArray(), 0)))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun signInFingerprint(view: View) {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to get an instance of KeyStore", e)
        }

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is NoSuchProviderException ->
                    throw RuntimeException("Failed to get an instance of KeyGenerator", e)
                else -> throw e
            }
        }

        try {
            keyStore.load(null)

            val keyProperties = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                val builder = KeyGenParameterSpec.Builder("default_key", keyProperties)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)

            keyGenerator.run {
                init(builder.build())
                generateKey()
            }
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is InvalidAlgorithmParameterException,
                is CertificateException,
                is IOException -> throw RuntimeException(e)
                else -> throw e
            }
        }

        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to get an instance of KeyStore", e)
        }

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is NoSuchProviderException ->
                    throw RuntimeException("Failed to get an instance of KeyGenerator", e)
                else -> throw e
            }
        }
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(view.context.getString(R.string.sign_in_finger_title))
            .setDescription(view.context.getString(R.string.sign_in_finger_description))
            .setConfirmationRequired(false)
            .setNegativeButtonText(view.context.getString(R.string.sign_in_finger_use_app_password))
            .build()
        val cipherString = "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
        val cipher = Cipher.getInstance(cipherString)
        if (initCipher(cipher, "default_key")) {
            val executor = ContextCompat.getMainExecutor(view.context)
            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Timber.e("$errorCode :: $errString")
                }
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Timber.e("Authentication failed for an unknown reason")
                }
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Timber.d("Authentication was successful")
                    result.cryptoObject?.cipher?.let {
                        try {
                            val encrypted = it.doFinal("Very secret message".toByteArray())
                            val encryptedString = Base64.encodeToString(encrypted, 0 /* flags */)
                            setUserToLocal(User(id = "ABC123", name = "Fingerprint", email = "email@biometric.com", token = encryptedString))
                        } catch (e: Exception) {
                            when (e) {
                                is BadPaddingException,
                                is IllegalBlockSizeException -> {
                                    Timber.e("Failed to encrypt the data with the generated key. ${e.message}")
                                }
                                else -> throw e
                            }
                        }
                    }
                }
            }

            val biometricPrompt = BiometricPrompt(view.context as FragmentActivity, executor, callback)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initCipher(cipher: Cipher, keyName: String): Boolean {
        try {
            keyStore.load(null)
            cipher.init(Cipher.ENCRYPT_MODE, keyStore.getKey(keyName, null) as SecretKey)
            return true
        } catch (e: Exception) {
            when (e) {
                is KeyPermanentlyInvalidatedException -> return false
                is KeyStoreException,
                is CertificateException,
                is UnrecoverableKeyException,
                is IOException,
                is NoSuchAlgorithmException,
                is InvalidKeyException -> throw RuntimeException("Failed to init Cipher", e)
                else -> throw e
            }
        }
    }
}