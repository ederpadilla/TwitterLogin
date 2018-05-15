package dev.eder.padilla.twitterlogin

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import android.content.Intent
import android.util.Log
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.internal.CallbackManagerImpl
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.TwitterConfig
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.internal.TwitterApiConstants
import com.twitter.sdk.android.core.models.User
import org.json.JSONException
import org.json.JSONObject
import java.net.MalformedURLException
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity(), FacebookCallback<LoginResult> {

    lateinit var mTwitterAuthClient : TwitterAuthClient

    val permissionNeeds = Arrays.asList("user_photos", "email",
            "user_birthday", "public_profile")


    private var mCallbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val config = TwitterConfig.Builder(this)
                .logger(DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(TwitterAuthConfig("KWtTxkP4osga3LxoT9rnT2XaG", "lUD4GdeUSh3jt4Hg0NSjkHfJU15pPUKVB7DurRMIaNaQSFNgIY"))
                .debug(true)
                .build()
        Twitter.initialize(config)
        mTwitterAuthClient = TwitterAuthClient()
        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(this)
        mCallbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(mCallbackManager, this)

    }

    fun twitterLogin(view : View){
        mTwitterAuthClient.authorize(this, object : com.twitter.sdk.android.core.Callback<TwitterSession>() {

            override fun success(twitterSessionResult: Result<TwitterSession>) {
                Log.e("MainActivity","😬😬😬authtoken ${twitterSessionResult.data.authToken} ${twitterSessionResult.toString()} ${twitterSessionResult.data.userName}")
                TwitterCore.getInstance().apiClient.accountService.verifyCredentials(true, true, false).enqueue(object : Callback<User>() {
                    override fun success(userResult: Result<User>) {
                        Log.e("MainActivity","😬😬😬user ${userResult.response.body().toString()} data ${userResult.data.toString()} default profile ${userResult.data.defaultProfileImage}" +
                                "profile img url ${userResult.data.profileImageUrl} ")

                    }

                    override fun failure(exception: TwitterException) {

                    }
                })
                // Success

            }

            override fun failure(e: TwitterException) {
                e.printStackTrace()
                Log.e("👹👹👹 Failure", " ${e.localizedMessage}  ${e.cause}")
            }
        })
    }

    fun facebookLogin(view : View){
        LoginManager.getInstance().logInWithReadPermissions(this,
                permissionNeeds)

    }

    private lateinit var profile_pic: String

    override fun onSuccess(loginResult: LoginResult?) {
        Log.e("MainActivity","😬😬😬onsuccess")
        val accessToken = loginResult!!.getAccessToken()
                .getToken()
        Log.i("accessToken", accessToken)

        val request = GraphRequest.newMeRequest(
                loginResult.getAccessToken()) {
            `object`, response ->
            Log.i("LoginActivity",
                    response.toString())
            try {
                var id = `object`.getString("id")
                try {
                     val profile_picc = URL(
                            "http://graph.facebook.com/$id/picture?type=large")
                    Log.i("profile_pic",
                            profile_picc.toString() + "")

                } catch (e: MalformedURLException) {
                    Log.e("👹👹👹 Failure", "second catch")
                    e.printStackTrace()
                }
                if (`object`.has("picture")) {
                    val pictureJson = `object`.getJSONObject("picture")
                    val dataJson = pictureJson.getJSONObject("data")
                    profile_pic = dataJson.getString("url")
                } else {
                    profile_pic = ""
                }

                val name = `object`.getString("name")
                val email = `object`.getString("email")
                Log.e("MainActivity","😬😬😬onsuccess name $name email $email profile_pic $profile_pic ")
            } catch (e: JSONException) {
                Log.e("👹👹👹 Failure", "first catch")
                e.printStackTrace()
            }
        }
       /* val parameters = Bundle()
        parameters.putString("fields",
                "id,name,email")
        request.parameters = parameters
        request.executeAsync()*/
    }

    override fun onCancel() {
        Log.e("👹👹👹 Failure", "Cancel")
    }

    override fun onError(error: FacebookException?) {
        Log.e("👹👹👹 Failure", " ${error!!.localizedMessage}  ${error.cause}")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==  140 && resultCode == Activity.RESULT_OK){
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, intent)
        }
        if (FacebookSdk.isFacebookRequestCode(requestCode) && resultCode == Activity.RESULT_OK) {
            //Facebook activity result
            //Do your stuff here
            //Further you can also check if it's login or Share etc by using
            //CallbackManagerImpl as explained by rajath's answer here
            if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
                //login
                mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}
