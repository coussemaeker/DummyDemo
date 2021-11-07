package com.ddrcss.android.dummydemo.ui

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.ddrcss.android.dummydemo.Constant
import com.ddrcss.android.dummydemo.R
import com.ddrcss.android.dummydemo.databinding.FragmentUserListBinding
import com.ddrcss.android.dummydemo.model.UserFullInfo
import com.ddrcss.android.dummydemo.model.UserPreview
import com.ddrcss.android.dummydemo.model.UsersPage
import com.ddrcss.android.dummydemo.util.GlobalFactory
import org.slf4j.LoggerFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserListFragment : Fragment() {

    private lateinit var binding: FragmentUserListBinding
    private val userListLiveData = MutableLiveData<List<UserPreview>>()
    private val userLiveData = MutableLiveData<UserFullInfo>()
    private val logger = LoggerFactory.getLogger(javaClass.simpleName)
    private val uiHandler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loadButton.setOnClickListener { loadUsers() }

        userListLiveData.observe(this) { userPreviews ->
            binding.userListView.adapter = UserPreviewAdapter(context!!, userPreviews)
            binding.userListView.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ -> loadUserFullInfo(userPreviews[position].id) }
        }

        userLiveData.observe(this) { userFullInfo ->
            val fragment = UserFullInfoFragment()
            fragment.arguments =
                Bundle().also { it.putParcelable(UserFullInfoFragment.KEY_USER, userFullInfo) }
            activity?.let {
                it.supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment, "USER_FULL_INFO").addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun loadUsers() {

        GlobalFactory.cache.loadUsersPage(
            Constant.DEFAULT_PAGE_INDEX,
            Constant.DEFAULT_PAGE_LIMIT,
            uiHandler,
            onSuccess = {
                userListLiveData.postValue(it.userPreviews)
            }) {
            downloadUsers()
        }
    }

    private fun downloadUsers() {
        GlobalFactory.dummyIoService.getUserList(Constant.DEFAULT_APP_ID)
            .enqueue(object : Callback<UsersPage> {
                override fun onResponse(call: Call<UsersPage>, response: Response<UsersPage>) {
                    // binding.userListView.adapter = UserPreviewAdapter(context!!, response.body()!!.userPreviews)
                    userListLiveData.postValue(response.body()!!.userPreviews)
                    GlobalFactory.cache.saveUsersPage(response.body()!!, uiHandler) {
                        it?.let { logger.error("saving UsersPage", it) }
                    }
                }

                override fun onFailure(call: Call<UsersPage>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Exception while loading " + t.javaClass.simpleName + " " + t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun loadUserFullInfo(userId: String) {

        GlobalFactory.cache.loadUserFullInfo(userId, uiHandler, onSuccess = {
            userLiveData.postValue(it)
        }, onFailure = {
            downloadUserFullInfo(userId)
        })
    }

    private fun downloadUserFullInfo(userId: String) {

        GlobalFactory.dummyIoService.getUser(Constant.DEFAULT_APP_ID, userId)
            .enqueue(object : Callback<UserFullInfo> {
                override fun onResponse(
                    call: Call<UserFullInfo>,
                    response: Response<UserFullInfo>
                ) {
                    userLiveData.postValue(response.body()!!)
                    GlobalFactory.cache.saveUserFullInfo(response.body()!!, uiHandler) {
                        it?.let { logger.error("saving UserFullInfo", it) }
                    }
                }

                override fun onFailure(call: Call<UserFullInfo>, t: Throwable) {
                    logger.error("downloadUserFullInfo", t)
                }
            })
    }

}
