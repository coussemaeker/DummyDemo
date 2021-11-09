package com.ddrcss.android.dummydemo.cache

import android.content.Context
import android.os.Handler
import androidx.core.util.Consumer
import com.ddrcss.android.dummydemo.model.UserFullInfo
import com.ddrcss.android.dummydemo.model.UsersPage
import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class FileCache(val context : Context, val gson: Gson) {

    companion object {
        const val DIRNAME = "object_cache"

        fun userFullInfoKey(userId : String) = "user_full_info_$userId.json"

        fun usersPageKey(pageIndex: Int, limit: Int) = "users_page_${pageIndex}_${limit}.json"
    }

    val cacheDirectory = File(context.externalCacheDir, DIRNAME).also { it.mkdirs() }

    private fun UserFullInfo.cacheKey() : String = userFullInfoKey(this.id)

    private fun UsersPage.cacheKey() : String = usersPageKey(this.pageIndex, this.limit)

    private fun <T> writeObject(key: String, value : T) {
        val fileWriter = FileWriter(File(cacheDirectory, key))

        gson.toJson(value, fileWriter)
        fileWriter.close()
    }

    private fun <T> readObject(key: String, clazz: Class<T>) : T {
        val fileReader = FileReader(File(cacheDirectory, key))

        val value : T = gson.fromJson(fileReader, clazz)
        fileReader.close()
        return value
    }

    private fun hasObject(key : String) = File(cacheDirectory, key).canRead()

    fun saveUsersPage(usersPage : UsersPage, handler: Handler, onResult: Consumer<Throwable?>) {

        Thread() {
            try {
                writeObject(usersPage.cacheKey(), usersPage)
                handler.post { onResult.accept(null) }
            } catch (throwable : Throwable) {
                handler.post { onResult.accept(throwable) }
            }
        }.start()
    }

    fun loadUsersPage(
        pageIndex: Int,
        limit: Int,
        handler: Handler,
        onSuccess: Consumer<UsersPage>,
        onFailure: Consumer<Throwable>
    ) {
        Thread() {
            try {
                val key = usersPageKey(pageIndex, limit)
                if (!hasObject(key)) {
                    throw NoSuchElementException()
                }
                val value = readObject(key, UsersPage::class.java)
                handler.post { onSuccess.accept(value) }
            } catch (throwable : Throwable) {
                handler.post { onFailure.accept(throwable) }
            }
        }.start()
    }

    fun saveUserFullInfo(userFullInfo: UserFullInfo, handler: Handler, onResult: Consumer<Throwable?>) {

        Thread() {
            try {
                writeObject(userFullInfo.cacheKey(), userFullInfo)
                handler.post { onResult.accept(null) }
            } catch (throwable : Throwable) {
                handler.post { onResult.accept(throwable) }
            }
        }.start()
    }

    fun loadUserFullInfo(userId : String, handler: Handler, onSuccess : Consumer<UserFullInfo>, onFailure : Consumer<Throwable>) {
        Thread() {
            try {
                val key = userFullInfoKey(userId)
                if (!hasObject(key)) {
                    throw NoSuchElementException()
                }
                val value = readObject(key, UserFullInfo::class.java)
                handler.post { onSuccess.accept(value) }
            } catch (throwable : Throwable) {
                handler.post { onFailure.accept(throwable) }
            }
        }.start()
    }

    fun purge() {
        for (child: File in cacheDirectory.listFiles()) {
            child.delete()
        }
    }

}
