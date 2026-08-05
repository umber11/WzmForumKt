package com.ls.news.ui.news

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ls.librarybase.R as LibR
import com.ls.librarybase.bean.ResArticleList
import com.ls.librarybase.component.CommonImage
import com.ls.librarybase.utils.LikeTracker
import com.ls.librarybase.utils.ViewTracker
import com.ls.news.R

/**
 * 资讯页展示
 */
@Composable
fun NewsScreen(
    vm: NewsViewModel,
    onArticleClick: (ResArticleList.ListBean) -> Unit,
    onSearchClick: () -> Unit
) {
    val categories by vm.mCategories.collectAsStateWithLifecycle()
    val articles by vm.mArticles.collectAsStateWithLifecycle()
    val cats = categories
    val arts = articles
    var selectedIndex by remember { mutableStateOf(0) }
    var showFilter by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val total = articles?.size ?: 0
            if (total < 10) false
            else (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) >= total - 2
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) vm.refreshFromTrackers()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(vm) { vm.refresh() }

    LaunchedEffect(categories) {
        if (!cats.isNullOrEmpty()) {
            selectedIndex = 0
            vm.onCategorySelected(cats!![0].id.toString())
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !vm.isLoadMore()) vm.loadArticles(true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "资讯",
                fontSize = 20.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NewsSearchBar(
                onClick = onSearchClick,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                painter = painterResource(R.mipmap.icon_zixun),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { showFilter = true }
            )
        }

        if (!cats.isNullOrEmpty()) {
            LazyRow(modifier = Modifier.padding(top = 8.dp)) {
                itemsIndexed(cats!!) { index, cat ->
                    val selected = index == selectedIndex
                    Text(
                        text = cat.name ?: "",
                        fontSize = 16.sp,
                        color = if (selected) Color(0xFF2196F3) else Color(0xFF999999),
                        modifier = Modifier
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .clickable {
                                if (selectedIndex != index) {
                                    selectedIndex = index
                                    vm.onCategorySelected(cat.id.toString())
                                }
                            }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE8E8E8))
        )

        when {
            arts == null -> Box(modifier = Modifier.weight(1f))
            arts.isEmpty() -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "暂无内容", fontSize = 14.sp, color = Color(0xFF999999))
            }
            else -> LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f)
            ) {
                items(arts!!, key = { it.id }) { item ->
                    NewsArticleCard(item = item, onClick = { onArticleClick(item) })
                }
            }
        }

        if (showFilter) {
            FilterDrawerDialog(
                onConfirm = { type ->
                    vm.applySort(type)
                    showFilter = false
                },
                onDismiss = { showFilter = false }
            )
        }
    }
}

@Composable
private fun NewsSearchBar(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(36.dp)
            .background(Color(0xFFF2F2F2), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.mipmap.icon_search),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 10.dp)
                .size(16.dp)
        )
        Text(
            text = "输入关键词搜索",
            fontSize = 12.sp,
            color = Color(0xFF999999),
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

@Composable
private fun NewsArticleCard(item: ResArticleList.ListBean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(14.dp)
            .clickable(onClick = onClick)
    ) {
        CommonImage(url = item.image, modifier = Modifier.size(112.dp, 80.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 11.dp)
        ) {
            Text(
                text = item.title ?: "",
                fontSize = 14.sp,
                color = Color(0xFF333333),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.publishtime ?: "",
                fontSize = 11.sp,
                color = Color(0xFF999999),
                modifier = Modifier.padding(top = 12.dp)
            )
            NewsStatsRow(item = item, modifier = Modifier.padding(top = 10.dp))
        }
    }
}

@Composable
private fun NewsStatsRow(item: ResArticleList.ListBean, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        val liked = item.islike == 1 || LikeTracker.isLiked(item.id)
        Image(
            painter = painterResource(if (liked) LibR.mipmap.icon_liked else LibR.mipmap.icon_unliked),
            contentDescription = null,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = "${LikeTracker.getLikeCount(item.id) ?: item.likes}",
            fontSize = 11.sp,
            color = Color(0xFF999999),
            modifier = Modifier.padding(start = 5.dp)
        )
        Image(
            painter = painterResource(LibR.mipmap.icon_pinlun),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 10.dp)
                .size(12.dp)
        )
        Text(
            text = "${item.comments}",
            fontSize = 11.sp,
            color = Color(0xFF999999),
            modifier = Modifier.padding(start = 5.dp)
        )
        Image(
            painter = painterResource(LibR.mipmap.icon_yanjin),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 10.dp)
                .size(12.dp)
        )
        Text(
            text = "${ViewTracker.getViews(item.id) ?: item.views}",
            fontSize = 11.sp,
            color = Color(0xFF999999),
            modifier = Modifier.padding(start = 5.dp)
        )
    }
}
