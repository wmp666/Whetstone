package com.wmp.test;

import com.sun.jna.WString;
import com.wmp.PublicTools.DrawingRights;

public class RunAsAdminTest {
    static void main() {
        System.out.println(DrawingRights.INSTANCE.RunAsAdmin(new WString("C:\\Windows\\System32\\cmd.exe"), new WString("")));
        System.out.println(DrawingRights.INSTANCE.RunAsAdmin(new WString("C:\\Windows\\System32\\cmd.exe"), new WString("")));
        System.out.println(DrawingRights.INSTANCE.RunAsAdmin(new WString("C:\\Windows\\System32\\cmd.exe"), new WString("")));
    }
}
