package com.ls.user.ui.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ls.librarybase.component.CommonImage
import com.ls.user.R
import com.ls.user.ui.user.UserViewModel.UserCenterAction

/**
 * 用户页面展示
 */
@Composable
fun UserCenterScreen(
    vm: UserViewModel,
    onNavigate: (UserCenterAction) -> Unit
) {
    val avatar by vm.avatar.collectAsStateWithLifecycle()
    val nickName by vm.nickName.collectAsStateWithLifecycle()
    val bio by vm.bio.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(vm) {
        vm.action.collect { action ->
            if (action == UserCenterAction.SHOW_LOGOUT_DIALOG) {
                showLogoutDialog = true
            } else {
                onNavigate(action)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            Image(
                painter = painterResource(R.mipmap.user_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "个人中心",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 10.dp)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CommonImage(
                    url = avatar.ifEmpty { null },
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .clickable { vm.onEditUserInfoClick() },
                    placeholder = painterResource(R.mipmap.icon_default_avatar),
                    error = painterResource(R.mipmap.icon_default_avatar)
                )
                Text(
                    text = nickName,
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .clickable { vm.onEditUserInfoClick() }
                )
                Text(
                    text = bio,
                    fontSize = 13.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .clickable { vm.onEditUserInfoClick() }
                )
            }
        }

        MenuItemRow(R.mipmap.icon_user_pinlun, "我发表的评论") { vm.onCommentClick() }
        MenuDivider()
        MenuItemRow(R.mipmap.icon_user_shoucang, "我的收藏") { vm.onCollectionClick() }
        MenuDivider()
        MenuItemRow(R.mipmap.icon_user_xinxi, "个人信息") { vm.onUserInfoMenuClick() }
        MenuDivider()
        MenuItemRow(R.mipmap.icon_user_about, "关于我们") { vm.onSettingsClick() }
        MenuDivider()
        MenuItemRow(R.mipmap.icon_user_tuichu, "退出登录") { vm.onLogoutClick() }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("提示") },
            text = { Text("是否退出当前账号？") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    vm.logout()
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("取消") }
            }
        )
    }
}

@Composable
private fun MenuItemRow(icon: Int, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF444444),
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
        Image(
            painter = painterResource(R.mipmap.gray_arrow),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun MenuDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0x1A000000))
    )
}