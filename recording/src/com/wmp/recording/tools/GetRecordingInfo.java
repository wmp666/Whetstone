package com.wmp.recording.tools;

import com.wmp.PublicTools.printLog.Log;
import com.wmp.recording.main.Recording;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;

public class GetRecordingInfo {
    /**
     * 枚举所有 Mixer，筛选出支持 TargetDataLine（输入）的设备
     */
    public static List<Mixer.Info> enumerateInputDevices() {
        // 定义音频格式
        AudioFormat format = new AudioFormat(Recording.SAMPLE_RATE, Recording.SAMPLE_SIZE_IN_BITS,
                Recording.CHANNELS, Recording.SIGNED, Recording.BIG_ENDIAN);

        List<Mixer.Info> result = new ArrayList<>();
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        Log.info.print(GetRecordingInfo.class.toString(), "正在检测录音设备...");
        for (Mixer.Info info : mixers) {
            Mixer mixer = AudioSystem.getMixer(info);
            // 检查是否支持 TargetDataLine
            DataLine.Info lineInfo = new DataLine.Info(TargetDataLine.class, format);
            if (mixer.isLineSupported(lineInfo)) {
                result.add(info);
                Log.info.print(GetRecordingInfo.class.toString(), "发现录音设备: " + info.getName());
            }
        }
        if (result.isEmpty()) {
            Log.err.print(GetRecordingInfo.class, "未发现任何录音设备。");
        }
        return result;
    }


}
