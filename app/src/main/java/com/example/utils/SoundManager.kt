package com.example.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundManager {
    private var audioManager: AudioManager? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun init(context: Context) {
        if (audioManager == null) {
            audioManager = context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        }
    }

    fun playClick() {
        scope.launch {
            try {
                // Try system keyboard press sound effect first
                val played = audioManager?.let { am ->
                    am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 0.7f)
                    true
                } ?: false
                if (!played) {
                    playPcmClick()
                }
            } catch (e: Exception) {
                playPcmClick()
            }
        }
    }

    fun playCorrect() {
        scope.launch {
            try {
                // Bright, uplifting major chord chime: C5 (523Hz), E5 (659Hz), G5 (784Hz)
                playPcmToneSeries(
                    listOf(
                        Tone(frequency = 523.25, durationMs = 80, volume = 0.6f),
                        Tone(frequency = 659.25, durationMs = 90, volume = 0.7f),
                        Tone(frequency = 783.99, durationMs = 180, volume = 0.8f)
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playWrong() {
        scope.launch {
            try {
                // Low pitch error alert tone: Eb4 (311Hz) -> A3 (220Hz)
                playPcmToneSeries(
                    listOf(
                        Tone(frequency = 311.13, durationMs = 110, volume = 0.7f),
                        Tone(frequency = 220.00, durationMs = 220, volume = 0.85f)
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun playPcmClick() {
        val sampleRate = 22050
        val durationMs = 12
        val numSamples = sampleRate * durationMs / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = 1.0 - (i.toDouble() / numSamples)
            val wave = sin(2.0 * Math.PI * 1400.0 * t) * envelope
            buffer[i] = (wave * Short.MAX_VALUE * 0.35).toInt().toShort()
        }
        playRawBuffer(buffer, sampleRate)
    }

    private data class Tone(val frequency: Double, val durationMs: Int, val volume: Float = 0.7f)

    private fun playPcmToneSeries(tones: List<Tone>) {
        val sampleRate = 22050
        var totalSamples = 0
        tones.forEach { totalSamples += sampleRate * it.durationMs / 1000 }
        val buffer = ShortArray(totalSamples)

        var offset = 0
        tones.forEach { tone ->
            val count = sampleRate * tone.durationMs / 1000
            for (i in 0 until count) {
                val t = i.toDouble() / sampleRate
                val attack = (i.toDouble() / (sampleRate * 0.008)).coerceAtMost(1.0)
                val release = ((count - i).toDouble() / (sampleRate * 0.025)).coerceAtMost(1.0)
                val envelope = attack * release
                val wave = sin(2.0 * Math.PI * tone.frequency * t) * envelope
                buffer[offset + i] = (wave * Short.MAX_VALUE * tone.volume).toInt().toShort()
            }
            offset += count
        }
        playRawBuffer(buffer, sampleRate)
    }

    private fun playRawBuffer(buffer: ShortArray, sampleRate: Int) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
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

            val totalDurationMs = (buffer.size.toDouble() / sampleRate * 1000).toLong() + 100
            scope.launch {
                kotlinx.coroutines.delay(totalDurationMs)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
