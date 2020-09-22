/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.guesstheword.screens.game

import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.android.guesstheword.databinding.GameFragmentBinding

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    private val viewModel by viewModels<GameViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = GameFragmentBinding.inflate(inflater)
            .apply {
                this.viewmodel = viewModel
                this.lifecycleOwner = viewLifecycleOwner
            }
            .also {
                viewModel.run {
                    eventGameFinish.observe(viewLifecycleOwner, Observer { hasFinished ->
                        handleEventGameFinish(hasFinished)
                    })
                    eventBuzz.observe(viewLifecycleOwner, Observer { buzzType ->
                        handleEventBuzz(buzzType)
                    })
                }
            }
            .root

    /**
     * Called when the game is finished
     */
    private fun handleEventGameFinish(hasFinished: Boolean) {
        if (hasFinished) {
            val action = GameFragmentDirections.actionGameToScore(viewModel.score.value ?: 0)
            findNavController(this).navigate(action)
            viewModel.onGameFinishComplete()
        }
    }

    /**
     *
     */
    private fun handleEventBuzz(type: GameViewModel.BuzzType) {
        if (type != GameViewModel.BuzzType.NO_BUZZ) {
            buzz(type.pattern)
            viewModel.onBuzzComplete()
        }
    }

    @Suppress("DEPRECATION")
    private fun buzz(pattern: LongArray) {
        activity?.getSystemService<Vibrator>()
                ?.let { vibrator ->
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                    } else {
                        vibrator.vibrate(pattern, -1)
                    }
                }
    }
}
