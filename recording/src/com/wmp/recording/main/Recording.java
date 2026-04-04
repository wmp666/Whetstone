package com.wmp.recording.main;

import com.wmp.PublicTools.printLog.Log;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Recording {
    // 音频格式参数
    public static final float SAMPLE_RATE = 22050;
    public static final int SAMPLE_SIZE_IN_BITS = 8;
    public static final int CHANNELS = 1;
    public static final boolean SIGNED = true;
    public static final boolean BIG_ENDIAN = false;

    // 降噪参数
    private static final float NOISE_THRESHOLD = 0.02f;
    private static final float LOWPASS_CUTOFF = 0.3f;


    public static void recording(File audioFile, Info info){
        audioFile.getParentFile().mkdirs();

        // 获取该 Mixer 对应的 TargetDataLine
        try {

            // 开始录音
            info.line.start();
            Log.info.print(Recording.class.toString(), "录音中...");

            // 创建线程保存音频
            Thread recordingThread = new Thread(() -> {
                try (AudioInputStream audioStream = new AudioInputStream(info.line)) {
                    byte[] audioData = audioStream.readAllBytes();
                    byte[] processedData = applyNoiseReduction(audioData);

                    AudioFormat format = audioStream.getFormat();
                    AudioInputStream processedStream = new AudioInputStream(
                            new java.io.ByteArrayInputStream(processedData),
                            format,
                            processedData.length / format.getFrameSize()
                    );

                    AudioSystem.write(processedStream, AudioFileFormat.Type.WAVE, audioFile);
                    Log.info.print(Recording.class.toString(), "录音已保存至：" + audioFile.getAbsolutePath());
                } catch (IOException e) {
                    Log.err.print(Recording.class, "写入文件失败：" + e.getMessage());
                }
            }, "保存录音");
            recordingThread.start();

        } catch (Exception e) {
            Log.err.print(Recording.class, "无法打开录音设备: " + e.getMessage());
        }
    }

    private static byte[] applyNoiseReduction(byte[] audioData) {
        int numSamples = audioData.length / 2;
        double[] samples = new double[numSamples];

        for (int i = 0; i < numSamples; i++) {
            int sampleIndex = i * 2;
            int sample = (audioData[sampleIndex] & 0xFF) | (audioData[sampleIndex + 1] << 8);
            samples[i] = sample / 32768.0;
        }

        double[] filtered = applyLowPassFilter(samples);
        filtered = applyAmplitudeThreshold(filtered);

        byte[] result = new byte[audioData.length];
        for (int i = 0; i < numSamples; i++) {
            int sampleIndex = i * 2;
            short sampleValue = (short) (Math.max(-1.0, Math.min(1.0, filtered[i])) * 32767);
            result[sampleIndex] = (byte) (sampleValue & 0xFF);
            result[sampleIndex + 1] = (byte) ((sampleValue >> 8) & 0xFF);
        }

        return result;
    }

    private static double[] applyLowPassFilter(double[] samples) {
        if (samples.length < 2) return samples;

        double[] filtered = new double[samples.length];
        double alpha = LOWPASS_CUTOFF;

        filtered[0] = samples[0];
        for (int i = 1; i < samples.length; i++) {
            filtered[i] = alpha * samples[i] + (1 - alpha) * filtered[i - 1];
        }

        return filtered;
    }

    private static double[] applyAmplitudeThreshold(double[] samples) {
        for (int i = 0; i < samples.length; i++) {
            if (Math.abs(samples[i]) < NOISE_THRESHOLD) {
                samples[i] = 0.0;
            }
        }
        return samples;
    }

    public static Info create(Mixer.Info mixerInfo){
        // 定义音频格式
        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS,
                CHANNELS, SIGNED, BIG_ENDIAN);

        // 获取该 Mixer 对应的 TargetDataLine
        try {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            DataLine.Info lineInfo = new DataLine.Info(TargetDataLine.class, format);
            if (!mixer.isLineSupported(lineInfo)) {
                Log.err.print(Recording.class, "所选设备不支持指定的音频格式！");
                return null;
            }

            TargetDataLine line = (TargetDataLine) mixer.getLine(lineInfo);
            line.open(format);

            return new Info(line);

        } catch (LineUnavailableException e) {
            Log.err.print(Recording.class, "无法打开录音设备: " + e.getMessage());
        }
        return null;
    }

    public static void stop(Info info){
        info.line.stop();
        info.line.close();
    }

    public record Info(TargetDataLine line){

    }

    static void main() {


    }


}
