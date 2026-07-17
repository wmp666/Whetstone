[![stars](https://img.shields.io/github/stars/wmp666/Whetstone?style=flat&logo=data:image/svg%2bxml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZlcnNpb249IjEiIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiI+PHBhdGggZD0iTTggLjI1YS43NS43NSAwIDAgMSAuNjczLjQxOGwxLjg4MiAzLjgxNSA0LjIxLjYxMmEuNzUuNzUgMCAwIDEgLjQxNiAxLjI3OWwtMy4wNDYgMi45Ny43MTkgNC4xOTJhLjc1MS43NTEgMCAwIDEtMS4wODguNzkxTDggMTIuMzQ3bC0zLjc2NiAxLjk4YS43NS43NSAwIDAgMS0xLjA4OC0uNzlsLjcyLTQuMTk0TC44MTggNi4zNzRhLjc1Ljc1IDAgMCAxIC40MTYtMS4yOGw0LjIxLS42MTFMNy4zMjcuNjY4QS43NS43NSAwIDAgMSA4IC4yNVoiIGZpbGw9IiNlYWM1NGYiLz48L3N2Zz4=&logoSize=auto&label=Stars&labelColor=444444&color=eac54f)](https://github.com/wmp666/ClassTools_JDK25/)
[![哔哩哔哩](https://img.shields.io/badge/主页-bilibili-00A4DB?style=flat&logo=bilibili&logoSize=auto&label=%E4%B8%BB%E9%A1%B5)](https://space.bilibili.com/1075810224)

# 磨刀石 
**让你的每节课都与众不同**

> 基于班级工具开发
> 旨在拓展班级工具的功能

> 作者只是个业余的开发者<br>
> 因此程序中可能存在大量不成熟的代码<br>
> 同时不太会写README,WiKi,License

# 功能

## 已实现
- [x] 磨刀石通过由班级工具生成的课表数据，智能匹配课程，结合课程配置，让你的每节课都与众不同
- [x] 在有准确课表的前提下，磨刀石会智能收集每节课的语音，防止错过精彩瞬间
- [x] 通过**JAVA的反射机制**和[JNA](https://github.com/java-native-access/jna)实现外部导入**Jar类型**和**dll类型**的彩蛋（彩蛋开发应当基于相应规则开发），让彩蛋的添加随心所欲
- [x] 支持通过网络发信器向磨刀石发出信息，让磨刀石听从你的指挥

## 局限性
- [ ] 网络发信器：由于网络端口的限制，只支持向一个磨刀石（第一次启动的）发出信息

# 彩蛋仓库

## 磨刀石作者开发
[![Static Badge](https://img.shields.io/badge/Github-Whetstone_EasterEgg?style=flat&logo=github&label=Whetstone_EasterEgg)](https://www.github.com/wmp666/Whetstone_EasterEgg)

## 共创者开发

### [Karagarasu](https://github.com/Karagarasu)
[![Static Badge](https://img.shields.io/badge/Github-ee?style=flat&logo=github&label=3600%E5%AE%89%E5%85%A8%E5%8D%AB%E5%A3%AB)](https://github.com/Karagarasu/3600safe)
[![Static Badge](https://img.shields.io/badge/Github-ee?style=flat&logo=github&label=U%E7%9B%98%E5%8A%A9%E6%89%8B)](https://github.com/Karagarasu/Uhelper)
[![Static Badge](https://img.shields.io/badge/Github-ee?style=flat&logo=github&label=%E9%9F%B3%E9%A2%91%E6%92%AD%E6%94%BE)](https://github.com/Karagarasu/PlaySound)
[![Static Badge](https://img.shields.io/badge/Github-ee?style=flat&logo=github&label=%E5%88%9B%E5%BB%BA%E7%A9%BA%E7%99%BD%E9%9A%90%E8%97%8F%E5%A4%A7%E6%96%87%E4%BB%B6)](https://github.com/Karagarasu/CreatFile)
[![Static Badge](https://img.shields.io/badge/Github-ee?style=flat&logo=github&label=%E9%9F%B3%E9%87%8F%E8%B0%83%E8%8A%82)](https://github.com/Karagarasu/SetSound)
[![Static Badge](https://img.shields.io/badge/Github-ee?style=flat&logo=github&label=%E6%8F%90%E6%9D%83)](https://github.com/Karagarasu/GetRights)


# 下载（磨刀石和彩蛋）
[蓝奏云 密码:1234](https://wmp666.lanzouw.com/b00uzytpej)

# 使用
[![Static Badge](https://img.shields.io/badge/WiKi-Whetstone?style=flat&logo=github&label=Whetstone)](https://www.github.com/wmp666/Whetstone/wiki)

# 许可证
GNU General Public License v3.0

# 开源仓库
- [![Static Badge](https://img.shields.io/badge/Github-lib?style=flat&logo=github&label=ClassTools_JDK25)](https://github.com/wmp666/ClassTools_JDK25/)
- [![Static Badge](https://img.shields.io/badge/Github-lib?style=flat&logo=github&label=Lunar)](https://github.com/6tail/lunar-java)
- [json]()
- [![Static Badge](https://img.shields.io/badge/Github-lib?style=flat&logo=github&label=JNA)](https://github.com/java-native-access/jna)
- [![Static Badge](https://img.shields.io/badge/Github-lib?style=flat&logo=github&label=FlatLaf)](https://github.com/JFormDesigner/FlatLaf)
- [commonmark]()
- ...

# Q&A
> **问：** 初次下载磨刀石，运行失败？
> **答：** 第一次启动磨刀石需要配置一些基础数据，如：课程特色功能配置文件（start_list.json），基础信息文件（settings.propreties），彩蛋单元变量文件（var_list.json）

> **问：** 每天都录音是否会占用大量空间？
> **答：** 并不会，在默认情况下，磨刀石只会允许存档5天的数据，如需修改这一数据，可以查看用户使用帮助

关注苍野喵
