package com.ddrcss.android.dummydemo

import android.os.Handler
import android.os.HandlerThread
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ddrcss.android.dummydemo.model.UserFullInfo
import com.ddrcss.android.dummydemo.model.UserLocation
import com.ddrcss.android.dummydemo.model.UserPreview
import com.ddrcss.android.dummydemo.model.UsersPage
import com.ddrcss.android.dummydemo.util.GlobalFactory

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

@RunWith(AndroidJUnit4::class)
class DummyIoTest {

    private val logger: Logger = LoggerFactory.getLogger(javaClass.simpleName)
    private val random = Random()

    @Test
    fun downloadOnePageOfUsers() {

        val response: UsersPage = GlobalFactory.dummyIoService.getUserList(
            Constant.DEFAULT_APP_ID,
            limit = 20
        ).execute().body()!!
        logger.trace(response.toString())
        assertTrue(response.pageIndex == 0)
    }

    // @Test
    fun getUserFullInfo() {
        val userList: UsersPage =
            GlobalFactory.dummyIoService.getUserList(Constant.DEFAULT_APP_ID).execute().body()!!

        for (preview in userList.userPreviews) {
            val userFullInfo =
                GlobalFactory.dummyIoService.getUser(Constant.DEFAULT_APP_ID, preview.id).execute()
                    .body()
            assertNotNull(userFullInfo)
            logger.trace("UserFullInfo={}", userFullInfo)
        }
    }

    private fun randomString(): String = random.nextLong().toString(16)

    private fun randomUserPreview(): UserPreview {
        return UserPreview(
            randomString(),
            randomString(),
            randomString(),
            randomString(),
            randomString()
        )
    }

    private fun randomUsersPage(): UsersPage {
        val count = 10 + random.nextInt(10);
        var userPreviews = List(count) { randomUserPreview() }
        return UsersPage(userPreviews, count, 0, count)
    }

    private fun randomUserFullInfo(): UserFullInfo {
        return UserFullInfo(
            randomString(), randomString(), randomString(), randomString(), randomString(),
            randomString(), randomString(), randomString(), randomString(), randomString(),
            UserLocation(
                randomString(),
                randomString(),
                randomString(),
                randomString(),
                randomString()
            )
        )
    }

    @Test
    fun savePageToCache() {
        val outPage = randomUsersPage()
        val cache = GlobalFactory.cache
        val thread = HandlerThread("thread").also { it.start() }
        cache.purge()
        cache.saveUsersPage(outPage, Handler(thread.looper)) {
            assertNull(it)
            thread.quit()
        }
    }

    @Test
    fun loadPageFromCache() {
        val outPage = randomUsersPage()
        val cache = GlobalFactory.cache
        val thread = HandlerThread("thread").also { it.start() }
        cache.purge()
        cache.saveUsersPage(outPage, Handler(thread.looper)) {
            val inPage =
                cache.loadUsersPage(outPage.limit, outPage.pageIndex, Handler(thread.looper), {
                    assertEquals(outPage, it)
                    thread.quit()
                }, {
                    thread.quit()
                    throw it
                })
        }
    }

    @Test
    fun saveFullInfoToCache() {
        val fullInfo = randomUserFullInfo()
        val cache = GlobalFactory.cache
        val thread = HandlerThread("thread").also { it.start() }
        cache.purge()
        cache.saveUserFullInfo(fullInfo, Handler(thread.looper)) {
            assertNull(it)
            thread.quit()
        }
    }

    @Test
    fun loadFullInfoFromCache() {
        val outFullInfo = randomUserFullInfo()
        val cache = GlobalFactory.cache
        val thread = HandlerThread("thread").also { it.start() }
        cache.purge()
        cache.saveUserFullInfo(outFullInfo, Handler(thread.looper)) {
            val inFullInfo = cache.loadUserFullInfo(outFullInfo.id, Handler(thread.looper), {
                assertEquals(outFullInfo, it)
                thread.quit()
            }, {
                thread.quit()
                throw it
            })
        }
    }

}