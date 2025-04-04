/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.media3.demo.playlist.data.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.demo.playlist.data.domain.Screen
import androidx.media3.demo.playlist.data.ui.screen.HomeScreen
import androidx.media3.demo.playlist.data.ui.screen.exo_demo.ExoDemoScreen
import androidx.media3.demo.playlist.data.ui.screen.exo_playlist_demo.ExoPlayListDemoScreen
import androidx.media3.demo.playlist.data.ui.screen.exo_preload_manager.ExoPreloadManagerDemoScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ComposeDemoApp() }
    }
}

@Composable
fun ComposeDemoApp(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->

        val navController = rememberNavController()

        NavHost(
            modifier = modifier.padding(innerPadding),
            navController = navController,
            startDestination = Screen.Home.route
        ) {

            composable(
                route = Screen.Home.route
            ) {
                HomeScreen(
                    navigate = {
                        navController.navigate(it)
                    }
                )
            }

            composable(
                route = Screen.ExoDemo.route
            ) {
                ExoDemoScreen(
                    goBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.ExoPlayListDemo.route
            ) {
                ExoPlayListDemoScreen (
                    goBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.ExoPreloadManagerDemo.route
            ) {
                ExoPreloadManagerDemoScreen (
                    goBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

    }
}


