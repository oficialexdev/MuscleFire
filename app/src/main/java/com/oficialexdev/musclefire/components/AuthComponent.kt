package com.oficialexdev.musclefire.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.oficialexdev.musclefire.R
import kotlinx.coroutines.delay


@Composable
fun AuthComponent(auth: FirebaseAuth, signInCallBack: () -> Unit) {
    val context = LocalContext.current
    val clientId: String = stringResource(id = R.string.client_id)
    var load: Boolean by remember { mutableStateOf(false) }
    var signed: Boolean by remember { mutableStateOf(false) }

    suspend fun untilUser() {
        if (auth.currentUser != null) {
            signInCallBack.invoke()
        } else {
            delay(10)
            untilUser()
        }
    }

    LaunchedEffect(signed) {
        if (signed) {
            untilUser()
        }
    }

    @Suppress("DEPRECATION")
    val sigInLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            val credentials = try {
                val result = task.result
                GoogleAuthProvider.getCredential(result.idToken, null)
            } catch (e: Throwable) {
//                println("ERROR ON GET CREDENTIALS $e")
                null
            }

            if (credentials != null) {
                try {
                    auth.signInWithCredential(credentials)
                    signed = true
//                    Toast.makeText(context, "Logged", Toast.LENGTH_SHORT).show()
                } catch (e: Throwable) {
//                    println("ERR $e")
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            }
            load = false
        }

    fun requestLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()

        @Suppress("DEPRECATION")
        val googleClient = GoogleSignIn.getClient(context, gso)
        sigInLauncher.launch(googleClient.signInIntent)
    }

    Surface(
        contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(if (isSystemInDarkTheme()) Color.Black else Color.White),
            Arrangement.SpaceEvenly,
            Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.musclefire),
                    contentDescription = "App Icon",
                    Modifier
                        .alpha(0.6f)
                )
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
            }
            if (load) {
                CircularProgressIndicator(
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
            } else {
                Button(onClick = {
                    load = true
                    requestLogin()
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.brand_google),
                        contentDescription = "Google Icon",
                        Modifier.padding(end = 4.dp)
                    )
                    Text(text = "Login with Google")
                }
            }
        }
    }
}

