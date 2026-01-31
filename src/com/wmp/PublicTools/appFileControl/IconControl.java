package com.wmp.PublicTools.appFileControl;


import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.UITools.CTColor;
import com.wmp.PublicTools.io.IOForInfo;
import com.wmp.PublicTools.printLog.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class IconControl {
    public static final int COLOR_DEFAULT = 0;
    public static final int COLOR_COLORFUL = 1;

    private static final Map<String, ImageIcon> DEFAULT_IMAGE_MAP = new HashMap<>();
    private static final Map<String, Map<String, ImageIcon>> COLORFUL_IMAGE_MAP = new HashMap<>();

    private static final Map<String, String> ICON_STYLE_MAP = new HashMap<>();

    static {
        DEFAULT_IMAGE_MAP.put("系统.图标", new ImageIcon(IconControl.class.getResource(CTInfo.iconPath)));
        COLORFUL_IMAGE_MAP.put("light", DEFAULT_IMAGE_MAP);
        COLORFUL_IMAGE_MAP.put("dark",
                getColorfulImageMap(DEFAULT_IMAGE_MAP, CTColor.getParticularColor("white")));
        COLORFUL_IMAGE_MAP.put("err",
                getColorfulImageMap(DEFAULT_IMAGE_MAP, CTColor.getParticularColor("blue")));
    }

    public static void init() {
        try {
            DEFAULT_IMAGE_MAP.clear();
            COLORFUL_IMAGE_MAP.clear();

            DEFAULT_IMAGE_MAP.put("系统.图标", new ImageIcon(IconControl.class.getResource(CTInfo.iconPath)));

            //获取基础图标
            String resourceInfos = IOForInfo.getInfos(IconControl.class.getResource("imagePath.json"));
            JSONArray resourceJsonArray = new JSONArray(resourceInfos);
            resourceJsonArray.forEach(object -> {

                JSONObject jsonObject = (JSONObject) object;
                Log.info.print("IconControl", String.format("名称:%s|位置:%s", jsonObject.getString("name"), jsonObject.getString("path")));
                String pathStr = jsonObject.getString("path");
                URL path = IconControl.class.getResource(pathStr);
                if (path == null) {
                    Log.warn.print("IconControl", String.format("图标文件%s不存在", jsonObject.getString("path")));
                    DEFAULT_IMAGE_MAP.put(jsonObject.getString("name"),
                            new ImageIcon(IconControl.class.getResource("/image/optionDialogIcon/warn.png")));
                } else {
                    DEFAULT_IMAGE_MAP.put(jsonObject.getString("name"),
                            new ImageIcon(path));
                }
                ICON_STYLE_MAP.put(jsonObject.getString("name"), jsonObject.getString("style"));
            });


        } catch (Exception e) {
            Log.warn.print(null, IconControl.class.getName(), "图片加载失败:\n" + e);
        }


            Map<String, ImageIcon> colorfulImageMap = getColorfulImageMap(DEFAULT_IMAGE_MAP, CTColor.mainColor);
            COLORFUL_IMAGE_MAP.put("light", colorfulImageMap);
            COLORFUL_IMAGE_MAP.put("dark", colorfulImageMap);

    }

    /**
     * 获取彩色图标
     *
     * @param map   原数据
     * @param color 需要更新的颜色
     * @return 修改为目标颜色后的数据
     */
    private static Map<String, ImageIcon> getColorfulImageMap(Map<String, ImageIcon> map, Color color) {
        return map;
    }

    public static String getIconStyle(String name) {
        return ICON_STYLE_MAP.getOrDefault(name, "png");
    }

    public static ImageIcon getDefaultIcon(String name) {
        return DEFAULT_IMAGE_MAP.getOrDefault(name,
                DEFAULT_IMAGE_MAP.get("default"));
    }

    public static ImageIcon getColorfulIcon(String name) {

        HashMap<String, ImageIcon> defaultMap = new HashMap<>();
        defaultMap.put("default", DEFAULT_IMAGE_MAP.get("default"));
        return COLORFUL_IMAGE_MAP.getOrDefault(CTColor.style, defaultMap)
                .getOrDefault(name, DEFAULT_IMAGE_MAP.get("default"));
    }


    public static ImageIcon getIcon(String name, int colorStyle) {
        ImageIcon imageIcon = colorStyle == COLOR_DEFAULT ? getDefaultIcon(name) : getColorfulIcon(name);
        if (imageIcon == null) {
            return new ImageIcon(IconControl.class.getResource("/image/default.png"));
        }
        return imageIcon;
    }
}
