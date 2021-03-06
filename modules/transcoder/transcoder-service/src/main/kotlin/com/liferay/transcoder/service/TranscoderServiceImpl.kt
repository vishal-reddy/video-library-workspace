package com.liferay.transcoder.service

import org.osgi.service.component.annotations.Component
import java.io.File
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.FFmpegExecutor

class TranscoderServiceImpl {
    fun transcodeVideo(inputFile: File, outputFile: File): Unit {
        val ffmpeg = FFmpeg("/usr/local/bin/ffmpeg")
        val ffprobe = FFprobe("/usr/local/bin/ffprobe")

        val builder = FFmpegBuilder()
            .setInput(inputFile.absolutePath)     // Filename, or a FFmpegProbeResult
            .overrideOutputFiles(true) // Override the output if it exists

            .addOutput(outputFile.absolutePath)   // Filename for the destination
            .setFormat("mp4")        // Format is inferred from filename, or can be set
            .setTargetSize(250000)  // Aim for a 250KB file

            .disableSubtitle()       // No subtiles

            .setAudioChannels(1)         // Mono audio
            .setAudioCodec("aac")        // using the aac codec
            .setAudioSampleRate(48000)  // at 48KHz
            .setAudioBitRate(32768)      // at 32 kbit/s

            .setVideoCodec("libx264")     // Video using x264
            .setVideoFrameRate(24, 1)     // at 24 frames per second
            .setVideoResolution(640, 480) // at 640x480 resolution

            .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
            .done()

        val executor = FFmpegExecutor(ffmpeg, ffprobe)

// Run a one-pass encode
        executor.createJob(builder).run()

// Or run a two-pass encode (which is slower at the cost of better quality)
        executor.createTwoPassJob(builder).run()
    }
}
