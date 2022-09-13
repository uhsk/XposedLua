# XposedLua

## 说明

使用Lua代码来编写Xposed的模块。

## 示例

```lua
function handleLoadPackage(loadPackageParam)
    XposedHelpers.findAndHookMethod('android.app.Application.Application', loadPackageParam.classLoader, 'onCreate', {
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

更为详细内容请参阅：WIKI

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
