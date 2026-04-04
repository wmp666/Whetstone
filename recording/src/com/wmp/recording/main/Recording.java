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
                    AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, audioFile);
                    Log.info.print(Recording.class.toString(), "录音已保存至: " + audioFile.getAbsolutePath());
                } catch (IOException e) {
                    Log.err.print(Recording.class, "写入文件失败: " + e.getMessage());
                }
            }, "保存录音");
            recordingThread.start();

        } catch (Exception e) {
            Log.err.print(Recording.class, "无法打开录音设备: " + e.getMessage());
        }
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
