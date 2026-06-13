package com.wmp.test;

import com.wmp.PublicTools.easter_egg_control.EasterEggControl;

public class JugeVersionTest {
    static void main() {
        System.out.println(EasterEggControl.judgeVersion("2.0.2", "2.1.0"));
        System.out.println(EasterEggControl.isCompatible("2.0.2", "2.1.0"));
    }
}
