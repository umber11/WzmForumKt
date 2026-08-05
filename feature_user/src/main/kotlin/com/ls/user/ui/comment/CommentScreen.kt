package com.ls.user.ui.comment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ls.librarybase.R
import com.ls.librarybase.component.CommonImage
import com.ls.librarybase.component.PageWrapper
import com.ls.librarybase.component.StatusView
import com.ls.librarybase.component.ViewState
import com.ls.librarybase.component.ViewModelEffects
import com.ls.librarybase.manager.UserManager
import com.ls.user.bean.ResComments

/**
 * 评论页展示
 */
@Composable
fun CommentScreen(
    vm: CommentViewModel = viewModel(),
    onBack: () -> Unit
) {
    val comments by vm.mCommentList.collectAsStateWithLifecycle()
    val loading = ViewModelEffects(vm = vm)
    var deleteTarget by remember { mutableStateOf<ResComments.ListBean?>(null) }

    LaunchedEffect(Unit) {
        vm.loadCommentList()
    }

    PageWrapper(title = "我发表的评论", showBack = true, onBack = onBack, loading = loading) {
        val list = comments
        when {
            list == null -> Unit
            list.isEmpty() -> StatusView(state = ViewState.Empty, emptyText = "暂无评论")
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().background(Color.White)
            ) {
                items(list, key = { it.id }) { comment ->
                    CommentItem(
                        comment = comment,
                        onClick = {},
                        onLongClick = {
                            val currentUserId = if (UserManager.getInstance().isLogin()) {
                                UserManager.getInstance().userInfo.user?.id ?: ""
                            } else {
                                ""
                            }
                            if (currentUserId.isNotEmpty() && currentUserId == comment.user_id.toString()) {
                                deleteTarget = comment
                            }
                        }
                    )
                }
            }
        }
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("提示") },
            text = { Text("确定删除这条评论吗？") },
            confirmButton = {
                TextButton(onClick = {
                    deleteTarget = null
                    val index = comments?.indexOf(target) ?: -1
                    if (index >= 0) vm.deleteComment(target.id, index)
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("取消") }
            }
        )
    }
}

@Composable
private fun CommentItem(
    comment: ResComments.ListBean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Row {
            CommonImage(
                url = comment.archives?.image,
                modifier = Modifier.size(width = 80.dp, height = 60.dp),
                placeholder = painterResource(R.mipmap.bg_default),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                val title = comment.archives?.title
                if (title != null) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Text(
                    text = comment.content ?: "",
                    fontSize = 14.sp,
                    color = Color(0xFF555555),
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )
            }
        }
        Text(
            text = comment.create_date ?: "",
            fontSize = 12.sp,
            color = Color(0xFF999999),
            modifier = Modifier.padding(start = 90.dp, top = 6.dp)
        )
    }
}