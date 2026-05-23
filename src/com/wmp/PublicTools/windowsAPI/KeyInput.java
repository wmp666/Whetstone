package com.wmp.PublicTools.windowsAPI;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

public record KeyInput(byte keyCode, int flags){
    public WinUser.INPUT toInput(){
        WinUser.INPUT input = new WinUser.INPUT();
        input.input.setType("ki");
        input.input.ki.wVk = new WinDef.WORD(keyCode);
        input.input.ki.dwFlags = new WinDef.DWORD(flags);
        return input;
    }
}
