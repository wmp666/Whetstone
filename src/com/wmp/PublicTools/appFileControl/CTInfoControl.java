package com.wmp.PublicTools.appFileControl;

import java.io.File;

/**
 * 用于管理各个组件的数据获取的标准类工具,在获取数据时会从缓存中获取(除非没有),可以通过refresh方法刷新缓存数据,
 * refresh方法需要用户自行实现,刷新缓存数据时请返回新的数据,可用于直接获取最新的数据
 */
public abstract class CTInfoControl<T> {
    private T info;

    public abstract File getInfoBasicFile();

    public final T getInfo() {
        if (info == null) {
            info = refresh();
        }
        return info;
    }

    public abstract void setInfo(T t);

    /**
     * 刷新缓存数据
     *
     * @return 新的数据
     */
    public final T refresh() {
        return info = refreshInfo();
    }

    protected abstract T refreshInfo();
}
