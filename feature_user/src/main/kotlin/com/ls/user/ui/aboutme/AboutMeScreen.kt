package com.ls.user.ui.aboutme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ls.librarybase.component.PageWrapper
import com.ls.user.R

/**
 * 关于我们页展示
 */
@Composable
fun AboutMeScreen(
    vm: AboutMeViewModel = viewModel(),
    onBack: () -> Unit
) {
    val versionLabel by vm.mVersionLabel.collectAsStateWithLifecycle()

    PageWrapper(title = "关于我们", showBack = true, onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.mipmap.logo_bg),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
                Image(
                    painter = painterResource(R.mipmap.icon_write_logo),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            }
            Text(
                text = versionLabel,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 40.dp)
            )
        }
    }
}