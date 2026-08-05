package com.ls.librarybase.ui.search

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ls.librarybase.R
import com.ls.librarybase.bean.ResArticleList
import com.ls.librarybase.component.CommonImage
import com.ls.librarybase.utils.LikeTracker
import com.ls.librarybase.utils.ViewTracker

/**
 * 搜索页
 */
@Composable
fun SearchScreen(
    vm: SearchViewModel,
    initialKeyword: String = "",
    onBack: () -> Unit,
    onArticleClick: (ResArticleList.ListBean) -> Unit
) {
    var keyword by remember { mutableStateOf(initialKeyword) }
    val articles by vm.mArticles.collectAsStateWithLifecycle()
    val hasMore by vm.mHasMore.collectAsStateWithLifecycle()
    val toastMsg by vm.mToast.collectAsStateWithLifecycle(initialValue = null)
    var selectedFilter by remember { mutableStateOf(FilterType.DEFAULT) }
    var expandedFilter by remember { mutableStateOf(FilterType.NONE) }

    val listState = rememberLazyListState()
    val context = LocalContext.current

    LaunchedEffect(initialKeyword) {
        if (initialKeyword.isNotBlank()) {
            keyword = initialKeyword
            vm.performSearch(initialKeyword)
        }
    }

    LaunchedEffect(toastMsg) {
        toastMsg?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val total = articles.size
            if (total < 10) false
            else (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) >= total - 2
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) vm.loadMore()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .height(48.dp)
                    .clickable(onClick = onBack),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.mipmap.icon_back),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 14.dp)
                        .size(20.dp)
                )
                Text(
                    text = "返回",
                    fontSize = 16.sp,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            SearchTextField(
                value = keyword,
                onValueChange = { keyword = it },
                onSearch = { vm.performSearch(keyword) },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 8.dp)
            )
            Text(
                text = "搜索",
                fontSize = 14.sp,
                color = Color(0xFF333333),
                modifier = Modifier
                    .width(48.dp)
                    .clickable { vm.performSearch(keyword) }
                    .padding(vertical = 12.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                label = "默认",
                selected = selectedFilter == FilterType.DEFAULT,
                onClick = {
                    selectedFilter = FilterType.DEFAULT
                    expandedFilter = FilterType.NONE
                    vm.setSort("default", "desc")
                },
                modifier = Modifier.weight(1f)
            )
            Box(modifier = Modifier.weight(1f)) {
                FilterChip(
                    label = "浏览次数",
                    selected = selectedFilter == FilterType.VIEWS,
                    showArrow = true,
                    onClick = {
                        selectedFilter = FilterType.VIEWS
                        expandedFilter =
                            if (expandedFilter == FilterType.VIEWS) FilterType.NONE else FilterType.VIEWS
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = expandedFilter == FilterType.VIEWS,
                    onDismissRequest = { expandedFilter = FilterType.NONE }
                ) {
                    SortMenuItem("按最少排序") {
                        expandedFilter = FilterType.NONE
                        vm.setSort("views", "asc")
                    }
                    SortMenuItem("按最多排序") {
                        expandedFilter = FilterType.NONE
                        vm.setSort("views", "desc")
                    }
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                FilterChip(
                    label = "发布日期",
                    selected = selectedFilter == FilterType.PUBLISHTIME,
                    showArrow = true,
                    onClick = {
                        selectedFilter = FilterType.PUBLISHTIME
                        expandedFilter =
                            if (expandedFilter == FilterType.PUBLISHTIME) FilterType.NONE else FilterType.PUBLISHTIME
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = expandedFilter == FilterType.PUBLISHTIME,
                    onDismissRequest = { expandedFilter = FilterType.NONE }
                ) {
                    SortMenuItem("按最近排序") {
                        expandedFilter = FilterType.NONE
                        vm.setSort("publishtime", "desc")
                    }
                    SortMenuItem("按最久排序") {
                        expandedFilter = FilterType.NONE
                        vm.setSort("publishtime", "asc")
                    }
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
            articles.isNotEmpty() -> LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f)
            ) {
                items(articles, key = { it.id }) { item ->
                    SearchArticleItem(item = item, onClick = { onArticleClick(item) })
                }
                if (!hasMore) {
                    item(key = "footer") {
                        Text(
                            text = "--没有更多了--",
                            fontSize = 13.sp,
                            color = Color(0xFF999999),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            keyword.isNotBlank() -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "没有找到相关内容", fontSize = 14.sp, color = Color(0xFF999999))
            }
            else -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "搜索相关内容", fontSize = 14.sp, color = Color(0xFF999999))
            }
        }
    }
}

private enum class FilterType { NONE, DEFAULT, VIEWS, PUBLISHTIME }

@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .background(Color(0xFFF2F2F2), RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        Image(
            painter = painterResource(R.mipmap.icon_search),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 12.dp)
                .size(18.dp)
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(start = 40.dp, end = 12.dp),
            singleLine = true,
            textStyle = TextStyle(fontSize = 14.sp, color = Color(0xFF333333)),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            cursorBrush = SolidColor(Color(0xFF333333)),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(text = "输入关键词", fontSize = 14.sp, color = Color(0xFF999999))
                }
                innerTextField()
            }
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showArrow: Boolean = false
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = if (selected) Color(0xFF2196F3) else Color(0xFF333333)
        )
        if (showArrow) {
            Image(
                painter = painterResource(R.mipmap.icon_unselected_arrow),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 2.dp)
                    .size(12.dp)
            )
        }
    }
}

@Composable
private fun SortMenuItem(label: String, onClick: () -> Unit) {
    DropdownMenuItem(
        text = {
            Text(text = label, fontSize = 14.sp, color = Color(0xFF333333))
        },
        onClick = onClick
    )
}

@Composable
private fun SearchArticleItem(item: ResArticleList.ListBean, onClick: () -> Unit) {
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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.publishtime ?: "",
                fontSize = 11.sp,
                color = Color(0xFF999999),
                modifier = Modifier.padding(top = 12.dp)
            )
            SearchStatsRow(item = item, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun SearchStatsRow(item: ResArticleList.ListBean, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.mipmap.icon_unliked),
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
            painter = painterResource(R.mipmap.icon_pinlun),
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
            painter = painterResource(R.mipmap.icon_yanjin),
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
