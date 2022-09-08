package io.github.uhsk.lua.extensions

import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.CoerceLuaToJava

fun Any.toLuaObject(): LuaValue = CoerceJavaToLua.coerce(this)

fun LuaValue.toJavaObject(): Any = CoerceLuaToJava.coerce(this, Any::class.java)
