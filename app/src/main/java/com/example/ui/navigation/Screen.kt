package com.example.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Categories : Screen("categories")
    data object Subcategories : Screen("subcategories/{categoryId}") {
        fun createRoute(categoryId: String) = "subcategories/$categoryId"
    }
    data object CategoryPosts : Screen("category_posts/{categoryId}?subcategoryId={subcategoryId}") {
        fun createRoute(categoryId: String, subcategoryId: String? = null) =
            if (subcategoryId != null) "category_posts/$categoryId?subcategoryId=$subcategoryId"
            else "category_posts/$categoryId?subcategoryId="
    }
    data object PostDetail : Screen("post_detail/{postId}") {
        fun createRoute(postId: String) = "post_detail/$postId"
    }
    data object DailyDua : Screen("daily_dua")
    data object QuizList : Screen("quiz_list")
    data object QuizPlay : Screen("quiz_play/{quizId}") {
        fun createRoute(quizId: String) = "quiz_play/$quizId"
    }
    data object Bookmarks : Screen("bookmarks")
    data object History : Screen("history")
    data object Search : Screen("search")
    data object Settings : Screen("settings")
}
