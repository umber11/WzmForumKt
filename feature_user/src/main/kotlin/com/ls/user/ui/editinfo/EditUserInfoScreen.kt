package com.ls.user.ui.editinfo

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ls.librarybase.component.AuthTextField
import com.ls.librarybase.component.CommonImage
import com.ls.librarybase.component.ViewModelEffects
import com.ls.user.R

/**
 * 编辑个人信息页展示
 */
@Composable
fun EditUserInfoScreen(
    vm: EditUserViewModel = viewModel(),
    pickedAvatarUri: Uri?,
    onCameraClick: () -> Unit,
    onFinish: () -> Unit
) {
    val nickName by vm.mNickName.collectAsStateWithLifecycle()
    val bio by vm.mBio.collectAsStateWithLifecycle()
    val avatarUrl by vm.mAvatarUrl.collectAsStateWithLifecycle()

    var localAvatarUri by remember { mutableStateOf<Uri?>(pickedAvatarUri) }
    var showAvatarDialog by remember { mutableStateOf(false) }
    var showExitConfirm by remember { mutableStateOf(false) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            localAvatarUri = uri
            vm.uploadAvatar(uri)
        }
    }

    val requestExit: () -> Unit = {
        if (vm.isChange()) showExitConfirm = true else onFinish()
    }

    val loading = ViewModelEffects(vm = vm)

    BackHandler(onBack = requestExit)

    LaunchedEffect(pickedAvatarUri) {
        pickedAvatarUri?.let { vm.uploadAvatar(it) }
    }

    LaunchedEffect(Unit) {
        vm.mAction.collect { action ->
            if (action == EditUserViewModel.EditUserAction.FINISH) onFinish()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 14.dp, top = 6.dp, end = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp).clickable { requestExit() }
                )
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(text = "编辑个人资料", fontSize = 20.sp, color = Color(0xFF333333))
                }
                Text(
                    text = "保存",
                    fontSize = 15.sp,
                    color = Color.Black,
                    modifier = Modifier.clickable { vm.onSaveUserInfo() }
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(R.mipmap.bg_user_center),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 9.dp)
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier.align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CommonImage(
                        url = localAvatarUri?.toString() ?: avatarUrl,
                        modifier = Modifier
                            .padding(top = 101.dp)
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.mipmap.icon_default_avatar),
                        error = painterResource(R.mipmap.icon_default_avatar)
                    )
                    Text(
                        text = "点击更换头像",
                        fontSize = 12.sp,
                        color = Color(0xFF9C9C9C),
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .clickable { showAvatarDialog = true }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 52.dp, top = 44.dp, end = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "名字", fontSize = 12.sp, color = Color(0xFF9C9C9C))
                AuthTextField(
                    value = nickName ?: "",
                    onValueChange = { vm.setNickName(it) },
                    hint = "输入昵称",
                    maxLength = 16,
                    modifier = Modifier.padding(start = 36.dp).weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 52.dp, top = 23.dp, end = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "简介", fontSize = 12.sp, color = Color(0xFF9C9C9C))
                AuthTextField(
                    value = bio ?: "",
                    onValueChange = { vm.setBio(it) },
                    hint = "输入简介",
                    maxLength = 16,
                    modifier = Modifier.padding(start = 36.dp).weight(1f)
                )
            }
        }

        if (showAvatarDialog) {
            AlertDialog(
                onDismissRequest = { showAvatarDialog = false },
                title = { Text("选择图片来源") },
                text = {
                    Column {
                        TextButton(
                            onClick = { showAvatarDialog = false; onCameraClick() },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("拍照") }
                        TextButton(
                            onClick = { showAvatarDialog = false; pickImageLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("相册") }
                    }
                },
                confirmButton = {},
                dismissButton = {}
            )
        }

        if (showExitConfirm) {
            AlertDialog(
                onDismissRequest = { showExitConfirm = false },
                title = { Text("提示") },
                text = { Text("是否保存更新?") },
                confirmButton = {
                    TextButton(onClick = {
                        showExitConfirm = false
                        vm.onSaveUserInfo()
                    }) { Text("保存") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showExitConfirm = false
                        onFinish()
                    }) { Text("取消") }
                }
            )
        }

        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
