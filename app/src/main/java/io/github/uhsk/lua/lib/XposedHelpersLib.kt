package io.github.uhsk.lua.lib

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.ThreeArgFunction
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.CoerceLuaToJava

class XposedHelpersLib : TwoArgFunction() {
    override fun call(modName: LuaValue, env: LuaValue): LuaValue {
        val table = LuaTable()
        table.set("findClass", FindClass())
        table.set("findAndHookMethod", FindAndHookMethod())

        table.set("getStaticBooleanField", GetStaticBooleanField())
        table.set("getStaticObjectField", GetStaticObjectField())

        env.set("XposedHelpers", table)
        env.get("package").get("loaded").set("XposedHelpers", table)
        return table
    }

    private class FindAndHookMethod : VarArgFunction() {

        override fun invoke(args: Varargs): Varargs {
            if (args.istable(args.narg()).not()) {
                throw IllegalArgumentException("FindAndHookMethod 参数错误")
            }
            val callback: LuaTable = args.checktable(args.narg())
            if (callback.get("replaceHookedMethod").isnil() && callback.get("beforeHookedMethod").isnil() && callback.get("afterHookedMethod").isnil()) {
                throw IllegalArgumentException("FindAndHookMethod 参数错误")
            }

            if (args.isstring(1) && args.isuserdata(2) && args.checkuserdata(2) is ClassLoader)
                return findAndHookMethod(className = args.checkjstring(1), classLoader = args.checkuserdata(2) as ClassLoader, methodName = args.checkjstring(3), args = args)

            if (args.isuserdata(1) && args.checkuserdata(1) is Class<*>)
                return findAndHookMethod(className = args.checkuserdata(1) as Class<*>, methodName = args.checkjstring(2), args = args)

            throw IllegalArgumentException("FindAndHookMethod 参数错误")
        }

        private fun findAndHookMethod(className: String, classLoader: ClassLoader, methodName: String, args: Varargs): LuaValue {
            val hookClass: Class<*> = XposedHelpers.findClass(className, classLoader)
            val parameters = ArrayList<Any>()
            for (i in 4 until args.narg()) {
                when {
                    args.isstring(i)                                        -> parameters.add(XposedHelpers.findClass(args.checkjstring(i), classLoader))
                    args.isuserdata(i) && args.checkuserdata(i) is Class<*> -> parameters.add(args.checkuserdata(i) as Class<*>)
                    else                                                    -> throw IllegalArgumentException()
                }
            }

            return findAndHookMethod(hookClass, methodName, parameters, args.checktable(args.narg()))
        }

        private fun findAndHookMethod(className: Class<*>, methodName: String, args: Varargs): LuaValue {
            val parameters = ArrayList<Any>()
            for (i in 3 until args.narg()) {
                when {
                    args.isstring(i)                                        -> parameters.add(XposedHelpers.findClass(args.checkjstring(i), className.classLoader))
                    args.isuserdata(i) && args.checkuserdata(i) is Class<*> -> parameters.add(args.checkuserdata(i) as Class<*>)
                    else                                                    -> throw IllegalArgumentException()
                }
            }
            return findAndHookMethod(className, methodName, parameters, args.checktable(args.narg()))
        }

        private fun findAndHookMethod(className: Class<*>, methodName: String, args: ArrayList<Any>, callback: LuaTable): LuaValue {
            when {
                callback.get("replaceHookedMethod").isfunction()
                -> {
                    args.add(object : XC_MethodReplacement() {
                        override fun replaceHookedMethod(param: MethodHookParam): Any {
                            return CoerceLuaToJava.coerce(callback.get("replaceHookedMethod").call(CoerceJavaToLua.coerce(param)), Any::class.java)
                        }
                    })
                }

                callback.get("beforeHookedMethod").isfunction() || callback.get("afterHookedMethod").isfunction()
                -> {
                    args.add(object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            super.beforeHookedMethod(param)
                            if (callback.get("beforeHookedMethod").isfunction()) {
                                callback.get("beforeHookedMethod").call(CoerceJavaToLua.coerce(param))
                            }
                        }

                        override fun afterHookedMethod(param: MethodHookParam) {
                            super.afterHookedMethod(param)
                            if (callback.get("afterHookedMethod").isfunction()) {
                                callback.get("afterHookedMethod").call(CoerceJavaToLua.coerce(param))
                            }
                        }
                    })
                }
            }

            val parameterTypesAndCallbackArray: Array<Any> = args.toArray()
            val unhook = XposedHelpers.findAndHookMethod(className, methodName, *parameterTypesAndCallbackArray)

            return CoerceJavaToLua.coerce(unhook)
        }

    }

    private class FindClass : TwoArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue): LuaValue {
            return CoerceJavaToLua.coerce(XposedHelpers.findClass(luaClassName.tojstring(), CoerceLuaToJava.coerce(luaClassLoader, ClassLoader::class.java) as ClassLoader))
        }
    }

    private class GetStaticBooleanField : VarArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue): LuaValue {
            return CoerceJavaToLua.coerce(XposedHelpers.getStaticBooleanField(XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader), luaFieldName.checkjstring()))
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue): LuaValue {
            return CoerceJavaToLua.coerce(XposedHelpers.getStaticBooleanField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring()))
        }
    }

    private class GetStaticObjectField : ThreeArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue): LuaValue {
            return CoerceJavaToLua.coerce(XposedHelpers.getStaticObjectField(XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader), luaFieldName.checkjstring()))
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue): LuaValue {
            return CoerceJavaToLua.coerce(XposedHelpers.getStaticObjectField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring()))
        }
    }
}
