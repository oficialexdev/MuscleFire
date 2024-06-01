package com.oficialexdev.musclefire.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.oficialexdev.musclefire.R
import com.oficialexdev.musclefire.view.TrainingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeComponent(user: FirebaseUser, logOutCallback: () -> Unit) {
    val db = Firebase.firestore
    val storageReference = Firebase.storage
    val userId: String? by remember { mutableStateOf(user.email?.split("@")?.first()) }

    Box(
        Modifier.background(if (isSystemInDarkTheme()) Color.Black else Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.musclefire),
            contentDescription = "Icon",
            Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .blur(28.dp)
                .alpha(0.08f)
        )
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.app_name)) },
                    actions = {
                        Box {
                            var expanded: Boolean by remember { mutableStateOf(false) }
                            IconButton(
                                onClick = {
                                    expanded = true
                                },
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                AsyncImage(
                                    model = user.photoUrl,
                                    contentDescription = "User Picture",
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100))

                                )
                            }
                            DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(text = { Text(text = "LogOut") }, onClick = {
                                    Firebase.auth.signOut()
                                    logOutCallback.invoke()
                                })
                            }
                        }
                    }
                )
            }

        ) { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
                userId?.let { TrainingView(userId = it, db = db) }
            }
        }
    }
}