package com.yogesh.alltools.imagetopdf

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yogesh.alltools.R

class ImageAdapter(
    private val context: Context
) :
    RecyclerView.Adapter<ImageAdapter.ListViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView = inflater.inflate(R.layout.single_image_grid_item, parent, false)
        return ListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        Glide
            .with(context)
            .load(Uri.parse(PictureContent.items[position].uri.toString()))
            .placeholder(R.drawable.ic_gallery)
            .into(holder.imageView);

        holder.checkBox.isChecked = PictureContent.items[position].checked

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            PictureContent.items[position].checked = isChecked
        }

        holder.imageView.setOnClickListener {
            holder.checkBox.isChecked = !holder.checkBox.isChecked
            PictureContent.items[position].checked = holder.checkBox.isChecked
        }

    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
    }

    override fun getItemCount(): Int {
        return PictureContent.items.size
    }

}