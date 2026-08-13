package com.ls.librarybase.ui.article

import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ls.librarybase.R
import com.ls.librarybase.bean.ResArticleDetail
import com.ls.librarybase.component.CommonImage
import com.ls.librarybase.component.ViewModelEffects
import com.ls.librarybase.manager.UserManager
import com.ls.librarybase.ui.dialog.YesOrNoDialog
import kotlinx.coroutines.launch

/**
 * 文章详情页：展示正文、点赞/收藏、评论列表与发表评论。
 */
@Composable
fun ArticleDetailScreen(
    vm: ArticleDetailViewModel,
    articleId: String,
    onBack: () -> Unit
) {
    val detail by vm.mArticleDetail.collectAsStateWithLifecycle()
    val comments by vm.mCommentList.collectAsStateWithLifecycle()
    val isLoading = ViewModelEffects(vm)
    val info = detail?.archivesInfo

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val showFab by remember { derivedStateOf { scrollState.value > 600 } }
    var commentInput by remember { mutableStateOf("") }
    var deleteTarget by remember {
        mutableStateOf<ResArticleDetail.CommentListBean?>(null)
    }

    val context = LocalContext.current

    LaunchedEffect(articleId) {
        vm.loadArticle(articleId)
    }

    fun submitComment() {
        val content = commentInput.trim()
        if (content.isEmpty()) {
            android.widget.Toast.makeText(context, "请输入评论内容", android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        vm.postComment(content)
        commentInput = ""
    }

    fun onCommentLongClick(comment: ResArticleDetail.CommentListBean) {
        val currentUserId = UserManager.getInstance().userInfo.user?.id?.toIntOrNull()
        if (currentUserId == null || currentUserId != comment.user_id) return
        deleteTarget = comment
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(Color.White)
        ) {
            DetailToolbar(onBack = onBack)

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(Color(0xFFEEEEEE))
            )

            if (info != null) {
                Text(
                    text = info.title ?: "",
                    fontSize = 22.sp,
                    color = Color(0xFF222222),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    lineHeight = 30.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                )
                Text(
                    text = info.create_date ?: "",
                    fontSize = 12.sp,
                    color = Color(0xFF999999),
                    modifier = Modifier.padding(start = 16.dp, top = 10.dp)
                )
                AuthorInfoRow(info = info, onToggleLike = { vm.toggleLike() })

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .padding(top = 8.dp)
                        .background(Color(0xFFF5F5F5))
                )
                CommonImage(
                    url = info.image,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(0xFFF5F5F5))
                )
                AndroidView(
                    factory = { ctx ->
                        TextView(ctx).apply {
                            setTextSize(16f)
                            setTextColor(0xFF333333.toInt())
                            setLineSpacing(6f, 1f)
                        }
                    },
                    update = { tv -> tv.text = htmlToSpanned(info.content) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionButton(
                        selectedIcon = R.mipmap.icon_selected_heart,
                        unselectedIcon = R.mipmap.icon_unselected_heart,
                        selected = info.iscollection == 1,
                        label = "收藏",
                        onClick = { vm.toggleCollection() }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    ActionButton(
                        selectedIcon = R.mipmap.icon_selected_dianzan,
                        unselectedIcon = R.mipmap.icon_unselected_dianzan,
                        selected = info.islike == 1,
                        label = "点赞",
                        extraText = "${info.likes}",
                        onClick = { vm.toggleLike() }
                    )
                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(Color(0xFFEEEEEE))
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "发表评论",
                    fontSize = 16.sp,
                    color = Color(0xFF333333),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .height(48.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = commentInput,
                        onValueChange = { commentInput = it },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 14.sp, color = Color(0xFF333333)),
                        cursorBrush = SolidColor(Color(0xFF333333)),
                        decorationBox = { innerTextField ->
                            if (commentInput.isEmpty()) {
                                Text(text = "请输入评论内容", fontSize = 14.sp, color = Color(0xFF999999))
                            }
                            innerTextField()
                        }
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp)
                        .height(36.dp)
                        .background(Color(0xFF2196F3), RoundedCornerShape(20.dp))
                        .clickable { submitComment() }
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "立即评论", fontSize = 14.sp, color = Color.White)
                }
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(Color(0xFFEEEEEE))
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "评论列表",
                    fontSize = 16.sp,
                    color = Color(0xFF333333),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                val list = comments
                if (list != null) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        list.forEach { comment ->
                            CommentItem(
                                comment = comment,
                                onLongClick = { onCommentLongClick(comment) }
                            )
                        }
                    }
                }
            }
        }

        if (showFab) {
            Image(
                painter = painterResource(R.mipmap.icon_up),
                contentDescription = "回到顶部",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 24.dp)
                    .size(48.dp)
                    .clickable { scope.launch { scrollState.animateScrollTo(0) } }
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        val target = deleteTarget
        if (target != null) {
            YesOrNoDialog(
                title = "提示",
                content = "确定删除这条评论吗？",
                onConfirm = {
                    vm.deleteComment(target.id)
                    deleteTarget = null
                },
                onCancel = { deleteTarget = null }
            )
        }
    }
}

@Composable
private fun DetailToolbar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(48.dp)
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(R.mipmap.icon_back),
            contentDescription = "返回",
            modifier = Modifier
                .size(48.dp)
                .padding(12.dp)
                .clickable(onClick = onBack)
        )
        Text(
            text = "详情",
            fontSize = 18.sp,
            color = Color(0xFF333333),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun AuthorInfoRow(
    info: ResArticleDetail.ArchivesInfoBean,
    onToggleLike: () -> Unit
) {
    val liked = info.islike == 1
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.mipmap.icon_default_avatar),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = info.author ?: "",
            fontSize = 13.sp,
            color = Color(0xFF333333),
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.clickable(onClick = onToggleLike),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(if (liked) R.mipmap.icon_liked else R.mipmap.icon_unliked),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "${info.likes}",
                fontSize = 12.sp,
                color = Color(0xFF999999),
                modifier = Modifier.padding(start = 3.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.mipmap.icon_pinlun),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "${info.comments}",
                fontSize = 12.sp,
                color = Color(0xFF999999),
                modifier = Modifier.padding(start = 3.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.mipmap.icon_yanjin),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "${info.views}",
                fontSize = 12.sp,
                color = Color(0xFF999999),
                modifier = Modifier.padding(start = 3.dp)
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.mipmap.icon_share),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "分享",
                fontSize = 12.sp,
                color = Color(0xFF999999),
                modifier = Modifier.padding(start = 3.dp)
            )
        }
    }
}

@Composable
private fun ActionButton(
    selectedIcon: Int,
    unselectedIcon: Int,
    selected: Boolean,
    label: String,
    extraText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(44.dp)
            .background(Color(0xFF2196F3), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(if (selected) selectedIcon else unselectedIcon),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.White,
            modifier = Modifier.padding(start = 5.dp)
        )
        if (extraText != null) {
            Text(
                text = extraText,
                fontSize = 13.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 3.dp)
            )
        }
    }
}

@Composable
private fun CommentItem(
    comment: ResArticleDetail.CommentListBean,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = {}, onLongClick = onLongClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFEEEEEE))
        ) {
            CommonImage(
                url = comment.user?.avatar,
                modifier = Modifier.size(36.dp),
                placeholder = painterResource(R.mipmap.icon_default_avatar),
                error = painterResource(R.mipmap.icon_default_avatar)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.user?.nickname ?: "",
                    fontSize = 13.sp,
                    color = Color(0xFF333333)
                )
                Text(
                    text = comment.create_date ?: "",
                    fontSize = 11.sp,
                    color = Color(0xFF999999),
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            Text(
                text = comment.content ?: "",
                fontSize = 14.sp,
                color = Color(0xFF444444),
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

private fun htmlToSpanned(html: String?): Spanned {
    return if (html.isNullOrEmpty()) {
        SpannableString("")
    } else {
        Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
    }
}
