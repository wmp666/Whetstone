# 示例代码
> 版本：2.5.0

## Java

### 彩蛋文件

```java
package com.wmp.whetstone;

public class EasterEggUnit{

    
    public String getID() {
        return "彩蛋ID";
    }

    
    public String getVersion() {
        return "x.x.x";
    }

    
    public String getTargetVersion() {
        return "x.x.x";//写入菜单开发时的磨刀石开发版本号
    }

    
    public String help() {
        return "整个文件的简要帮助";
    }
    
    public FuncHelpUnit[] funcHelps() {
        return new FuncHelpUnit[]{
                new FuncHelpUnit("example(方法名)(一定要与需要调用的方法名称一致)", "该方法的用法 + 参数作用(按顺序列)")
                //可以写入更多方法帮助
        };
    }

    public void example(String[] args){
        //会被调用的彩蛋方法
        //可以将用户输入的字符串转换为需要的参数
    }

    

}

```

## C++(dll)

### 彩蛋头文件

```cpp
#pragma once
#include<iostream>
#include <windows.h>
#include <psapi.h> 
// 如果定义了 MYDLL_EXPORTS，说明正在编译 DLL，函数需要导出
#ifdef MYDLL_EXPORTS
#define MYDLL_API __declspec(dllexport)
#else
#define MYDLL_API __declspec(dllimport)
#endif
// 加上 extern "C" 防止 C++ 名称修饰，让函数名更干净
extern "C" MYDLL_API void example();//在括号内写入需要的参数(只能是映射表中的)
extern "C" MYDLL_API extern "C" __declspec(dllexport) const char* getID();
extern "C" MYDLL_API extern "C" __declspec(dllexport) const char* getVersion();
extern "C" MYDLL_API extern "C" __declspec(dllexport) const char* help();
extern "C" MYDLL_API extern "C" __declspec(dllexport) const char* funcHelps();
```

### 彩蛋文件

```cpp
#include"pch.h"
#include"函数声明.hpp"

//example仅为方法名,可以更改
void example() {//在括号内写入需要的参数(只能是映射表中的)
    //写入实现
}
extern "C" __declspec(dllexport) const char* getID()
{
    return "彩蛋ID";
}
extern "C" __declspec(dllexport) const char* getVersion()
{
    return "x.x.x";
}
extern "C" __declspec(dllexport) const char* help()
{
    return "整个文件的简要帮助";
}
extern "C" __declspec(dllexport) const char* funcHelps()
{
    return"方法名|方法帮助 + 参数作用;方法二|...;...";
}
```

## 配置文件

### 彩蛋单元

#### Jar
> 代码中`more`表示该可以放下更多数据,不是使用时需要的参数
```json
{"id": "...", "funcName":"JAR中方法名", "func": "要使用功能", "args": ["value", "more"]}
```
#### DLL
```json
{"id": "dll:...", "funcName":"DLL中方法名", "func": "要使用功能", "args": ["style:value", "more"]}
```

#### 变量
```json
{"id": "var:变量名"}
```

### 彩蛋变量文件(var_list.json)
> 代码中`help`表示该功能帮助,不是使用时需要的参数
```json
{
  "变量名": {"help": "彩蛋单元"},
  "help": "还能放入更多"
}
```

### 彩蛋-课程映射文件(start_list.json)
> 代码中`help`表示该功能帮助,不是使用时需要的参数
```json
{
  "app_start": [{"help":"放入彩蛋单元,此处用于程序刚启动时启动"}],
  "class_start": [{"help":"放入彩蛋单元,此处用于课程刚开始时启动"}],
  "class_list": {
    "课程名": [{"help":"放入彩蛋单元,此处用于指定课程刚开始时启动"}]
  }
}
```

### 程序数据配置文件(settings.properties)

```properties
password = 加密后的密码数据
trayIconNum = 托盘图标数(10)
recordingDirNum = 录音最大存储量(3)
serverPort = 端口(8888)
```