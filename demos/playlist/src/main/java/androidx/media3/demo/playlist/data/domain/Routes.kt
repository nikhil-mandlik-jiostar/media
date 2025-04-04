package androidx.media3.demo.playlist.data.domain

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ExoDemo : Screen("exodemo")
    object ExoPlayListDemo : Screen("exoplaylist")
    object ExoPreloadManagerDemo : Screen("exopreloadmanager")
}