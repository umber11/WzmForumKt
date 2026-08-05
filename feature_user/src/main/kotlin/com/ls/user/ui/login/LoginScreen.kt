package com.ls.user.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ls.librarybase.component.AgreementRow
import com.ls.librarybase.component.AuthTextField
import com.ls.librarybase.component.CountDownButton
import com.ls.librarybase.component.ViewModelEffects
import com.ls.librarybase.ui.theme.Primary
import com.ls.user.R
import com.ls.user.config.UserConfig

/**
 * 登录页面展示
 */
@Composable
fun LoginScreen(
    vm: LoginViewModel = viewModel(),
    onBack: () -> Unit,
    onRegister: () -> Unit,
    onOpenAgreement: (Int) -> Unit,
    onLoginSuccess: () -> Unit
) {
    val phone by vm.userMobile.collectAsStateWithLifecycle(initialValue = "")
    val code by vm.code.collectAsStateWithLifecycle(initialValue = "")
    val account by vm.account.collectAsStateWithLifecycle(initialValue = "")
    val password by vm.password.collectAsStateWithLifecycle(initialValue = "")
    val mode by vm.loginMode.collectAsStateWithLifecycle(initialValue = LoginViewModel.MODE_PHONE_CODE)
    val sendCodeText by vm.getVerticalCodeText.collectAsStateWithLifecycle(initialValue = "获取验证码")
    val sendCodeEnabled by vm.isEnableSendCode.collectAsStateWithLifecycle(initialValue = true)
    val enableLogin by vm.isEnableLogin.collectAsStateWithLifecycle(initialValue = false)
    val agreed by vm.checkAgreement.collectAsStateWithLifecycle(initialValue = false)
    val loginSuccess by vm.loginSuccess.collectAsStateWithLifecycle(initialValue = false)

    val loading = ViewModelEffects(vm = vm)

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) onLoginSuccess()
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
                //顶部导航栏
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(start = 14.dp, end = 14.dp, top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp).clickable { onBack() }
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Image(
                            painter = painterResource(R.mipmap.icon_qualifications),
                            contentDescription = "资质",
                            modifier = Modifier.size(20.dp).clickable {
                                onOpenAgreement(UserConfig.AgreementType.VALUE_AGREEMENT)
                            }
                        )
                        Image(
                            painter = painterResource(R.mipmap.icon_settings),
                            contentDescription = "设置",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                //中间logo
                Image(
                    painter = painterResource(R.mipmap.icon_write_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 127.dp)
                        .size(width = 140.dp, height = 25.dp)
                )
            }

                // 白色登录卡片（居中于顶部背景图）
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 17.5.dp).padding(top = 170.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(vertical = 16.dp)
                    ) {
                    //登录方式Tab
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        LoginTab("验证码登录", selected = mode == LoginViewModel.MODE_PHONE_CODE) { vm.setLoginMode(0) }
                        LoginTab("密码登录", selected = mode == 1, modifier = Modifier.padding(start = 20.dp)) {
                            vm.setLoginMode(1)
                        }
                    }
                    //"用户账号"分隔符
                    Column(modifier = Modifier.padding(start = 36.5.dp, top = 27.5.dp)) {
                        Text(text = "用户账号", fontSize = 13.sp, color = Color.Black)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp)
                                .height(1.dp)
                                .background(Color(0xFF9C9C9C))
                        )
                    }
                    //验证码登录模式
                    if (mode == LoginViewModel.MODE_PHONE_CODE) {
                        //手机号输入框
                        AuthTextField(
                            value = phone,
                            onValueChange = {
                                vm.setUserMobile(it)
                                vm.updateEnableLoginBtnStatus()
                            },
                            hint = "输入手机号登录/注册",
                            keyboardType = KeyboardType.Phone,
                            maxLength = 11,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 36.5.dp, end = 35.5.dp, top = 24.dp)
                        )
                        //验证码输入
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 36.5.dp, end = 35.5.dp, top = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AuthTextField(
                                value = code,
                                onValueChange = {
                                    vm.setCode(it)
                                    vm.updateEnableLoginBtnStatus()
                                },
                                hint = "输入验证码",
                                keyboardType = KeyboardType.Number,
                                maxLength = 4,
                                modifier = Modifier.weight(1f)
                            )
                            //发送按钮
                            CountDownButton(
                                text = sendCodeText,
                                enabled = sendCodeEnabled,
                                onClick = { vm.sendCode() },
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        //注册提示
                        Text(
                            text = "新手机号将自动注册",
                            color = Color(0xFFBBBBBB),
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
                        )
                    } else {
                        //密码登录模式
                        AuthTextField(
                            value = account,
                            onValueChange = {
                                vm.setAccount(it)
                                vm.updateEnableLoginBtnStatus()
                            },
                            hint = "输入用户名",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 36.5.dp, end = 35.5.dp, top = 24.dp)
                        )

                        AuthTextField(
                            value = password,
                            onValueChange = {
                                vm.setPassword(it)
                                vm.updateEnableLoginBtnStatus()
                            },
                            hint = "输入密码",
                            passwordToggle = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 36.5.dp, end = 35.5.dp, top = 16.dp)
                        )

                        Text(
                            text = "没有账号？去注册",
                            color = Color(0xFF666666),
                            fontSize = 11.sp,
                            modifier = Modifier
                                .padding(start = 36.5.dp, top = 12.dp)
                                .clickable { onRegister() }
                        )
                    }
                    //协议勾选
                    AgreementRow(
                        checked = agreed,
                        onCheckedChange = { vm.setCheckAgreement(it) },
                        onAgreementClick = { onOpenAgreement(UserConfig.AgreementType.VALUE_AGREEMENT) },
                        onPrivacyClick = { onOpenAgreement(UserConfig.AgreementType.VALUE_PRIVATE) },
                        modifier = Modifier.fillMaxWidth().padding(top = 18.dp)
                    )
                    //登录按钮
                    Button(
                        onClick = { vm.login() },
                        enabled = enableLogin,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            disabledContainerColor = Primary.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 17.5.dp, end = 17.5.dp, top = 17.dp)
                            .height(55.dp)
                    ) {
                        Text(text = "登录", fontSize = 16.sp, color = Color.White)
                    }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        //加载遮罩
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
//私有组件登录方式Tab
@Composable
private fun LoginTab(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = if (selected) Color.Black else Color(0xFFBBBBBB),
        modifier = modifier.clickable(onClick = onClick)
    )
}
