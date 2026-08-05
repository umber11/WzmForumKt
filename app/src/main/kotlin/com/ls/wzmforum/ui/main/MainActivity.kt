package com.ls.wzmforum.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ls.librarybase.ui.theme.WzmForumTheme
import com.ls.librarybase.utils.StatusBarUtils
import com.ls.wzmforum.ui.navigation.AppNavigation

/**
 * 主页面Activity
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setImmerseStatusBar(this)
        setContent {
            WzmForumTheme {
                AppNavigation(
                    onExit = { finish() }
                )
            }
        }
    }
}
