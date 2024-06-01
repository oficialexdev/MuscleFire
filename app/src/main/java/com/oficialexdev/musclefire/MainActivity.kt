package com.oficialexdev.musclefire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.oficialexdev.musclefire.components.AuthComponent
import com.oficialexdev.musclefire.components.HomeComponent
import com.oficialexdev.musclefire.ui.theme.MuscleFireTheme

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        enableEdgeToEdge()
        setContent {
            var signed: Boolean by remember { mutableStateOf(auth.currentUser != null) }
            MuscleFireTheme {
                if (signed) {
                    HomeComponent(auth.currentUser!!) {
                        signed = false
                    }

                } else {
                    AuthComponent(auth) {
                        signed = true
                    }
                }
            }
        }
    }
}

