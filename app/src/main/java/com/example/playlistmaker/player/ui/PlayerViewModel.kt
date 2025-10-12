package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val interactor: PlayerInteractor) : ViewModel() {
    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> get() = _currentTime

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val handler = Handler(Looper.getMainLooper())
    private var updateTask: Runnable? = null

    fun prepare(track: Track) {
        interactor.preparePlayer(track)
        _currentTime.value = formatTime(0)
        _isPlaying.value = false
    }

    private fun formatTime(millis: Int): String {
        return SimpleDateFormat("m:ss", Locale.getDefault()).format(millis)
    }

    fun playbackControl() {
        if (interactor.isPlaying()) {
            interactor.pause()
            _isPlaying.value = false
            stopTimer()
        } else {
            interactor.play()
            _isPlaying.value = true
            startTimer()
        }
    }

    private fun startTimer() {
        updateTask = Runnable {
            _currentTime.value = interactor.getCurrentPosition()
            if (interactor.isPlaying()) {
                handler.postDelayed(updateTask!!, 300)
            } else {
                _currentTime.value = formatTime(0)
                _isPlaying.value = false
                handler.removeCallbacks(updateTask!!)
            }
        }
        handler.post(updateTask!!)
    }

    private fun stopTimer() {
        updateTask?.let { handler.removeCallbacks(it) }
    }

    override fun onCleared() {
        super.onCleared()
        interactor.stop()
        stopTimer()
        handler.removeCallbacksAndMessages(null)
    }
}