package com.wmp.test;


import com.wmp.PublicTools.windowsAPI.BlurGlassEffect;
import com.wmp.PublicTools.windowsAPI.DesktopAppEnumerator;
import com.wmp.PublicTools.windowsAPI.DisableGlassEffect;

import java.util.List;

public class WindowsAPITest {
    public static void main(String[] args) throws InterruptedException {

        {
            List<DesktopAppEnumerator.WindowInfo> windowInfoList = DesktopAppEnumerator.getVisibleWindows();
            for (DesktopAppEnumerator.WindowInfo windowInfo : windowInfoList) {
                System.out.println(windowInfo.title);
                BlurGlassEffect.setWindowLayered(windowInfo.hwnd);
                BlurGlassEffect.enableDwmGlassEffect(windowInfo.hwnd);
            }
        }

        Thread.sleep(5000);

        {
            List<DesktopAppEnumerator.WindowInfo> windowInfoList = DesktopAppEnumerator.getVisibleWindows();
            for (DesktopAppEnumerator.WindowInfo windowInfo : windowInfoList) {
                DisableGlassEffect.disableAllGlassEffects(windowInfo.hwnd);
            }
        }

    }


}
