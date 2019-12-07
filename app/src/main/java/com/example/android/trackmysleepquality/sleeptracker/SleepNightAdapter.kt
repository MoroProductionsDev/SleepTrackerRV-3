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

import android.view.LayoutInflater
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

class SleepNightAdapter : ListAdapter<SleepNight, SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()) {
    // inflates the TextItem view and return the ViewHolder
    // provide the view holder when requested
    // check what layout needs to be inflate
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    // Retrieves item from the data list
    // Set up so the recycler view can render the data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    // Let the recycle view node when the data changes
    // internal view holder for display
                                                                        // the root is a view. binding is a View Holder
    class ViewHolder private constructor (val binding: ListItemSleepNightBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SleepNight) {
            binding.sleep = item
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