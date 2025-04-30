package com.blczy.maltiprac.navigation

class HomeContext() {}

class ListeningContext() {}

sealed class NavContext() {
    data class Home(var context: HomeContext = HomeContext()): NavContext()
    data class Listening(var context: ListeningContext = ListeningContext()): NavContext()
}