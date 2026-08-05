package com.ls.user.ui.agreement

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ls.librarybase.component.PageWrapper
import com.ls.user.config.UserConfig

/**
 * 静态协议展示页面(用户协议/隐私政策/个人信息清单)
 */
private const val TEXT_AGREEMENT = "账号使用：您需用手机号注册，不得转让或共享账号，并对账号下所有行为负责。\n\n" +
        "发言规则：禁止发布违法、侵权、色情、辱骂或虚假信息，否则我们有权删帖或封号。\n\n" +
        "责任声明：因系统维护、网络故障或用户间纠纷导致的服务中断或损失，平台不承担责任。\n\n" +
        "协议变更：我们修改协议时会通过App公告通知，继续使用即视为同意。"

private const val TEXT_PRIVACY = "收集信息：仅收集手机号、昵称、发布内容及设备型号，用于登录、展示和保障安全。\n\n" +
        "使用与共享：信息仅用于App功能优化，不出售给第三方；法律法规要求时可能披露。\n\n" +
        "用户权利：您可随时修改个人资料或通过\"设置-注销账号\"删除全部数据。\n\n" +
        "安全与更新：我们采取标准加密措施保护数据，政策重大变更时通过弹窗告知。"

private const val TEXT_USER_INFO = "（暂无内容）"

@Composable
fun AgreementScreen(mType: Int, onBack: () -> Unit) {
    val (title, content) = when (mType) {
        UserConfig.AgreementType.VALUE_AGREEMENT -> "用户协议" to TEXT_AGREEMENT
        UserConfig.AgreementType.VALUE_SIMPLE_PRIVATE,
        UserConfig.AgreementType.VALUE_PRIVATE -> "隐私政策" to TEXT_PRIVACY
        else -> "个人信息收集清单" to TEXT_USER_INFO
    }

    PageWrapper(title = title, showBack = true, onBack = onBack) {
        Text(
            text = content,
            color = Color(0xFF333333),
            fontSize = 14.sp,
            lineHeight = 22.sp,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        )
    }
}