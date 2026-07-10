package com.wmp.whetstone;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.HelpDoc;
import com.wmp.PublicTools.printLog.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Receiver {
    // ========== 新增全局队列和客户端列表 ==========
    private static final BlockingQueue<String> OUT_QUEUE = new LinkedBlockingQueue<>();
    private static final List<Socket> CLIENTS = new CopyOnWriteArrayList<>(); // 线程安全

    // ========== 重定向 System.out ==========
    static {
        redirectSystemOut();
        redirectSystemErr();
    }

    private static void redirectSystemOut() {
        PrintStream originalOut = System.out;
        PrintStream customOut = new PrintStream(new ByteArrayOutputStream()) {
            @Override
            public void println(String x) {
                // 直接放入队列（保留原始字符串）
                try { OUT_QUEUE.put(x); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                // 同时输出到控制台
                originalOut.println(x);
            }

            @Override
            public void print(String x) {
                try { OUT_QUEUE.put(x); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                originalOut.print(x);
            }

            // 其他重载如 println(Object)、println(char[]) 等可类似处理，或委托给 String.valueOf
            @Override
            public void println(Object x) {
                println(String.valueOf(x));
            }
            // 为完整，可覆盖 print(Object)、print(char[]) 等，但通常够用
        };
        System.setOut(customOut);
    }

    private static void redirectSystemErr() {
        PrintStream originalErr = System.err;

        PrintStream customErr = new PrintStream(new ByteArrayOutputStream()) {
            @Override
            public void println(String x) {
                if (x != null) {
                    try {
                        // 加入 [ERR] 前缀，便于客户端识别
                        OUT_QUEUE.put("[ERR] " + x);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                originalErr.println(x); // 仍然打印到控制台
            }

            @Override
            public void println(Object x) {
                println(String.valueOf(x));
            }

            @Override
            public void println(char[] x) {
                println(new String(x));
            }

            @Override
            public void println(int x) {
                println(Integer.toString(x));
            }

            // 其他基本类型（long, float, double, boolean）按需添加，逻辑同上

            @Override
            public void print(String x) {
                if (x != null) {
                    try {
                        OUT_QUEUE.put(x);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                originalErr.print(x);
            }

            @Override
            public void print(Object x) {
                print(String.valueOf(x));
            }

            @Override
            public void print(char[] x) {
                print(new String(x));
            }

            // 对于 write(byte[], int, int)，大多数框架不会用它来输出日志文本，直接透传即可
            @Override
            public void write(byte[] buf, int off, int len) {
                originalErr.write(buf, off, len);
            }
        };
        System.setErr(customErr);
    }

    // ========== 服务端初始化 ==========
    public static void initSever(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Thread.ofVirtual().name("Server Creative Thread")
                    .start(()->{
                        Log.info.print(Receiver.class.toString(), "服务器已启动，等待客户端传入数据...");

                        // 主线程持续接收连接
                        while (true) {
                            try {
                                Socket socket = serverSocket.accept();

                                // 配置 Socket 超时和选项
                                socket.setSoTimeout(300000); // 5min超时
                                socket.setKeepAlive(true);

                                CLIENTS.add(socket);  // 添加到全局列表

                                Log.info.print(Receiver.class.getSimpleName(), "新客户端连接: " + socket.getRemoteSocketAddress());

                                // 启动读取线程（处理命令）
                                Thread.ofVirtual().name("Reader-" + socket.getPort()).start(() -> {
                                    DataInputStream dis = null;
                                    try {
                                        dis = new DataInputStream(socket.getInputStream());
                                        while (true) {
                                            // 读取消息长度并校验
                                            int length = dis.readInt();
                                            if (length <= 0 || length > 1048576) { // 最大1MB
                                                Log.warn.print(Receiver.class.getSimpleName(),
                                                        "无效消息长度: " + length + "，关闭连接: " + socket.getRemoteSocketAddress());
                                                break;
                                            }

                                            byte[] data = new byte[length];
                                            dis.readFully(data);
                                            String msg = new String(data, StandardCharsets.UTF_8);
                                            Log.info.print(Receiver.class.getSimpleName(), "接收到数据：" + msg);

                                            // 执行命令（可能产生 System.out 输出）
                                            Thread.startVirtualThread(() -> runCommand(msg));
                                        }
                                    } catch (EOFException e) {
                                        // 客户端正常断开
                                        Log.info.print(Receiver.class.getSimpleName(), "客户端断开: " + socket.getRemoteSocketAddress());
                                    } catch (SocketTimeoutException e) {
                                        Log.warn.print(Receiver.class.getSimpleName(), "连接超时: " + socket.getRemoteSocketAddress());
                                    } catch (Exception e) {
                                        Log.err.print(Receiver.class, "读取客户端数据失败", e);
                                    } finally {
                                        // 移除已断开的 Socket
                                        CLIENTS.remove(socket);
                                        try {
                                            if (dis != null) dis.close();
                                            socket.close();
                                        } catch (IOException ignored) {}
                                    }
                                });

                                // 启动发送线程（从队列取消息，推送给该客户端）
                                Thread.ofVirtual().name("Writer-" + socket.getPort()).start(() -> {
                                    try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
                                        while (!socket.isClosed()) {
                                            String msg = OUT_QUEUE.take(); // 阻塞等待
                                            byte[] data = msg.getBytes(StandardCharsets.UTF_8); // 明确指定 UTF-8
                                            dos.writeInt(data.length);      // 4字节长度前缀

                                            dos.write(data);                // 字节内容
                                            dos.flush();                    // 确保立即发出
                                        }
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                    } catch (Exception e) {
                                        Log.err.print(Receiver.class, "发送数据失败", e);
                                    } finally {
                                        CLIENTS.remove(socket);
                                        try { socket.close(); } catch (IOException ignored) {}
                                    }
                                });
                            } catch (Exception e) {
                                // 短暂休眠避免快速重试消耗资源
                                try { Thread.sleep(100); } catch (InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                    break;
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            Log.err.print(Receiver.class, "服务器启动失败", e);
        }
    }

    // ========== 原有命令处理 ==========
    public static void runCommand(String command) {
        if (command.startsWith("help")) {
            HelpDoc.help();
        } else if (command.startsWith("run:EE:")) {
            String[] list = command.substring(7).split(";");

            CTInfo.easterEggUnits.stream().filter(easterEggUnit ->
                    list[0].equals(easterEggUnit.getID())).forEach(easterEggUnit -> {
                        if (list.length < 4) {
                            String[] args = Arrays.copyOf(Arrays.copyOfRange(list, 1, list.length), 3);
                            for (int i = 0; i < args.length; i++) {
                                if (args[i] == null) {
                                    args[i] = "";
                                }
                            }
                            easterEggUnit.run(args);
                        } else {
                            easterEggUnit.run(Arrays.copyOfRange(list, 1, list.length));
                        }
                    }
            );
        } else if (command.startsWith("clear:EE:")) {
            String[] EEInfo = command.substring(9).split(";", 2);
            CTInfo.easterEggUnits.stream().filter(easterEggUnit ->
                    EEInfo[0].equals(easterEggUnit.getID())).forEach(basicEasterEggUnit -> basicEasterEggUnit.clear(EEInfo[1]));
        } else if (command.startsWith("refresh")) {
            CTInfo.init();
        } else if (command.startsWith("exit")) {
            Log.exit(0);
        } else {
            Log.warn.print(Receiver.class.toString(), "无效的命令");
        }
    }
}