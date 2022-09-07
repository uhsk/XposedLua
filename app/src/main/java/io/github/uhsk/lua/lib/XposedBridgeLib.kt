package io.github.uhsk.lua.lib

import de.robv.android.xposed.XposedBridge
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.TwoArgFunction
import java.util.*

class XposedBridgeLib : TwoArgFunction() {
    override fun call(modName: LuaValue, env: LuaValue): LuaValue {
        val table = LuaTable()
        table.set("log", Log())

        env.set("XposedBridge", table)
        env.get("package").get("loaded").set("XposedBridge", table)
        return table
    }

    class Log : OneArgFunction() {
        override fun call(arg: LuaValue): LuaValue {
            when {
                arg.isstring() -> XposedBridge.log(String.format(Locale.ENGLISH, "XposedLua: %s", arg.checkstring().tojstring()))
            }
            return NONE
        }

    }
}
