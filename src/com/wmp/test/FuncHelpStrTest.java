package com.wmp.test;

import com.wmp.PublicTools.easter_egg_control.FuncHelpUnit;

import java.util.Arrays;

public class FuncHelpStrTest {
    static void main() {
        String s = "data_callback|void data_callback(ma_device* pDevice, void* pOutput, const void* pInput, ma_uint32 frameCount)音频数据回调函数，一般不用调用;" +
                "playsound|int playsound(const wchar_t* path)传入绝对路径" +
                "";
        String[] split = s.split(";");
        FuncHelpUnit[] funcHelpUnits = new FuncHelpUnit[split.length];
        for (int i = 0; i < split.length; i++) {
            String[] split1 = split[i].split("\\|");
            funcHelpUnits[i] = new FuncHelpUnit(split1[0], split1[1]);
        }
        System.out.println(Arrays.toString(funcHelpUnits));
    }
}
