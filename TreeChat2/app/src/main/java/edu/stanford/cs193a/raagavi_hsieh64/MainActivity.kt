package edu.stanford.cs193a.raagavi_hsieh64

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class MainActivity : AppCompatActivity() {
    private lateinit var account: GoogleSignInAccount
    private lateinit var name: String
    private lateinit var google: GoogleSignInClient
    private val REQUEST_SIGN_IN_GOOGLE = 1932
    /* The onCreate method, if the user has logged in before,
     * processes the last logged in google account and starts
      * the next activity. Otherwise it calls the getGoogleAccount
      * method */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            account = GoogleSignIn.getLastSignedInAccount(this)!!
        }
        if (this::account.isInitialized) {
            name = account.displayName.toString()
            val myIntent = Intent(this, ChannelListActivity::class.java)
            myIntent.putExtra("account", name)
            startActivity(myIntent)

        } else {
            getGoogleAccount()
        }

    }
/* This method calls the google login API
* allowing the user to login using an existing google account
* on receiving a login result, the onActivityResult is invoked*/

    fun getGoogleAccount() {
        val options = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestEmail()
            .build()
        this.google = GoogleSignIn.getClient(this, options)
        startActivityForResult(this.google.signInIntent, REQUEST_SIGN_IN_GOOGLE)
    }
/* The override function onActivityResult, on receiving a login result from
* the google sign in intent and processes it, starting the intent for the
* next activity using the received user details*/
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int, intent: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_SIGN_IN_GOOGLE) {
            if (resultCode == Activity.RESULT_OK) {
                account = GoogleSignIn.getSignedInAccountFromIntent(intent).result!!
                name = account.displayName.toString()
                val myIntent = Intent(this, ChannelListActivity::class.java)
                myIntent.putExtra("account", name)
                startActivity(myIntent)

            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_LONG)
            }
        }
    }
}

