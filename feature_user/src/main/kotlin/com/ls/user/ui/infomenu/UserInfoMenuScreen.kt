package com.ls.user.ui.infomenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ls.librarybase.component.CommonImage
import com.ls.librarybase.component.PageWrapper
import com.ls.librarybase.ui.theme.TextPrimary
import com.ls.librarybase.ui.theme.TextSecondary
import com.ls.librarybase.ui.theme.White
import com.ls.user.R

/**
 * 个人信息页展示
 */
@Composable
fun UserInfoMenuScreen(
    vm: UserInfoMenuViewModel = viewModel(),
    onBack: () -> Unit
) {
    val avatarUrl by vm.avatarUrl.collectAsStateWithLifecycle(initialValue = "")
    val userName by vm.userName.collectAsStateWithLifecycle(initialValue = "")
    val nickName by vm.nickName.collectAsStateWithLifecycle(initialValue = "")
    val bio by vm.bio.collectAsStateWithLifecycle(initialValue = "")
    val status by vm.status.collectAsStateWithLifecycle(initialValue = "")
    val follow by vm.follow.collectAsStateWithLifecycle(initialValue = "0")
    val fans by vm.fans.collectAsStateWithLifecycle(initialValue = "0")
    val medal by vm.medal.collectAsStateWithLifecycle(initialValue = "0")

    LaunchedEffect(vm) {
        vm.loadUserInfo()
    }

    PageWrapper(title = "个人信息", showBack = true, onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                CommonImage(
                    url = avatarUrl.ifEmpty { null },
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.mipmap.icon_default_avatar),
                    error = painterResource(R.mipmap.icon_default_avatar)
                )
            }

            InfoRow("用户名", userName)
            InfoRow("昵称", nickName)
            InfoRow("签名", bio)
            InfoRow("状态", status)
            InfoRow("关注", follow)
            InfoRow("粉丝", fans)
            InfoRow("奖牌", medal)
        }
    }
}
//自定义私有组件，避免重复代码
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 25.dp, top = 10.dp)
    ) {
        Text(text = label, color = TextPrimary, fontSize = 18.sp)
        Text(
            text = value.ifEmpty { label },
            color = TextSecondary,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 30.dp)
        )
    }
}