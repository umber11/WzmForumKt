package com.ls.products.ui.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ls.librarybase.bean.ResArticleList
import com.ls.librarybase.component.CommonImage
import com.ls.products.R

/**
 * 产品界面：标题栏、搜索栏、频道 Tab、双列网格产品卡片及筛选排序弹窗。
 */
@Composable
fun ProductsScreen(
    vm: ProductsViewModel,
    onItemClick: (ResArticleList.ListBean) -> Unit,
    onSearchClick: () -> Unit
) {
    val categories by vm.mCategories.collectAsStateWithLifecycle()
    val content by vm.mContent.collectAsStateWithLifecycle()
    val cats = categories
    val arts = content
    var selectedIndex by remember { mutableStateOf(0) }
    var resumeTick by remember { mutableStateOf(0) }
    var showFilter by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val total = content?.size ?: 0
            if (total < 6) false
            else (gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) >= total - 3
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { resumeTick++ }

    LaunchedEffect(vm) { vm.refresh() }

    LaunchedEffect(categories) {
        if (!cats.isNullOrEmpty()) {
            selectedIndex = 0
            vm.onCategorySelected(cats!![0].id.toString())
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !vm.isLoadMore()) vm.loadContent(true)
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
                text = "产品",
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
            ProductsSearchBar(
                onClick = onSearchClick,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                painter = painterResource(R.mipmap.icon_chanpin),
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
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = gridState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = 4.dp,
                    top = 4.dp,
                    end = 4.dp,
                    bottom = 4.dp
                )
            ) {
                items(arts!!, key = { it.id }) { item ->
                    resumeTick
                    ProductCard(item = item, onClick = { onItemClick(item) })
                }
            }
        }

        if (showFilter) {
            ProductsFilterDrawerDialog(
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
private fun ProductsSearchBar(onClick: () -> Unit, modifier: Modifier = Modifier) {
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
private fun ProductCard(item: ResArticleList.ListBean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .background(Color(0xFFEEEEEE))
            .aspectRatio(3f / 2f)
            .clickable(onClick = onClick)
    ) {
        CommonImage(url = item.image, modifier = Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color(0xB3000000))
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Text(
                text = item.title ?: "",
                fontSize = 12.sp,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
