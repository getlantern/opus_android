package top.oply.opuslib;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by young on 2015/7/2.
 */
public class OpusRecorder {
    private static final String TAG = OpusRecorder.class.getName();
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    public enum OpusApplication {
        /**
         * Encoding optimized for speech intelligibility
         */
        VOIP(2048),
        AUDIO(2049);

        public final int code;

        OpusApplication(int code) {
            this.code = code;
        }
    }

    /**
     * Encoding optimized for speech intelligibility
     */
    public static int OPUS_APPLICATION_VOIP = 2048;

    /**
     * Encoding optimized for music quality
     */
    public static int OPUS_APPLICATION_AUDIO = 2049;

    private final AtomicBoolean recording = new AtomicBoolean(true);
    private final AtomicLong started = new AtomicLong();
    private AudioRecord audioRecord;
    private final OpusTool opusTool = new OpusTool();
    private final long recordLimitMillis;
    private final int chunkSize;
    private final int frameSize;
    private final String filePath;
    private final Thread readWriteAndEncodeThread;

    public interface EffectsInitializer {
        void init(int audioSessionId);
    }

    /**
     * Starts recording audio from the system microphone to the given file.
     *
     * @param file               path to file that will contain the Opus encoded audio data
     * @param application        determines what kind of encoding to use
     * @param sampleRate         sample rate at which to record
     * @param bitRate            bit rate at which to encode Opus
     * @param stereo             if true records in stereo, if false in mono
     * @param effectsInitializer optional hook for initializing audio effects on the audio session
     * @param recordLimitMillis  limit for how long to record, in milliseconds. This is a safeguard against accidentally continuing to record if client fails to call stopRecording.
     * @return a runnable that can be run to stop recording
     * @throws IllegalArgumentException if we were unable to initialize an AudioRecord using the supplied parameters
     */
    public static Runnable startRecording(final String file, final OpusApplication application, final int sampleRate, final int bitRate, final boolean stereo, long recordLimitMillis, EffectsInitializer effectsInitializer) throws IllegalArgumentException {
        final OpusRecorder recorder = new OpusRecorder(file, application.code, sampleRate, bitRate, stereo, recordLimitMillis, effectsInitializer);
        return new Runnable() {
            @Override
            public void run() {
                recorder.stopRecording();
            }
        };
    }

    private OpusRecorder(final String file, final int application, final int sampleRate, final int bitRate, final boolean stereo, long recordLimitMillis, EffectsInitializer effectsInitializer) throws IllegalArgumentException {
        this.recordLimitMillis = recordLimitMillis;
        int defaultFrameSize = getDefaultFrameSize(sampleRate);
        int channels = stereo ? 2 : 1;
        chunkSize = defaultFrameSize * channels * 2;
        frameSize = chunkSize / 2 / channels;

        final int audioRecordChannels = stereo ? AudioFormat.CHANNEL_IN_STEREO : AudioFormat.CHANNEL_IN_MONO;
        int bufferSize = 4 * AudioRecord.getMinBufferSize(sampleRate, audioRecordChannels, AUDIO_FORMAT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, audioRecordChannels, AUDIO_FORMAT, bufferSize);
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            throw new IllegalArgumentException("AudioRecord failed to initialize. Usually this means that one of the configuration arguments was incorrect.");
        }

        if (effectsInitializer != null) {
            effectsInitializer.init(audioRecord.getAudioSessionId());
        }

        started.set(System.currentTimeMillis());
        audioRecord.startRecording();
        filePath = file;

        int rst = opusTool.startRecording(filePath, application, sampleRate, bitRate, frameSize, channels);
        if (rst != 1) {
            throw new RuntimeException("error initializing opus recorder");
        }

        readWriteAndEncodeThread = new Thread(readWriteAndEncode, "OpusRecorder recording");
        readWriteAndEncodeThread.start();
    }

    private int getDefaultFrameSize(int sampleRate) {
        switch (sampleRate) {
            case 8000:
                return 160;
            case 12000:
                return 240;
            case 16000:
                return 160;
            case 24000:
                return 240;
            case 48000:
                return 120;
            default:
                throw new IllegalArgumentException(String.format("Sample rate %1$s not support, please specify a rate of 8000, 12000, 16000, 24000 or 48000", sampleRate));
        }
    }

    private Runnable readWriteAndEncode = new Runnable() {
        @Override
        public void run() {
            while (recording.get()) {
                ByteBuffer frame = ByteBuffer.allocateDirect(chunkSize);
                int offset = 0;
                int remaining = chunkSize;
                while (remaining > 0 && recording.get()) {
                    ByteBuffer slice = frame.slice();
                    slice.position(offset);
                    int read = audioRecord.read(slice, remaining);
                    offset += read;
                    remaining -= read;
                }
                if (remaining <= 0) {
                    opusTool.writeFrame(frame, chunkSize);
                }

                if (System.currentTimeMillis() - started.get() > recordLimitMillis) {
                    stopRecording();
                }
            }
        }
    };

    synchronized private void stopRecording() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException ie) {
            Log.d(TAG, "Interrupted during final sleep, ignoring");
        }

        if (!recording.compareAndSet(true, false)) return;

        try {
            readWriteAndEncodeThread.join(1000);
        } catch (InterruptedException ie) {
            Log.d(TAG, "Interrupted while waiting for readWriteAndEncode to finish, ignoring");
        }

        opusTool.stopRecording();
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
    }
}
