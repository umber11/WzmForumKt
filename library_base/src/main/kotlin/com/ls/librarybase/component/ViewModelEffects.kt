package com.ls.librarybase.component

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ls.librarybase.base.BaseViewModel

/**
 *ViewModel 副作用统一处理器
 */
@Composable
fun ViewModelEffects(
    vm: BaseViewModel,
): Boolean {
    val context = LocalContext.current
    val toast by vm.toastText.collectAsStateWithLifecycle(initialValue = null)
    val loading by vm.showLoading.collectAsStateWithLifecycle()

    LaunchedEffect(toast) {
        toast?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }
    return loading
}
