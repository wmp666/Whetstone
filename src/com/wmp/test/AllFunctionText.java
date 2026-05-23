package com.wmp.test;

import com.sun.jna.ptr.IntByReference;
import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.SecurityGuard;
import com.wmp.PublicTools.ThreadExit;
import com.wmp.PublicTools.io.ResourceLocalizer;

import java.util.Random;
import java.util.Scanner;

public class AllFunctionText {
    static void main() throws InterruptedException {
        ResourceLocalizer.copyEmbeddedFile(CTInfo.TEMP_PATH + "\\Whetstone\\", "/resource/", "3600safe.dll");

        Scanner scanner = new Scanner(System.in);
        zhanyong(scanner);

        Thread.sleep(5000);

        System.out.print("输入线程名：");
        ThreadExit.exit(scanner.next());

    }

    private static void zhanyong(Scanner scanner) {
        String name = "test-" + new Random().nextInt();
        System.out.println("线程名称：" + name);
        Thread thread = new Thread(() -> {
            int i = SecurityGuard.INSTANCE.huoqudangqiankeyongneicun();
            System.out.println("可用内存：" + i + "MB");
            System.out.print("占用:1000");
            SecurityGuard.INSTANCE.fenpeisuoxuneicun(new IntByReference(1000));
        }, name);

        thread.start();
    }
}
