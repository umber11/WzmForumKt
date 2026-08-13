package com.ls.products.ui.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ls.products.R

/**
 * 排序方式
 */
enum class SortType { DEFAULT, VIEWS, PUBLISHTIME }

/**
 * 产品筛选弹窗
 */
@Composable
fun ProductsFilterDrawerDialog(
    selectedSort: SortType = SortType.DEFAULT,
    onConfirm: (SortType) -> Unit,
    onDismiss: () -> Unit
) {
    var selected by remember { mutableStateOf(selectedSort) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp)
                .background(Color(0x80000000))
                .clickable(onClick = onDismiss)
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth(0.8f)
                .fillMaxHeight()
                .padding(top = 50.dp)
                .background(Color.White)
        ) {
            Text(
                text = "全部",
                fontSize = 16.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, top = 24.dp)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(1.dp)
                    .background(Color(0xFFE8E8E8))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp)
            ) {
                DrawerFilterChip(
                    label = "默认",
                    selected = selected == SortType.DEFAULT,
                    iconUp = true,
                    onClick = { selected = SortType.DEFAULT },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp)
                )
                DrawerFilterChip(
                    label = "浏览次数",
                    selected = selected == SortType.VIEWS,
                    onClick = { selected = SortType.VIEWS },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp)
                )
                DrawerFilterChip(
                    label = "发布时间",
                    selected = selected == SortType.PUBLISHTIME,
                    onClick = { selected = SortType.PUBLISHTIME },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE8E8E8))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(50.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .background(Color.White, RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(6.dp))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "取消", fontSize = 15.sp, color = Color(0xFF333333))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .background(Color(0xFF4A90D9), RoundedCornerShape(6.dp))
                        .clickable { onConfirm(selected) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "确定", fontSize = 15.sp, color = Color.White)
                }
            }
        }
        }
    }
}

@Composable
private fun DrawerFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconUp: Boolean = false
) {
    Row(
        modifier = modifier
            .height(36.dp)
            .background(
                if (selected) Color(0xFF4A90D9) else Color(0xFFF5F5F5),
                RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (selected) Color.White else Color(0xFF666666)
        )
        Image(
            painter = painterResource(if (iconUp) R.mipmap.icon_up_arrow else R.mipmap.icon_down_arrow),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(14.dp)
        )
    }
}
