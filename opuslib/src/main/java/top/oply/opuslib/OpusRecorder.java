package top.oply.opuslib;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private AudioRecord audioRecord;
    private Thread recordingThread = new Thread();
    private final OpusTool opusTool = new OpusTool();
    private int bufferSize = 0;
    private String filePath = null;
    private final ByteBuffer fileBuffer = ByteBuffer.allocateDirect(1920);// Should be 1920, to accord with function writeFreme()

    class RecordThread implements Runnable {
        public void run() {
            writeAudioDataToFile();
        }
    }

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
     * @return a runnable that can be run to stop recording
     * @throws IllegalArgumentException if we were unable to initialize an AudioRecord using the supplied parameters
     */
    public static Runnable startRecording(final String file, final OpusApplication application, final int sampleRate, final int bitRate, final boolean stereo, EffectsInitializer effectsInitializer) throws IllegalArgumentException {
        final OpusRecorder recorder = new OpusRecorder(file, application.code, sampleRate, bitRate, stereo, effectsInitializer);
        return new Runnable() {
            @Override
            public void run() {
                recorder.stopRecording();
            }
        };
    }

    private OpusRecorder(final String file, final int application, final int sampleRate, final int bitRate, final boolean stereo, EffectsInitializer effectsInitializer) throws IllegalArgumentException {
        final int channels = stereo ? AudioFormat.CHANNEL_IN_STEREO : AudioFormat.CHANNEL_IN_MONO;
        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channels, AUDIO_FORMAT);
        bufferSize = (minBufferSize / 1920 + 1) * 1920;
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channels, AUDIO_FORMAT, bufferSize);
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            throw new IllegalArgumentException("AudioRecord failed to initialize. Usually this means that one of the configuration arguments was incorrect.");
        }

        if (effectsInitializer != null) {
            effectsInitializer.init(audioRecord.getAudioSessionId());
        }

        audioRecord.startRecording();
        filePath = file;
        int rst = opusTool.startRecording(filePath, application, sampleRate, bitRate, stereo ? 2 : 1);
        if (rst != 1) {
            Log.e(TAG, "error initializing opus recorder");
            return;
        }

        recordingThread = new Thread(new RecordThread(), "OpusRecorder record");
        recordingThread.start();
    }

    private void writeAudioDataToOpus(ByteBuffer buffer, int size) {
        ByteBuffer finalBuffer = ByteBuffer.allocateDirect(size);
        finalBuffer.put(buffer);
        finalBuffer.rewind();
        boolean flush = false;

        //write data to Opus file
        while (recording.get() && finalBuffer.hasRemaining()) {
            int oldLimit = -1;
            if (finalBuffer.remaining() > fileBuffer.remaining()) {
                oldLimit = finalBuffer.limit();
                finalBuffer.limit(fileBuffer.remaining() + finalBuffer.position());
            }
            fileBuffer.put(finalBuffer);
            if (fileBuffer.position() == fileBuffer.limit() || flush) {
                int length = !flush ? fileBuffer.limit() : finalBuffer.position();

                int rst = opusTool.writeFrame(fileBuffer, length);
                if (rst != 0) {
                    fileBuffer.rewind();
                }
            }
            if (oldLimit != -1) {
                finalBuffer.limit(oldLimit);
            }
        }
    }

    private void writeAudioDataToFile() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);

        while (recording.get()) {
            buffer.rewind();
            int len = audioRecord.read(buffer, bufferSize);
            if (len != AudioRecord.ERROR_INVALID_OPERATION) {
                try {
                    writeAudioDataToOpus(buffer, len);
                } catch (Exception e) {
                    e.printStackTrace();
                    stopRecording();
                }
            }

        }
    }

    synchronized private void stopRecording() {
        if (!recording.compareAndSet(true, false)) return;

        try {
            Thread.sleep(200);
        } catch (InterruptedException ie) {
            Log.d(TAG, "Interrupted during final sleep, ignoring");
        }

        if (null != audioRecord) {
            opusTool.stopRecording();
            recordingThread = null;
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }
}
