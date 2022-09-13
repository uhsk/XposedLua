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

package io.github.uhsk.lua.lib

import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import io.github.uhsk.lua.extensions.toLuaObject
import io.github.uhsk.lua.utils.UtilLuaValue
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.ThreeArgFunction
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.ZeroArgFunction
import java.util.*

/**
 * XposedBridge 中转类
 *
 * @since  1.0.0
 * @author sollyu
 * @date   2022-09-13
 */
class XposedBridgeLib : TwoArgFunction() {


    override fun call(modName: LuaValue, env: LuaValue): LuaValue {
        val table = LuaTable()
        table.set("getXposedVersion", GetXposedVersion())
        table.set("log", Log())

        table.set("hookAllMethods", HookAllMethods())
        table.set("hookAllConstructors", HookAllConstructors())

        env.set("XposedBridge", table)
        env.get("package").get("loaded").set("XposedBridge", table)
        return table
    }

    private class GetXposedVersion : ZeroArgFunction() {
        override fun call(): LuaValue {
            return XposedBridge.getXposedVersion().toLuaObject()
        }
    }

    private class HookAllMethods : ThreeArgFunction() {
        override fun call(luaHookClass: LuaValue, luaMethodName: LuaValue, luaCallback: LuaValue): LuaValue {
            return XposedBridge.hookAllMethods(luaHookClass.checkuserdata() as Class<*>, luaMethodName.checkjstring(), UtilLuaValue.luaTableToXcMethod(luaCallback.checktable())).toLuaObject()
        }
    }

    private class HookAllConstructors : ThreeArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaCallback: LuaValue): LuaValue {
            val clazz: Class<*> = XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader)
            return XposedBridge.hookAllConstructors(clazz, UtilLuaValue.luaTableToXcMethod(luaCallback.checktable())).toLuaObject()
        }

        override fun call(luaHookClass: LuaValue, luaCallback: LuaValue): LuaValue {
            return XposedBridge.hookAllConstructors(luaHookClass.checkuserdata() as Class<*>, UtilLuaValue.luaTableToXcMethod(luaCallback.checktable())).toLuaObject()
        }
    }

    private class Log : OneArgFunction() {
        override fun call(arg: LuaValue): LuaValue {
            when {
                arg.isstring()                                       -> XposedBridge.log(String.format(Locale.ENGLISH, "XposedLua: %s", arg.checkstring().tojstring()))
                arg.isuserdata() && arg.checkuserdata() is Throwable -> XposedBridge.log(arg.checkuserdata() as Throwable)
            }
            return NONE
        }
    }
}
