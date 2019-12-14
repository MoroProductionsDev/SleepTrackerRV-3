/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter // from package androidx. NOT android
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import com.example.android.trackmysleepquality.generated.callback.OnClickListener

// Constant identifier for the sealed class
private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

// Update declaration of SleepNight adaptor to support any type of viewHolder
// Set it up to know which view type to return (Header, View)
class SleepNightAdapter(val clickListener : SleepNightListener) : ListAdapter<DataItem, RecyclerView.ViewHolder>(SleepNightDiffCallback()) {
    // inflates the TextItem view and return the ViewHolder
    // provide the view holder when requested
    // check what layout needs to be inflate
    // Update return to RecyclerView.ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Return a item or header base on the sealed class
        return when(viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }


    // Retrieves item from the data list
    // Set up so the recycler view can render the data
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // This code will unwrap a data item to pass a SleepNight to the view holder
        when (holder) {
            is ViewHolder ->{
                val nightItem = getItem(position) as DataItem.SleepNightItem
                holder.bind(nightItem.sleepNight, clickListener)
            }
        }
    }

    // Add at text holder view class
    class TextViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent : ViewGroup) : TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.header, parent, false)
                return TextViewHolder(view)
            }
        }
    }

    // Checks whether the item is header or item
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM
        }
    }



    // Let the recycle view node when the data changes
    // internal view holder for display     // the root is a view. binding is a View Holder
    class ViewHolder private constructor (val binding: ListItemSleepNightBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: SleepNightListener, item: SleepNight) {
            binding.sleep = item
            binding.clickListener = clickListener
            binding.executePendingBindings()    // execute the pending binding right away
        }

        companion object {
             fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                //val view = layoutInflater.inflate(R.layout.list_item_sleep_night, parent, false)
                 val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)
                 return ViewHolder(binding)
            }
        }
    }
}

// Diff util class that improve performance by having a list of only the key and value that has changed
// Allows our adaptor to use diff util to determine the minimum changes when list get updated
class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>() {
    // Check if the id are the same
    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem.nightId == newItem.nightId
    }

    // Check all of the fields of both data classes
    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem == newItem
    }
}

class SleepNightListener(val clickListener: (sleepId : Long) -> Unit) {
    fun onClick(night : SleepNight) = clickListener(night.nightId)
}

// Sealed class. Create a single class that can represent two types of data a SleepNight or a header
// Sealed class is close type that means that all subclass of data must be define in this file
// Sealed cannot be a base class. It cannot extends and this class
// Avoid defining of any new DataItem
sealed class DataItem {
    // This a data class which is wrapper for SleepNight
    data class SleepNightItem(val sleepNigth : SleepNight) : DataItem() {
        override val id = sleepNigth.nightId
    }

    // Since the header has no data. It is created as an object which only has one instance of it
    // needs id of each item
    object Header: DataItem() {
        override val id = Long.MIN_VALUE
    }

    abstract val id : Long
}