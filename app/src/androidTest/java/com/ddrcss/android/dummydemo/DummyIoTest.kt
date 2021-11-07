package com.ddrcss.android.dummydemo

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ddrcss.android.dummydemo.model.UsersPage
import com.ddrcss.android.dummydemo.util.GlobalFactory

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@RunWith(AndroidJUnit4::class)
class DummyIoTest {

    private val logger: Logger = LoggerFactory.getLogger(javaClass.simpleName)

    @Test
    fun downloadOnePageOfUsers() {

        val response: UsersPage = GlobalFactory.dummyIoService.getUserList(
            Constant.DEFAULT_APP_ID,
            limit = 20
        ).execute().body()!!
        logger.trace(response.toString())
        assertTrue(response.pageIndex == 0)
    }

    @Test
    fun getUserFullInfo() {
        val userList: UsersPage = GlobalFactory.dummyIoService.getUserList(Constant.DEFAULT_APP_ID).execute().body()!!

        for (preview in userList.userPreviews) {
            val userFullInfo = GlobalFactory.dummyIoService.getUser(Constant.DEFAULT_APP_ID, preview.id).execute().body()
            assertNotNull(userFullInfo)
            logger.trace("UserFullInfo={}", userFullInfo)
        }
    }
}