package com.wmp.whetstone;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.HelpDoc;
import com.wmp.PublicTools.easter_egg_control.easterEggUnit.BasicEasterEggUnit;
import com.wmp.PublicTools.printLog.Log;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Receiver {
    public static void initSever(int port){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Log.info.print(Receiver.class.toString(), "服务器已启动，等待客户端传入数据...");
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    // 获取输入流
                    InputStream input = socket.getInputStream();
                    // 读取长度前缀（4字节大端整数）
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    int length = dis.readInt();          // 读取长度
                    byte[] data = new byte[length];
                    dis.readFully(data);                 // 读取完整消息
                    String msg = new String(data, StandardCharsets.UTF_8);

                    Log.info.print(Receiver.class.toString(), "接收到数据：" + msg);
                    runCommand(msg);
                } catch (Exception e) {
                    Log.err.print(Receiver.class, "接收数据失败", e);
                }
            }
        } catch (Exception e) {
            Log.err.print(Receiver.class, "服务器启动失败", e);
        }
    }

    public static void runCommand(String command) {
        if (command.startsWith("help")) {
            HelpDoc.help();
        } else if (command.startsWith("run:EE:")) {
            String[] list = command.substring(7).split(";");

            CTInfo.easterEggUnits.stream().filter(easterEggUnit ->
                    list[0].equals(easterEggUnit.getID())).forEach(easterEggUnit -> {
                easterEggUnit.run(Arrays.copyOfRange(list, 1, list.length));
            });
        } else if (command.startsWith("clear:EE:")) {
            String id = command.substring(9);
            CTInfo.easterEggUnits.stream().filter(easterEggUnit ->
                    id.equals(easterEggUnit.getID())).forEach(BasicEasterEggUnit::clear);
        } else if (command.startsWith("exit")){
            System.exit(0);
        }else {
            Log.warn.print(Receiver.class.toString(), "无效的命令");
        }
    }
}
