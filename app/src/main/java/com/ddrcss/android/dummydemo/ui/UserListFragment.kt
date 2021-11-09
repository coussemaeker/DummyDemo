package com.ddrcss.android.dummydemo.ui

import android.os.Bundle
import android.os.Handler
import android.view.*
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
    private val infoLiveData = MutableLiveData<String>()
    private val logger = LoggerFactory.getLogger(javaClass.simpleName)
    private val uiHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

        infoLiveData.observe(this) {
            binding.infoText.text = it
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_item_clear_cache) {
            GlobalFactory.cache.purge()
            userListLiveData.postValue(listOf())
            infoLiveData.postValue(getString(R.string.info_cache_cleared))
            return true;
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    private fun loadUsers() {

        infoLiveData.postValue("")
        GlobalFactory.cache.loadUsersPage(
            Constant.DEFAULT_PAGE_INDEX,
            Constant.DEFAULT_PAGE_LIMIT,
            uiHandler,
            onSuccess = {
                userListLiveData.postValue(it.userPreviews)
                infoLiveData.postValue(getString(R.string.info_from_cache))
            }) {
            downloadUsers()
        }
    }

    private fun downloadUsers() {
        GlobalFactory.dummyIoService.getUserList(Constant.DEFAULT_APP_ID)
            .enqueue(object : Callback<UsersPage> {
                override fun onResponse(call: Call<UsersPage>, response: Response<UsersPage>) {
                    userListLiveData.postValue(response.body()!!.userPreviews)
                    GlobalFactory.cache.saveUsersPage(response.body()!!, uiHandler) {
                        it?.let { logger.error("saving UsersPage", it) }
                    }
                    infoLiveData.postValue(getString(R.string.info_from_network))
                }

                override fun onFailure(call: Call<UsersPage>, t: Throwable) {
                    /*
                    Toast.makeText(
                        context,
                        "Exception while loading " + t.javaClass.simpleName + " " + t.message,
                        Toast.LENGTH_LONG
                    ).show()
                    */
                    infoLiveData.postValue(getString(R.string.info_exception, t.javaClass.simpleName, t.message))
                }
            })
    }

    private fun loadUserFullInfo(userId: String) {

        infoLiveData.postValue("")
        GlobalFactory.cache.loadUserFullInfo(userId, uiHandler, onSuccess = {
            infoLiveData.postValue(getString(R.string.info_user_from_cache))
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
                    infoLiveData.postValue(getString(R.string.info_user_from_network))
                    userLiveData.postValue(response.body()!!)
                    GlobalFactory.cache.saveUserFullInfo(response.body()!!, uiHandler) {
                        it?.let { logger.error("saving UserFullInfo", it) }
                    }
                }

                override fun onFailure(call: Call<UserFullInfo>, t: Throwable) {
                    infoLiveData.postValue(getString(R.string.info_exception, t.javaClass.simpleName, t.message))
                    logger.error("downloadUserFullInfo", t)
                }
            })
    }

}
