package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val interactor: PlayerInteractor) : ViewModel() {
    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> get() = _currentTime

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private var updateJob: Job? = null

    companion object {
        private const val UPDATE_DELAY = 300L
    }

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
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (true) {
                if (interactor.isPlaying()) {
                    _currentTime.value = interactor.getCurrentPosition()
                    delay(UPDATE_DELAY)
                } else {
                    _currentTime.value = formatTime(0)
                    _isPlaying.value = false
                    break
                }
            }
        }
    }

    private fun stopTimer() {
        updateJob?.cancel()
        updateJob = null
    }

    override fun onCleared() {
        super.onCleared()
        interactor.stop()
        stopTimer()
    }
}