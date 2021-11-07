package com.ddrcss.android.dummydemo.ui

import android.content.Context
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ddrcss.android.dummydemo.R
import com.ddrcss.android.dummydemo.databinding.ItemUserPreviewBinding
import com.ddrcss.android.dummydemo.model.UserPreview
import com.squareup.picasso.Picasso

class UserPreviewAdapter(context: Context, userList: List<UserPreview>) :
    ArrayAdapter<UserPreview>(context, 0, userList) {

    companion object {
        private class ViewHolder() {
            var fullnameText: TextView? = null
            var pictureImage: ImageView? = null
        }
    }

    val layoutInflater: LayoutInflater by lazy { context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        lateinit var itemView: View
        val viewHolder: ViewHolder =
            convertView?.getTag(R.id.view_holder) as ViewHolder? ?: ViewHolder()
        if (convertView == null) {
            val binding = ItemUserPreviewBinding.inflate(layoutInflater)
            itemView = binding.root
            itemView.setTag(R.id.view_holder, viewHolder)
            viewHolder.fullnameText = binding.fullnameText
            viewHolder.pictureImage = binding.userPictureImage
        } else {
            itemView = convertView
        }
        val userPreview = getItem(position)
        viewHolder.fullnameText?.text = Html.fromHtml(
            context.getString(
                R.string.user_fullname_format,
                userPreview?.title?.replaceFirstChar { it.uppercase() },
                userPreview?.firstName,
                userPreview?.lastName
            )
        )
        if (!TextUtils.isEmpty(userPreview?.pictureUrl)) {
            Picasso.get().load(userPreview?.pictureUrl).into(viewHolder.pictureImage)
        }
        return itemView
    }

}