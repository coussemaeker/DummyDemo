package com.ddrcss.android.dummydemo.ui

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ddrcss.android.dummydemo.BuildConfig
import com.ddrcss.android.dummydemo.R
import com.ddrcss.android.dummydemo.databinding.FragmentUserFullInfoBinding
import com.ddrcss.android.dummydemo.model.UserFullInfo
import com.ddrcss.android.dummydemo.util.DateConverter
import com.squareup.picasso.Picasso

class UserFullInfoFragment() : Fragment() {

    companion object {
        const val KEY_USER: String = BuildConfig.APPLICATION_ID
    }

    lateinit var binding: FragmentUserFullInfoBinding
    private val user by lazy { arguments?.get(KEY_USER) as UserFullInfo? }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserFullInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        user?.let { displayUserFullInfo(it) }
    }

    private fun displayUserFullInfo(userInfo: UserFullInfo) {
        userInfo.pictureUrl?.let { Picasso.get().load(it).into(binding.userPictureImage) }
        binding.fullnameText.text = Html.fromHtml(context!!.getString(
            R.string.user_fullname_format,
            userInfo.title.replaceFirstChar { it.uppercase() },
            userInfo.firstName,
            userInfo.lastName
        ))
        binding.genderText.text = userInfo.gender
        binding.emailText.text = userInfo.email
        binding.birthdateText.text = DateConverter.convertIsoDate(userInfo.dateOfBirth)
        binding.registerDateText.text = DateConverter.convertIsoDate(userInfo.registerDate)
        binding.phoneText.text = userInfo.phone
        binding.locationText.text = context!!.getString(
            R.string.user_location_format,
            userInfo.location.street,
            userInfo.location.city,
            userInfo.location.state,
            userInfo.location.country
        )
    }

}
