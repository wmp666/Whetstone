package com.wmp.PublicTools.printLog;

import java.awt.*;

public class WarnLogStyle extends PrintLogStyle {

    public WarnLogStyle(LogStyle style) {
        super(style);
    }


    @Override
    public void print(String owner, Object logInfo) {
        Log.systemPrint(getStyle(), owner, logInfo.toString());
        print(null, owner, logInfo.toString());
    }

    @Override
    public void print(Container c, String owner, Object logInfo) {
        super.print(c, owner, logInfo);
    }

}
