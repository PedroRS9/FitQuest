package es.ulpgc.pigs.fitquest.extensions

import android.content.Context
import android.media.MediaPlayer

fun playSound(context: Context, sound: Int) {
    val mediaPlayer: MediaPlayer = MediaPlayer.create(context, sound)
    mediaPlayer.setOnCompletionListener { mp ->
        mp.release() //
    }
    mediaPlayer.start()
}