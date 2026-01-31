package com.wmp.PublicTools.io;

import com.wmp.PublicTools.printLog.Log;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class IOForInfo {

    private final File file;

    public IOForInfo(File file) {
        this.file = file;
    }

    public IOForInfo(String file) {
        this.file = new File(file);
    }

    public IOForInfo(URI file) {
        this.file = new File(file);
    }

    public static String[] getInfo(String file) {
        String infos = getInfos(file);
        if (infos.equals("err")) {
            return new String[]{"err"};
        }
        return infos.split("\n");
    }

    public static String[] getInfo(URL file) {
        String infos = getInfos(file);
        if (infos.equals("err")) {
            return new String[]{"err"};
        }
        return infos.split("\n");
    }

    public static String getInfos(String file) {
        try {
            File file1 = new File(file);
            if (!file1.exists()) return "err";
            return getInfos(file1.toURI().toURL());
        } catch (MalformedURLException e) {
            Log.err.print(IOForInfo.class, file + "文件读取失败", e);
            return "err";
        }
    }

    public static String getInfos(URL file) {
        try { // 明确指定编码
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            file.openStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                if (line.startsWith("#")) {
                    continue;
                }
                if (line.isEmpty()) {
                    continue;
                }
                sb.append(line).append("\n");
            }
            Log.info.print("IOForInfo-获取数据", "数据内容: " + sb);
            return sb.toString();// 读取第一行
        } catch (IOException e) {
            Log.err.print(IOForInfo.class, file.getPath() + "文件读取失败", e);
        }
        return null;
    }

    public String[] getInfo() throws IOException {
        String s = getInfos();
        if (s.equals("err")) {
            return new String[]{"err"};
        }
        return s.split("\n");
    }

    public void setInfo(String... infos) throws IOException {

        if (!file.exists()) {
            if (!creativeFile(file)) {
                Log.err.print(IOForInfo.class, file.getPath() + "文件无法创建");
                return;
            }
        }

        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8)) {// 明确指定编码
            Log.info.print("IOForInfo-设置数据", file.getPath() + "文件开始写入");


            String inf = String.join("\n", infos);
            Log.info.print("IOForInfo-设置数据", "数据内容: " + inf);
            writer.write(inf);
            writer.flush();

            // 验证部分也需要使用UTF-8读取
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), StandardCharsets.UTF_8))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
            }
        } catch (IOException e) {
            Log.err.print(IOForInfo.class, file.getPath() + "文件写入失败", e);
        }
    }

    public String getInfos() throws IOException {
        if (!file.exists()) {
            if (!creativeFile(file)) {
                Log.err.print(IOForInfo.class, file.getPath() + "文件无法创建");
                return "err";
            }
        }


        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file), StandardCharsets.UTF_8))) { // 明确指定编码

            Log.info.print("IOForInfo-获取数据", file.getPath() + "文件开始读取");
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            String s = "";
            if (!content.isEmpty()) {
                //去除文字中的空格
                s = content.deleteCharAt(content.length() - 1).toString().trim();
            }

            String replace = s.replace("\n", "\\n");

            Log.info.print("IOForInfo-获取数据", "数据内容: " + replace);
            Log.info.print("IOForInfo-获取数据", file.getPath() + "文件读取完成");
            reader.close();
            return !s.isEmpty() ? s : "err";
        } catch (IOException e) {
            Log.err.print(IOForInfo.class, file.getPath() + "文件读取失败", e);
            return "err";
        }
    }

    private boolean creativeFile(File file) throws IOException {
        Log.info.print("IOForInfo-创建文件", file.getPath() + "文件创建");
        file.getParentFile().mkdirs();
        return file.createNewFile();
    }

    @Override
    public String toString() {

        try {
            return "IOForInfo{" +
                    "file=" + file +
                    " Inf=" + Arrays.toString(getInfo()) +
                    '}';
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
