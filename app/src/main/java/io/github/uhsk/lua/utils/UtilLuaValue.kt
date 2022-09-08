package io.github.uhsk.lua.utils

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import io.github.uhsk.lua.extensions.toJavaObject
import io.github.uhsk.lua.extensions.toLuaObject
import org.luaj.vm2.LuaTable

object UtilLuaValue {

    fun luaTableToXcMethod(table: LuaTable): XC_MethodHook {
        if (table.get("replaceHookedMethod").isfunction()) {
            return object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam): Any {
                    return table.get("replaceHookedMethod").call(param.toLuaObject()).toJavaObject()
                }
            }
        }

        if (table.get("beforeHookedMethod").isfunction() || table.get("afterHookedMethod").isfunction()) {
            return object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    super.beforeHookedMethod(param)
                    if (table.get("beforeHookedMethod").isfunction()) {
                        table.get("beforeHookedMethod").call(param.toLuaObject())
                    }
                }

                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    if (table.get("afterHookedMethod").isfunction()) {
                        table.get("afterHookedMethod").call(param.toLuaObject())
                    }
                }
            }
        }

        throw IllegalArgumentException()
    }

}
