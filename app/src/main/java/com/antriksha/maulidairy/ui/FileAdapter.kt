package com.antriksha.maulidairy.ui

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.antriksha.maulidairy.R
import com.google.firebase.storage.StorageMetadata
import java.text.SimpleDateFormat
import java.util.Locale

class FileAdapter(
    private val mContext: Context,
    private val fileList: LinkedHashMap<String, StorageMetadata?>,
    private val onItemClick: OnItemClick
) :
    RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    private var isSwitchView = false
    private var LIST_ITEM = 1
    private var GRID_ITEM = 2

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileNameTextView: TextView = itemView.findViewById(R.id.fileNameTextView)
        val fileNameDate: TextView = itemView.findViewById(R.id.fileNameDate)
        val iv_icon: ImageView = itemView.findViewById(R.id.iv_icon)

        init {
            itemView.setOnClickListener {
                onItemClick.onItemClick(getElementNameByIndex(fileList, adapterPosition), getElementByIndex(fileList, adapterPosition))
            }

            itemView.setOnLongClickListener {
                onItemClick.onItemLongClick(getElementNameByIndex(fileList, adapterPosition), getElementByIndex(fileList, adapterPosition))
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return FileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.fileNameTextView.text = getElementNameByIndex(fileList, position)
        val contentType = fileList[getElementNameByIndex(fileList, position)]
        if (contentType == null) {
            holder.fileNameDate.text = ""
            holder.iv_icon.setImageDrawable(
                ContextCompat.getDrawable(
                    mContext,
                    R.drawable.folder_icon
                )
            )
        } else {
            holder.fileNameDate.text = "Modified ${getModifiedDate(contentType?.updatedTimeMillis)}"
            if (!TextUtils.isEmpty(contentType?.contentType)) {
                if (contentType?.contentType?.contains("pdf") == true) {
                    holder.iv_icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.pdf
                        )
                    )
                } else if (contentType?.contentType?.contains("image") == true) {
                    holder.iv_icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.img
                        )
                    )
                } else if (contentType?.contentType?.contains("octet") == true) {
                    holder.iv_icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.txt
                        )
                    )
                } else if (contentType?.contentType?.contains("xml") == true) {
                    holder.iv_icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.xml
                        )
                    )
                }
                else if (contentType?.contentType?.contains("video") == true || contentType?.contentType?.contains("mp4") == true) {
                    holder.iv_icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.video
                        )
                    )
                }
                else if (contentType?.contentType?.contains("doc") == true) {
                    holder.iv_icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.docx
                        )
                    )
                }
                else {
                    holder.iv_icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.document
                        )
                    )
                }
            }
        }
    }

    private fun getModifiedDate(updatedTimeMillis: Long?): Any {
        val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
        return formatter.format(updatedTimeMillis)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    fun getElementNameByIndex(map: LinkedHashMap<*, *>, index: Int): String {
        //return map[map.keys.toTypedArray()[index]].toString()
        return map.keys.toTypedArray()[index].toString()
    }

    fun getElementByIndex(
        map: LinkedHashMap<String, StorageMetadata?>,
        index: Int
    ): StorageMetadata? {
        return map.values.toTypedArray()[index]
    }

    override fun getItemViewType(position: Int): Int {
        return if (isSwitchView) {
            LIST_ITEM
        } else {
            GRID_ITEM
        }
    }

    fun toggleItemViewType(): Boolean {
        isSwitchView = !isSwitchView
        return isSwitchView
    }
}
