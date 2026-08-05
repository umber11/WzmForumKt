package com.ls.user.ui.editinfo

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.ls.librarybase.base.BaseApplication
import com.ls.librarybase.base.IRequestCallback
import com.ls.librarybase.bean.UserInfo
import com.ls.librarybase.manager.UserManager
import com.ls.network.ApiCall
import com.ls.network.bean.ResBase
import com.ls.user.api.UserApiServiceProvider
import com.ls.user.bean.ReqUpdateUserProfile
import com.ls.user.bean.ResUpload
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.IOException

class EditUserModel {
    fun isLogin(): Boolean = UserManager.getInstance().isLogin()

    val userInfo: UserInfo?
        get() {
            var userInfo: UserInfo? = null
            if (isLogin()) {
                userInfo = UserManager.getInstance().userInfo?.user
            }
            return userInfo
        }

    fun updateFile(uri: Uri, callback: IRequestCallback<ResUpload>) {
        val multipartBody = createMultipartBody(uri)
        val token = UserManager.getInstance().token ?: ""
        val call = UserApiServiceProvider.apiService.uploadFile(token, multipartBody)
        ApiCall.enqueueCommon(call, object : ApiCall.ApiCallBack<ResBase<ResUpload>> {
            override fun onSuccess(result: ResBase<ResUpload>) {
                val data = result.data ?: return
                callback.onLoadSuccess(data)
            }

            override fun onError(errorCode: Int, message: String) {
                callback.onLoadFailure(errorCode, message)
            }
        })
    }

    fun updateUserInfo(avatarUrl: String?, nickName: String?, bio: String?, callback: IRequestCallback<ResBase<Void>>) {
        if (isLogin()) {
            val userProfile = ReqUpdateUserProfile()
            userProfile.avatar = avatarUrl
            userProfile.nickname = nickName
            userProfile.bio = bio
            val token = UserManager.getInstance().token ?: ""
            val call = UserApiServiceProvider.apiService.updateUserProfile(token, userProfile)
            ApiCall.enqueueCommon(call, object : ApiCall.ApiCallBack<ResBase<Void>> {
                override fun onSuccess(result: ResBase<Void>) {
                    UserManager.getInstance().updateUserInfo(avatarUrl ?: "", nickName ?: "", bio ?: "")
                    callback.onLoadSuccess(result)
                }

                override fun onError(errorCode: Int, message: String) {
                    callback.onLoadFailure(errorCode, message)
                }
            })
        }
    }

    private fun createMultipartBody(uri: Uri): MultipartBody.Part {
        val contentResolver = BaseApplication.getContext().contentResolver
        val mimeType = contentResolver.getType(uri)
        val requestBody = object : RequestBody() {
            override fun contentType(): MediaType? = mimeType?.toMediaTypeOrNull()

            @Throws(IOException::class)
            override fun writeTo(sink: BufferedSink) {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val buffer = ByteArray(4096)
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        sink.write(buffer, 0, read)
                    }
                }
            }
        }
        return MultipartBody.Part.createFormData("file", getFileNameFromUri(uri), requestBody)
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var fileName: String? = null
        if (uri.scheme == "content") {
            val contentResolver = BaseApplication.getContext().contentResolver
            try {
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            fileName = cursor.getString(nameIndex)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (uri.scheme == "file") {
            fileName = File(uri.path ?: "").name
        }
        return fileName ?: "unnamed_file"
    }
}
