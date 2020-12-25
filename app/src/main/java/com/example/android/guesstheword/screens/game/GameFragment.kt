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

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.android.guesstheword.databinding.GameFragmentBinding
import com.example.android.guesstheword.screens.game.GameViewModel.Companion.INITIAL_SCORE

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    private val viewModel: GameViewModel by viewModels()

    private val navController: NavController by lazy { findNavController() }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = GameFragmentBinding.inflate(inflater, container, false)
            .apply {
                viewModel = this@GameFragment.viewModel
                lifecycleOwner = viewLifecycleOwner
            }
            .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
            gameFinishedEvent.observe(viewLifecycleOwner) { hasFinished ->
                if (hasFinished) navigateToScore()
            }
            buzzGameEvent.observe(viewLifecycleOwner) { buzzType -> vibrate(buzzType.pattern) }
        }
    }

    /**
     * Called when the game is finished
     */
    private fun navigateToScore() =
            GameFragmentDirections.actionGameToScore(viewModel.score.value ?: INITIAL_SCORE)
                    .run { navController.navigate(this) }
                    .also { viewModel.onGameFinishedNavigated() }

    private fun vibrate(pattern: LongArray) =
            activity?.getSystemService<Vibrator>()?.run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    //deprecated in API 26
                    @Suppress("DEPRECATION")
                    vibrate(pattern, -1)
                }
            }
}
