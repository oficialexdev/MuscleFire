package com.oficialexdev.musclefire.components

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
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
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeComponent(user: FirebaseUser, logOutCallback: () -> Unit) {
    val db = Firebase.firestore
    val storage = Firebase.storage
    val coroutine = rememberCoroutineScope()
    val userId: String? by remember { mutableStateOf(user.email?.split("@")?.first()) }
    val modalSheetStateCreate = rememberModalBottomSheetState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            if (isSystemInDarkTheme())
                colorResource(
                    id = android.R.color.system_accent1_1000
                )
            else colorResource(id = android.R.color.system_accent1_10)
        else
            if (isSystemInDarkTheme())
                Color.Black
            else Color.White,
        contentColor = if (isSystemInDarkTheme())
            Color.White
        else Color.Black,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    coroutine.launch {
                        modalSheetStateCreate.show()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arm),
                    contentDescription = "Add icon",
                    Modifier
                        .width(24.dp)
                        .padding(top = 2.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    Box {
                        var expanded: Boolean by remember { mutableStateOf(false) }
                        val uriHandler = LocalUriHandler.current
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
                            DropdownMenuItem(text = { Text(text = stringResource(id = R.string.logout)) }, onClick = {
                                Firebase.auth.signOut()
                                logOutCallback.invoke()
                            })
                            DropdownMenuItem(text = { Text(text = stringResource(id = R.string.about)) }, onClick = {
                                uriHandler.openUri("https://oficialexdev.github.io/portfolio")
                            })
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        if (isSystemInDarkTheme())
                            colorResource(
                                id = android.R.color.system_accent1_600
                            )
                        else colorResource(id = android.R.color.system_accent1_300)
                    else
                        if (isSystemInDarkTheme())
                            Color.Black
                        else Color.White,
                ),
            )
        }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            userId?.let { TrainingView(userId = it, db = db,storage, modalSheetStateCreate) }
        }
    }

}