package dev.eder.padilla.twitterlogin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import android.content.Intent
import android.util.Log
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.TwitterConfig
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.models.User


class MainActivity : AppCompatActivity() {
    lateinit var mTwitterAuthClient : TwitterAuthClient

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

    override fun onActivityResult(requestCode: Int, responseCode: Int, intent: Intent) {
        mTwitterAuthClient.onActivityResult(requestCode, responseCode, intent)
    }
}
