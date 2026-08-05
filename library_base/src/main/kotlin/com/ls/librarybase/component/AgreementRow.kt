package com.ls.librarybase.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 协议勾选组件
 */
@Composable
fun AgreementRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onAgreementClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val annotated = buildAnnotatedString {
        append("请阅读并同意《用户协议》和《隐私政策》")
        addStyle(SpanStyle(fontWeight = FontWeight.Bold), 6, 12)
        addStyle(SpanStyle(fontWeight = FontWeight.Bold), 14, 19)
        addStringAnnotation(tag = "agreement", annotation = "agreement", start = 6, end = 12)
        addStringAnnotation(tag = "privacy", annotation = "privacy", start = 14, end = 19)
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        ClickableText(
            text = annotated,
            style = TextStyle(fontSize = 10.sp, color = Color.Black),
            onClick = { offset ->
                annotated.getStringAnnotations("agreement", offset, offset).firstOrNull()?.let {
                    onAgreementClick()
                }
                annotated.getStringAnnotations("privacy", offset, offset).firstOrNull()?.let {
                    onPrivacyClick()
                }
            }
        )
    }
}