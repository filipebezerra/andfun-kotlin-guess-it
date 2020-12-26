package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    // The current word
    private val _word = MutableLiveData("")
    val word: LiveData<String>
        get() = _word

    // The current score
    private val _score = MutableLiveData(INITIAL_SCORE)
    val score: LiveData<Int>
        get() = _score

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    private val _gameFinishedEvent = MutableLiveData(false)
    val gameFinishedEvent: LiveData<Boolean>
        get() = _gameFinishedEvent

    private lateinit var timer: CountDownTimer

    private val _currentTime = MutableLiveData(DONE)

    val currentTimeText: LiveData<String> = Transformations.map(_currentTime) {
        DateUtils.formatElapsedTime(it)
    }

    private val _buzzGameEvent = MutableLiveData<BuzzType>()
    val buzzGameEvent: LiveData<BuzzType>
        get() = _buzzGameEvent

    init {
        resetList()
        nextWord()
        configureTimer()
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            finishGame()
        } else {
            _word.value = wordList.removeAt(0)
        }
    }

    /**
     * Setup and start the game timer
     */
    private fun configureTimer() {
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) = gameTicking(millisUntilFinished)
            override fun onFinish() = finishGame()
        }.run { start() }
    }

    /**
     * Game event fired when [timer] completed one cycle of [ONE_SECOND]
     */
    private fun gameTicking(millisUntilFinished: Long) {
        (millisUntilFinished / ONE_SECOND).let { secondsUntilFinished ->
            _currentTime.value = secondsUntilFinished
            secondsUntilFinished.takeIf { it <= SECONDS_TO_FINISH }?.apply {
                _buzzGameEvent.value = BuzzType.COUNTDOWN_PANIC
            }
        }
    }

    /**
     * Game event fired when [timer] is [DONE] counting
     */
    private fun finishGame() {
        _currentTime.value = DONE
        _gameFinishedEvent.value = true
        _buzzGameEvent.value = BuzzType.GAME_FINISHED
    }

    /**
     * User interaction event fired when user touched SKIP button
     */
    fun onSkip() {
        _score.value = score.value?.minus(1)
        nextWord()
    }

    /**
     * User interaction event fired when user touched GOT IT button
     */
    fun onCorrect() {
        _buzzGameEvent.value = BuzzType.CORRECT
        _score.value = score.value?.plus(1)
        nextWord()
    }

    /**
     * Navigation event fired when user completed navigating to Score screen
     */
    fun onGameFinishedNavigated() {
        _gameFinishedEvent.value = false
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

    companion object {
        // This is when the game is over
        const val DONE = 0L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L
        // This is the total time of the game
        const val COUNTDOWN_TIME = 60000L

        // This is the initial score of the game
        const val INITIAL_SCORE = 0

        // This is the number os seconds to finish the game
        const val SECONDS_TO_FINISH = 10
    }
}