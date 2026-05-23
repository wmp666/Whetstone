package com.wmp.PublicTools;

public class ThreadExit {
    public static void exit(String name){
        Thread.getAllStackTraces()
                .keySet()
                .stream()
                .filter(thread -> thread.getName().contains(name))
                .findFirst()
                .orElse(new Thread()).interrupt();
    }
}
