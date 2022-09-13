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
 *
 *
 */

package io.github.uhsk.lua.utils

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import io.github.uhsk.lua.extensions.toJavaObject
import io.github.uhsk.lua.extensions.toLuaObject
import org.luaj.vm2.LuaTable

/**
 * Lua工具类
 *
 * @since  1.0.0
 * @author sollyu
 * @date   2022-09-13
 */
object UtilLuaValue {

    /**
     * 将LuaTable转换成XC_Method
     *
     * ```lua
     * xposed.findAndHook('test', 'test', {
     *      // 此处为LuaTable
     * })
     * ```
     *
     * @since 1.0.0
     */
    fun luaTableToXcMethod(table: LuaTable): XC_MethodHook {

        //
        // 如果包含 replaceHookedMethod 就返回
        //
        if (table.get("replaceHookedMethod").isfunction()) {
            return object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam): Any {
                    return table.get("replaceHookedMethod").call(param.toLuaObject()).toJavaObject()
                }
            }
        }

        if (table.get("beforeHookedMethod").isfunction() || table.get("afterHookedMethod").isfunction()) {
            return object : XC_MethodHook() {
                override fun beforeHookedMethod(methodHookParam: MethodHookParam) {
                    super.beforeHookedMethod(methodHookParam)
                    methodHookParam.thisObject
                    if (table.get("beforeHookedMethod").isfunction()) {
                        table.get("beforeHookedMethod").call(methodHookParam.toLuaObject())
                    }
                }

                override fun afterHookedMethod(methodHookParam: MethodHookParam) {
                    super.afterHookedMethod(methodHookParam)
                    if (table.get("afterHookedMethod").isfunction()) {
                        table.get("afterHookedMethod").call(methodHookParam.toLuaObject())
                    }
                }
            }
        }

        throw IllegalArgumentException()
    }

}
