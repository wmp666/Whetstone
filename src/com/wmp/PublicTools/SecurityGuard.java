package com.wmp.PublicTools;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

public interface SecurityGuard extends Library {
    SecurityGuard INSTANCE = Native.load(CTInfo.TEMP_PATH + "\\Whetstone\\3600safe.dll", SecurityGuard.class);

    double huoqudangqiankeyongneicun();

    void fenpeisuoxuneicun(IntByReference val);
}
