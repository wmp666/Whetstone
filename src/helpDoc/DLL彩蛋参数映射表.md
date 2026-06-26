> 版本：2.4.1

| 配置文件（Json） | Java       | C/C++                    |
|------------|------------|--------------------------|
| byte       | byte       | char                     |
| char       | char       | wchar_t                  |
| int        | int        | int                      |
| long       | NativeLong | long                     |
| double     | double     | double                   |
| string     | String     | char*/const wchar_t*     |
| WString    | WString    | wchar_t*, const wchar_t* |
| int        | int        | enum                     |
| 未实现        | String[]   | char**                   |
| 未实现        | WString[]  | wchar_t**                |
| 未实现        | int[]      | 固定长度数组 (如 int[10])       |

