package com.example.android.guesstheword.screens.score

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreViewModel(score: Int) : ViewModel() {

    private val _scoreText = MutableLiveData(score.toString())
    val scoreText: LiveData<String>
        get() = _scoreText

    private val _playAgainGameEvent = MutableLiveData(false)
    val playAgainGameEvent: LiveData<Boolean>
        get() = _playAgainGameEvent

    fun playAgain() = _playAgainGameEvent.postValue(true)

    fun navigatedToPlayAgain() = _playAgainGameEvent.postValue(false)
}