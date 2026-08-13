package com.ls.user.ui.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ls.librarybase.component.AgreementRow
import com.ls.librarybase.component.AuthTextField
import com.ls.librarybase.component.ViewModelEffects
import com.ls.librarybase.ui.theme.Primary
import com.ls.user.R

/**
 * 注册页面展示，提供用户名、密码、确认密码输入与协议勾选。
 */
@Composable
fun RegisterScreen(
    vm: RegisterViewModel = viewModel(),
    onBack: () -> Unit,
    onAgreementClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val username by vm.username.collectAsStateWithLifecycle(initialValue = "")
    val password by vm.password.collectAsStateWithLifecycle(initialValue = "")
    val confirmPwd by vm.confirmPwd.collectAsStateWithLifecycle(initialValue = "")
    val agreed by vm.checkAgreement.collectAsStateWithLifecycle(initialValue = false)
    val enableRegister by vm.isEnableRegister.collectAsStateWithLifecycle(initialValue = false)
    val registerSuccess by vm.registerSuccess.collectAsStateWithLifecycle(initialValue = false)

    val loading = ViewModelEffects(vm = vm)

    LaunchedEffect(registerSuccess) {
        if (registerSuccess) onRegisterSuccess()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFE5E5E5))
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // 顶部背景图
                Box(modifier = Modifier.fillMaxWidth().height(340.dp)) {
                Image(
                    painter = painterResource(R.mipmap.bg_login_top),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(start = 14.dp, top = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp).clickable { onBack() }
                    )
                }
            }

                // 白色注册卡片（居中于顶部背景图）
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 17.5.dp).padding(top = 170.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(bottom = 20.dp)
                    ) {
                    Text(
                        text = "账号注册",
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 36.5.dp, top = 27.5.dp)
                    )

                    Column(modifier = Modifier.padding(start = 36.5.dp, end = 35.5.dp)) {
                        FieldGroup(label = "用户名") {
                            AuthTextField(
                                value = username,
                                onValueChange = {
                                    vm.setUsername(it)
                                    vm.updateRegisterBtnStatus()
                                },
                                hint = "请输入用户名",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        FieldGroup(label = "密码") {
                            AuthTextField(
                                value = password,
                                onValueChange = {
                                    vm.setPassword(it)
                                    vm.updateRegisterBtnStatus()
                                },
                                hint = "请输入密码",
                                passwordToggle = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        FieldGroup(label = "确认密码") {
                            AuthTextField(
                                value = confirmPwd,
                                onValueChange = {
                                    vm.setConfirmPwd(it)
                                    vm.updateRegisterBtnStatus()
                                },
                                hint = "请再次输入密码",
                                passwordToggle = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    AgreementRow(
                        checked = agreed,
                        onCheckedChange = { vm.setCheckAgreement(it) },
                        onAgreementClick = onAgreementClick,
                        onPrivacyClick = onPrivacyClick,
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
                    )

                    Button(
                        onClick = { vm.register() },
                        enabled = enableRegister,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            disabledContainerColor = Primary.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 17.5.dp, end = 17.5.dp, top = 17.dp)
                            .height(55.dp)
                    ) {
                        Text(text = "注册", fontSize = 16.sp, color = Color.White)
                    }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
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
//私有组件FieldGroup（标签文字+内存槽位）
@Composable
private fun FieldGroup(
    label: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 24.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}