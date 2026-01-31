package com.wmp.PublicTools.printLog;

public class InfoLogStyle extends PrintLogStyle {


    public InfoLogStyle(LogStyle style) {
        super(style);
    }
    public void systemPrint(String owner, String logInfo) {
        Log.systemPrint(LogStyle.INFO, owner, logInfo);
    }

}
