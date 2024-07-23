package com.antriksha.maulidairy.ui

import com.google.firebase.storage.StorageMetadata

interface OnItemClick {
    fun onItemClick(adapterPosition: String, storageMetadata: StorageMetadata?)
    fun onItemLongClick(adapterPosition: String, storageMetadata: StorageMetadata?)
}
