package com.ls.wzmforum.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ls.home.ui.home.HomeScreen
import com.ls.home.ui.home.HomeViewModel
import com.ls.librarybase.bean.ResArticleList
import com.ls.news.ui.news.NewsScreen
import com.ls.news.ui.news.NewsViewModel
import com.ls.products.ui.products.ProductsScreen
import com.ls.products.ui.products.ProductsViewModel
import com.ls.user.ui.user.UserCenterScreen
import com.ls.user.ui.user.UserViewModel
import com.ls.user.ui.user.UserViewModel.UserCenterAction
import com.ls.wzmforum.R
import kotlinx.coroutines.launch

/**
 * 主页面展示
 */
private data class MainTab(
    val label: String,
    val selectedIcon: Int,
    val unselectedIcon: Int
)

@Composable
fun MainScreen(
    onExit: () -> Unit,
    vm: MainViewModel = viewModel(),
    onArticleClick: (ResArticleList.ListBean) -> Unit,
    onSearchClick: () -> Unit,
    onUserNavigate: (UserCenterAction) -> Unit
) {
    val tabs = remember {
        listOf(
            MainTab("首页", R.mipmap.icon_selected_home, R.mipmap.icon_unselected_home),
            MainTab("资讯", R.mipmap.icon_selected_zixun, R.mipmap.icon_unselected_zixun),
            MainTab("产品", R.mipmap.icon_selected_chanpin, R.mipmap.icon_unselected_chanpin),
            MainTab("我的", R.mipmap.icon_selected_user, R.mipmap.icon_unselected_user)
        )
    }
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val saveableStateHolder = rememberSaveableStateHolder()
    val coroutineScope = rememberCoroutineScope()
    var showAgreement by remember { mutableStateOf(!vm.getPrivacyAgreementStatus()) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) { page ->
            saveableStateHolder.SaveableStateProvider(page) {
                when (page) {
                    0 -> HomeScreen(
                        vm = viewModel(),
                        onArticleClick = onArticleClick,
                        onSearchClick = onSearchClick
                    )
                    1 -> NewsScreen(
                        vm = viewModel(),
                        onArticleClick = onArticleClick,
                        onSearchClick = onSearchClick
                    )
                    2 -> ProductsScreen(
                        vm = viewModel(),
                        onItemClick = onArticleClick,
                        onSearchClick = onSearchClick
                    )
                    else -> UserCenterScreen(
                        vm = viewModel(),
                        onNavigate = onUserNavigate
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color.White)
        ) {
            tabs.forEachIndexed { index, tab ->
                val selected = index == pagerState.currentPage
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(if (selected) tab.selectedIcon else tab.unselectedIcon),
                        contentDescription = tab.label,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.label,
                        fontSize = 12.sp,
                        color = if (selected) Color(0xFF1082FF) else Color(0xFF999999)
                    )
                }
            }
        }
    }

    if (showAgreement) {
        AgreementDialog(
            onAgreement = {
                vm.savePrivacyAgreementStatus()
                showAgreement = false
            },
            onExit = {
                showAgreement = false
                onExit()
            },
            onDismiss = { showAgreement = false }
        )
    }
}

@Composable
private fun AgreementDialog(
    onAgreement: () -> Unit,
    onExit: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(14.dp)
        ) {
            Text(
                text = "本App深知个人信息对您的重要性，并会尽全力保护您的个人信息安全可靠。我们致力于维持您对我们的信任，恪守以下原则，保护您的个人信息：权责一致原则、目的明确原则、选择同意原则、最少够用原则、确保安全原则、主体参与原则、公开透明原则等。同时，我们承诺，我们将按业界成熟的安全标准，采取相应的安全保护措施来保护您的个人信息。请在使用我们的产品（或服务）前，仔细阅读并了解本《隐私权政策》。\n\n如何联系我们：如果您对本隐私政策有任何疑问、意见或建议，通过App内部的联系方式与我们联系，一般情况下，我们将在三十天内回复。",
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 14.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(Color.Black)
                    .clickable { onAgreement() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "同意", fontSize = 18.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(Color(0xFFBBBBBB))
                    .clickable { onExit() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "退出", fontSize = 18.sp, color = Color.Black)
            }
        }
    }
}
