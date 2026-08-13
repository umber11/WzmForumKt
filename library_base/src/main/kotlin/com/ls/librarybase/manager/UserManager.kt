package com.ls.librarybase.manager

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.ls.librarybase.base.BaseApplication
import com.ls.librarybase.bean.ResUser
import com.ls.librarybase.bean.UserInfo
import com.ls.librarybase.navigation.LoginStateBus
import com.ls.librarybase.utils.PendingSyncManager
import java.io.IOException
import java.security.GeneralSecurityException
/**
 * 用户状态管理单例：加密持久化登录 token、用户信息，并同步登录状态。
 */
class UserManager private constructor() {

    private val mPreferences = run {
        try {
            val masterAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                PREFS_NAME, masterAlias, BaseApplication.getContext(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun saveToken(token: String) {
        mPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    val token: String?
        get() = mPreferences.getString(KEY_TOKEN, null)

    fun saveUserInfo(user: ResUser) {
        val userInfo = user.user ?: return
        mPreferences.edit()
            .putString(KEY_USER_ID, userInfo.id)
            .putString(KEY_NICK_NAME, userInfo.nickname)
            .putString(KEY_USER_NAME, userInfo.username)
            .putString(KEY_AVATAR, userInfo.avatar)
            .putString(KEY_BIO, userInfo.bio)
            .putString(KEY_STATUS, userInfo.status)
            .putInt(KEY_FANS, user.fans)
            .putInt(KEY_FOLLOW, user.follow)
            .putInt(KEY_MEDAL, user.medal)
            .apply()
    }

    val userInfo: ResUser
        get() {
            val userId = mPreferences.getString(KEY_USER_ID, null)
            val nickName = mPreferences.getString(KEY_NICK_NAME, null)
            val userName = mPreferences.getString(KEY_USER_NAME, null)
            val bio = mPreferences.getString(KEY_BIO, null)
            val avatar = mPreferences.getString(KEY_AVATAR, null)
            val status = mPreferences.getString(KEY_STATUS, null)
            val fans = mPreferences.getInt(KEY_FANS, 0)
            val follow = mPreferences.getInt(KEY_FOLLOW, 0)
            val medal = mPreferences.getInt(KEY_MEDAL, 0)

            val user = ResUser()
            user.fans = fans
            user.follow = follow
            user.medal = medal

            val userInfo = UserInfo()
            userInfo.id = userId
            userInfo.nickname = nickName
            userInfo.username = userName
            userInfo.bio = bio
            userInfo.avatar = avatar
            userInfo.status = status
            user.user = userInfo

            return user
        }

    fun logout() {
        mPreferences.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_USER_ID)
            .remove(KEY_NICK_NAME)
            .remove(KEY_USER_NAME)
            .remove(KEY_AVATAR)
            .remove(KEY_BIO)
            .remove(KEY_STATUS)
            .remove(KEY_FANS)
            .remove(KEY_FOLLOW)
            .remove(KEY_MEDAL).apply()
        // 清空待同步队列，防止上一账号的离线点赞/收藏补传到下一个账号
        PendingSyncManager.clear(BaseApplication.getContext())
        LoginStateBus.post(false)
    }

    fun isLogin(): Boolean {
        return !token.isNullOrEmpty()
    }

    fun updateUserInfo(avatarUrl: String, nickName: String, bio: String) {
        mPreferences.edit()
            .putString(KEY_NICK_NAME, nickName)
            .putString(KEY_AVATAR, avatarUrl)
            .putString(KEY_BIO, bio)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_TOKEN = "key_token"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_NICK_NAME = "key_nick_name"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_BIO = "key_bio"
        private const val KEY_AVATAR = "key_avatar"
        private const val KEY_STATUS = "key_status"
        private const val KEY_FOLLOW = "key_follow"
        private const val KEY_FANS = "key_fans"
        private const val KEY_MEDAL = "key_medal"

        @Volatile
        private var instance: UserManager? = null

        @JvmStatic
        fun getInstance(): UserManager {
            return instance ?: synchronized(this) {
                instance ?: UserManager().also { instance = it }
            }
        }
    }
}
