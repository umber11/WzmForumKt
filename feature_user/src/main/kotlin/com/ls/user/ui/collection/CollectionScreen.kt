package com.ls.user.ui.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ls.librarybase.bean.ResArticleList
import com.ls.librarybase.component.CommonImage
import com.ls.librarybase.component.PageWrapper
import com.ls.librarybase.component.StatusView
import com.ls.librarybase.component.ViewModelEffects
import com.ls.librarybase.component.ViewState

/**
 * 收藏页展示
 */
@Composable
fun CollectionScreen(
    vm: CollectionViewModel = viewModel(),
    onBack: () -> Unit,
    onArticleClick: (ResArticleList.ListBean) -> Unit
) {
    val articles by vm.mArticles.collectAsStateWithLifecycle()
    val loading = ViewModelEffects(vm = vm)

    // 等价原 onResume -> refreshLocal()
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) vm.refreshLocal()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        vm.loadCollectionList()
    }

    PageWrapper(title = "我的收藏", showBack = true, onBack = onBack, loading = loading) {
        val list = articles
        when {
            list == null -> Unit
            list.isEmpty() -> StatusView(
                state = ViewState.Empty,
                emptyText = "暂无收藏"
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().background(Color.White)
            ) {
                items(list, key = { it.id }) { item ->
                    CollectionArticleItem(item = item, onClick = { onArticleClick(item) })
                }
            }
        }
    }
}

@Composable
private fun CollectionArticleItem(
    item: ResArticleList.ListBean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(14.dp)
            .clickable(onClick = onClick)
    ) {
        CommonImage(
            url = item.image,
            modifier = Modifier.size(width = 112.dp, height = 80.dp),
            contentScale = ContentScale.Crop
        )
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
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = item.publishtime ?: "",
                fontSize = 11.sp,
                color = Color(0xFF999999),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
