package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin

object SoundEffectHelper {

    /**
     * Plays a rich, celebratory confirmation chime.
     * Uses synthesized arpeggiated major chord (C5 -> E5 -> G5 -> C6) with smooth exponential decay.
     */
    fun playOrderConfirmationChime() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                playSynthesizedCelebration()
            } catch (e: Throwable) {
                Log.w("SoundEffectHelper", "AudioTrack synthesis fallback to ToneGenerator", e)
                playToneGeneratorFallback()
            }
        }
    }

    private fun playSynthesizedCelebration() {
        val sampleRate = 44100
        val durationSeconds = 0.8
        val numSamples = (sampleRate * durationSeconds).toInt()
        val buffer = ShortArray(numSamples)

        // Musical major chord: C5 (523.25 Hz), E5 (659.25 Hz), G5 (783.99 Hz), C6 (1046.50 Hz)
        val freqs = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
        val startTimes = doubleArrayOf(0.0, 0.09, 0.18, 0.27)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            var sample = 0.0

            for (k in freqs.indices) {
                val noteT = t - startTimes[k]
                if (noteT >= 0) {
                    val envelope = exp(-noteT * 5.2)
                    sample += 0.28 * envelope * sin(2.0 * Math.PI * freqs[k] * noteT)
                }
            }

            buffer[i] = (sample * 32767).coerceIn(-32768.0, 32767.0).toInt().toShort()
        }

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()
    }

    private fun playToneGeneratorFallback() {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 95)
            toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 350)
        } catch (_: Throwable) {}
    }
}
