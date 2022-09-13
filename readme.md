# XposedLua

## 说明

使用Lua代码来编写Xposed的模块。可以让Xposed模块开发者快速测试hook功能，拥有免重启特性。

## 示例

```lua
function handleLoadPackage(loadPackageParam)
    XposedHelpers.findAndHookMethod('android.app.Application', loadPackageParam.classLoader, 'onCreate', {
        beforeHookedMethod = function(methodHookParam)
            XposedBridge.log('xposed lua hook beforeHookedMethod: ' .. methodHookParam.thisObject)
        end,
        afterHookedMethod = function(methodHookParam)
            XposedBridge.log('xposed lua hook afterHookedMethod: ' .. methodHookParam.thisObject)
        end
    })
end
```

## 文档

更为详细内容请参阅：[WIKI](https://github.com/uhsk/XposedLua/wiki)

## 下载

前往[Release](https://github.com/uhsk/XposedLua/releases)页面下载APK安装并激活.

> #### 备注
> - 正常此项目不出现兼容性或bug，不会更新。
> - 如果使用中有任何问题，欢迎提交issue。

## LICENSE

```c++
/*
 * Copyright (C) 2022. sollyu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```
