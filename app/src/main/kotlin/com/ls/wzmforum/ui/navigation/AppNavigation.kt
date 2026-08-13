package com.ls.wzmforum.ui.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ls.librarybase.bean.ResArticleList
import com.ls.librarybase.navigation.LoginStateBus
import com.ls.librarybase.navigation.NavRoutes
import com.ls.librarybase.ui.article.ArticleDetailScreen
import com.ls.librarybase.ui.search.SearchScreen
import com.ls.user.config.UserConfig
import com.ls.user.ui.aboutme.AboutMeScreen
import com.ls.user.ui.agreement.AgreementScreen
import com.ls.user.ui.camera.CameraScreen
import com.ls.user.ui.collection.CollectionScreen
import com.ls.user.ui.comment.CommentScreen
import com.ls.user.ui.editinfo.EditUserInfoScreen
import com.ls.user.ui.infomenu.UserInfoMenuScreen
import com.ls.user.ui.login.LoginScreen
import com.ls.user.ui.register.RegisterScreen
import com.ls.user.ui.user.UserViewModel.UserCenterAction
import com.ls.wzmforum.ui.main.MainScreen

/**
 * 应用导航图（单 Activity + NavHost + 登录守卫）
 */
@Composable
fun AppNavigation(onExit: () -> Unit) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val loginState by LoginStateBus.loginState.collectAsStateWithLifecycle()

    fun guardedNavigate(route: String) {
        if (loginState) {
            navController.navigate(route)
        } else {
            navController.navigate(NavRoutes.LOGIN)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            navController.navigate(NavRoutes.CAMERA)
        } else {
            Toast.makeText(context, "权限获取失败，请在系统设置手动授权", Toast.LENGTH_SHORT).show()
        }
    }

    val onArticleClick: (ResArticleList.ListBean) -> Unit = { item ->
        navController.navigate(NavRoutes.articleDetail(item.id.toString()))
    }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.MAIN
    ) {
        composable(NavRoutes.MAIN) {
            MainScreen(
                onExit = onExit,
                onArticleClick = onArticleClick,
                onSearchClick = { navController.navigate(NavRoutes.SEARCH) },
                onUserNavigate = { action ->
                    when (action) {
                        UserCenterAction.NAVIGATE_TO_LOGIN -> navController.navigate(NavRoutes.LOGIN)
                        UserCenterAction.NAVIGATION_TO_EDIT_INFO -> guardedNavigate(NavRoutes.EDIT_USER_INFO)
                        UserCenterAction.NAVIGATION_TO_COMMENT -> guardedNavigate(NavRoutes.COMMENT)
                        UserCenterAction.NAVIGATION_TO_COLLECTION -> guardedNavigate(NavRoutes.COLLECTION)
                        UserCenterAction.NAVIGATE_TO_USER_INFO_MENU -> guardedNavigate(NavRoutes.USER_INFO_MENU)
                        UserCenterAction.NAVIGATE_TO_ABOUT_ME -> navController.navigate(NavRoutes.ABOUT_ME)
                        UserCenterAction.SHOW_LOGOUT_DIALOG -> Unit
                    }
                }
            )
        }

        composable(NavRoutes.ARTICLE_DETAIL) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString("articleId").orEmpty()
            ArticleDetailScreen(
                vm = viewModel(),
                articleId = articleId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.SEARCH) {
            SearchScreen(
                vm = viewModel(),
                onBack = { navController.popBackStack() },
                onArticleClick = onArticleClick
            )
        }

        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onBack = { navController.popBackStack() },
                onRegister = { navController.navigate(NavRoutes.REGISTER) },
                onOpenAgreement = { type -> navController.navigate(NavRoutes.agreement(type)) },
                onLoginSuccess = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onAgreementClick = {
                    navController.navigate(NavRoutes.agreement(UserConfig.AgreementType.VALUE_AGREEMENT))
                },
                onPrivacyClick = {
                    navController.navigate(NavRoutes.agreement(UserConfig.AgreementType.VALUE_PRIVATE))
                },
                onRegisterSuccess = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.AGREEMENT) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type")?.toIntOrNull() ?: 0
            AgreementScreen(
                mType = type,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.USER_INFO_MENU) {
            UserInfoMenuScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.EDIT_USER_INFO) { backStackEntry ->
            val avatarUriString by backStackEntry.savedStateHandle
                .getStateFlow<String?>("avatarUri", null)
                .collectAsStateWithLifecycle()
            EditUserInfoScreen(
                vm = viewModel(),
                pickedAvatarUri = avatarUriString?.let { Uri.parse(it) },
                onCameraClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        navController.navigate(NavRoutes.CAMERA)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                onFinish = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.CAMERA) {
            CameraScreen(
                onCaptured = { uri ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("avatarUri", uri.toString())
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoutes.COMMENT) {
            CommentScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.COLLECTION) {
            CollectionScreen(
                onBack = { navController.popBackStack() },
                onArticleClick = onArticleClick
            )
        }

        composable(NavRoutes.ABOUT_ME) {
            AboutMeScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
