package com.example.ytaudio.screens.playlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.R
import com.example.ytaudio.database.AudioInfo

class PlaylistAdapter(var audioPlaylist: List<AudioInfo>) :
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(audioPlaylist[position])
    }


    override fun getItemCount() = audioPlaylist.size


    fun setData(newAudioPlaylist: List<AudioInfo>) {
        audioPlaylist = newAudioPlaylist
        notifyDataSetChanged()
    }


    private var listener: OnItemClickListener? = null


    interface OnItemClickListener {
        fun onItemClick(itemView: View, position: Int)
    }


    fun setOnItemClickListener(reaction: (View, Int) -> Unit) {
        listener = object : OnItemClickListener {
            override fun onItemClick(itemView: View, position: Int) {
                reaction(itemView, position)
            }
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(audio: AudioInfo) {
            val titleTextView = itemView.findViewById<TextView>(R.id.audio_title)
            titleTextView.text = audio.audioTitle
        }

        init {
            itemView.setOnClickListener {
                if (listener != null) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION)
                        listener!!.onItemClick(itemView, position)
                }
            }
        }
    }
}