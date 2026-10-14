package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.bookmarks.BookmarksScreen
import com.example.ui.category.CategoryDetailScreen
import com.example.ui.dua.DailyDuaScreen
import com.example.ui.home.HomeScreen
import com.example.ui.quiz.QuizListScreen
import com.example.ui.quiz.QuizPlayScreen
import com.example.ui.reader.PostDetailScreen
import com.example.ui.search.SearchScreen
import com.example.ui.settings.SettingsScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToCategory = { categoryId ->
                    navController.navigate(Screen.CategoryPosts.createRoute(categoryId))
                },
                onNavigateToSubcategory = { categoryId, subcategoryId ->
                    navController.navigate(Screen.CategoryPosts.createRoute(categoryId, subcategoryId))
                },
                onNavigateToPost = { postId ->
                    navController.navigate(Screen.PostDetail.createRoute(postId))
                },
                onNavigateToQuiz = { navController.navigate(Screen.QuizList.route) },
                onNavigateToDailyDua = { navController.navigate(Screen.DailyDua.route) },
                onNavigateToBookmarks = { navController.navigate(Screen.Bookmarks.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(
            route = Screen.CategoryPosts.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("subcategoryId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: "cat_quran"
            val subcategoryId = backStackEntry.arguments?.getString("subcategoryId")?.ifEmpty { null }
            CategoryDetailScreen(
                categoryId = categoryId,
                initialSubcategoryId = subcategoryId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPost = { postId ->
                    navController.navigate(Screen.PostDetail.createRoute(postId))
                }
            )
        }

        composable(
            route = Screen.PostDetail.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            PostDetailScreen(
                postId = postId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.DailyDua.route) {
            DailyDuaScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.QuizList.route) {
            QuizListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToQuizPlay = { quizId ->
                    navController.navigate(Screen.QuizPlay.createRoute(quizId))
                }
            )
        }

        composable(
            route = Screen.QuizPlay.route,
            arguments = listOf(navArgument("quizId") { type = NavType.StringType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            QuizPlayScreen(
                quizId = quizId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Bookmarks.route) {
            BookmarksScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPost = { postId ->
                    navController.navigate(Screen.PostDetail.createRoute(postId))
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPost = { postId ->
                    navController.navigate(Screen.PostDetail.createRoute(postId))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
