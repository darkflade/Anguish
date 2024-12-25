package com.evilcorp.anguish

import TokenManager
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var network: GetRequestAndEtc

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        tokenManager = TokenManager(this)
        network = GetRequestAndEtc(this)

        val savedUsername = tokenManager.getUsername()
        val savedPassword = tokenManager.getPassword()
        val savedToken = tokenManager.getAccessToken()

        if (savedUsername != null && savedPassword != null) {

            if (savedToken == null) {

                CoroutineScope(Dispatchers.IO).launch {
                    if(network.authenticateUser(savedUsername, savedPassword) == "Success")
                    {
                        startActivity(Intent(this@MainActivity, ProfileActivity::class.java)) //------------------Ne poteryasya
                        finish()
                    }
                }
            }
            else {
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val button = findViewById<Button>(R.id.SignInButton)
        button.setOnClickListener{
            val username = findViewById<EditText>(R.id.EMail).text.toString();
            val password = findViewById<EditText>(R.id.Password).text.toString();

            if (username == "" && password == "") {
                Toast.makeText(
                    this, "Debug Mode Activated",
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(this, ProfileActivity::class.java))
            }   else {
                CoroutineScope(Dispatchers.IO).launch {

                    withContext(Dispatchers.Main) {
                        showLoadingDialog("Authenticate...")
                    }

                    val status = network.authenticateUser(username, password)

                    withContext(Dispatchers.Main) {
                        dismissLoadingDialog()
                    }

                    if(status == "Success")
                    {
                        val token = tokenManager.getAccessToken().toString()

                        withContext(Dispatchers.Main) {
                            showLoadingDialog("Load Profile...")
                        }
                        network.getUserProfile(token)

                        withContext(Dispatchers.Main) {
                            dismissLoadingDialog()
                        }

                        async { network.getTimeTableFor2Weeks(token) }

                        startActivity(Intent(this@MainActivity, ProfileActivity::class.java)) //------------------Ne poteryasya
                        finish()
                    } else {
                        if (status == "Wrong Login or Password") {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@MainActivity, status, Toast.LENGTH_SHORT)
                                    .show()
                            }
                            changeColor(Color.RED)
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@MainActivity, status, Toast.LENGTH_SHORT)
                                    .show()
                            }
                            changeColor(Color.BLUE)
                        }
                    }
                }
            }

        }
    }

    fun dismissLoadingDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    // Modify showLoadingDialog to keep a reference to the dialog
    var dialog: Dialog? = null

    fun showLoadingDialog(text: String) {
        dialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
        }

        val view = LayoutInflater.from(this).inflate(R.layout.loading_dialog, null).apply {
            findViewById<TextView>(R.id.loadingText).text = text
        }

        dialog!!.setContentView(view)

        val layoutParams = WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            gravity = Gravity.CENTER
        }
        dialog!!.window?.attributes = layoutParams

        dialog!!.show()
    }




    private fun changeColor(color: Int) {
        findViewById<EditText>(R.id.EMail).setBackgroundTintList(ColorStateList.valueOf(color))
        findViewById<EditText>(R.id.Password).setBackgroundTintList(ColorStateList.valueOf(color))
    }

    override fun onDestroy() {
        super.onDestroy()
        //shimmerTextView.stopShimmer()
    }
}



