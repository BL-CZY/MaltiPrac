package com.blczy.maltiprac.navigation

import com.blczy.maltiprac.listening.Category

class HomeContext() {}

class ListeningContext() {}

class ListeningCategoryContext(var category: Category = Category.Shopping) {}

class PsmContext(var id: Int = 0, var category: Category = Category.Shopping) {}

sealed class NavContext() {
    data class Home(var context: HomeContext = HomeContext()) : NavContext()
    data class Listening(var context: ListeningContext = ListeningContext()) : NavContext()
    data class ListeningCategory(var context: ListeningCategoryContext = ListeningCategoryContext()) :
        NavContext()
    data class Psm(var context: PsmContext = PsmContext()): NavContext()
}