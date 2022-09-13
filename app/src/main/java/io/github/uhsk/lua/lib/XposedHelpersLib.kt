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

import de.robv.android.xposed.XposedHelpers
import io.github.uhsk.lua.extensions.toLuaObject
import io.github.uhsk.lua.utils.UtilLuaValue
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.ThreeArgFunction
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.jse.CoerceLuaToJava

/**
 * XposedHelpers 中转类
 *
 * @since  1.0.0
 * @author sollyu
 * @date   2022-09-13
 */
class XposedHelpersLib : TwoArgFunction() {
    override fun call(modName: LuaValue, env: LuaValue): LuaValue {
        val table = LuaTable()
        table.set("findClass", FindClass())
        table.set("findClassIfExists", FindClassIfExists())
        table.set("findField", FindField())
        table.set("findFieldIfExists", FindFieldIfExists())

        table.set("findAndHookMethod", FindAndHookMethod())
        table.set("findAndHookConstructor", FindAndHookConstructor())

        table.set("getObjectField", GetObjectField())
        table.set("getSurroundingThis", GetSurroundingThis())
        table.set("getBooleanField", GetBooleanField())
        table.set("getByteField", GetByteField())
        table.set("getCharField", GetCharField())
        table.set("getDoubleField", GetDoubleField())
        table.set("getFloatField", GetFloatField())
        table.set("getIntField", GetIntField())
        table.set("getLongField", GetLongField())
        table.set("getShortField", GetShortField())

        table.set("getStaticObjectField", GetStaticObjectField())
        table.set("getStaticBooleanField", GetStaticBooleanField())
        table.set("getStaticByteField", GetStaticByteField())
        table.set("getStaticCharField", GetStaticCharField())
        table.set("getStaticDoubleField", GetStaticDoubleField())
        table.set("getStaticFloatField", GetStaticFloatField())
        table.set("getStaticIntField", GetStaticIntField())
        table.set("getStaticLongField", GetStaticLongField())
        table.set("getStaticShortField", GetStaticShortField())

        table.set("setObjectField", SetObjectField())
        table.set("setBooleanField", SetBooleanField())
        table.set("setByteField", SetByteField())
        table.set("setCharField", SetCharField())
        table.set("setDoubleField", SetDoubleField())
        table.set("setFloatField", SetFloatField())
        table.set("setIntField", SetIntField())
        table.set("setLongField", SetLongField())
        table.set("setShortField", SetShortField())

        table.set("setStaticObjectField", SetStaticObjectField())
        table.set("setStaticBooleanField", SetStaticBooleanField())
        table.set("setStaticByteField", SetStaticByteField())
        table.set("setStaticCharField", SetStaticCharField())
        table.set("setStaticDoubleField", SetStaticDoubleField())
        table.set("setStaticFloatField", SetStaticFloatField())
        table.set("setStaticIntField", SetStaticIntField())
        table.set("setStaticLongField", SetStaticLongField())
        table.set("setStaticShortField", SetStaticShortField())

        table.set("callMethod", CallMethod())
        table.set("callStaticMethod", CallStaticMethod())

        table.set("newInstance", NewInstance())

        env.set("XposedHelpers", table)
        env.get("package").get("loaded").set("XposedHelpers", table)
        return table
    }

    private class FindAndHookMethod : VarArgFunction() {

        override fun invoke(args: Varargs): LuaValue {
            return when {
                args.isstring(1)   -> invoke(className = args.checkjstring(1), classLoader = args.checkuserdata(2) as ClassLoader, methodName = args.checkjstring(3), args = args)
                args.isuserdata(1) -> invoke(className = args.checkuserdata(1) as Class<*>, methodName = args.checkjstring(2), args = args)
                else               -> throw IllegalArgumentException()
            }
        }

        private fun invoke(className: String, methodName: String, classLoader: ClassLoader, args: Varargs): LuaValue {
            val clazz: Class<*> = XposedHelpers.findClass(className, classLoader)

            val patternList: ArrayList<Any> = ArrayList()
            for (i in 4 until args.narg()) {
                patternList.add(CoerceLuaToJava.coerce(args.arg(i), Any::class.java))
            }
            patternList.add(UtilLuaValue.luaTableToXcMethod(args.checktable(args.narg())))

            val patternArray: Array<Any> = patternList.toArray()
            return XposedHelpers.findAndHookMethod(clazz, methodName, *patternArray).toLuaObject()
        }

        private fun invoke(className: Class<*>, methodName: String, args: Varargs): LuaValue {
            val patternList: ArrayList<Any> = ArrayList()
            for (i in 3 until args.narg()) {
                patternList.add(CoerceLuaToJava.coerce(args.arg(i), Any::class.java))
            }
            patternList.add(UtilLuaValue.luaTableToXcMethod(args.checktable(args.narg())))

            val patternArray: Array<Any> = patternList.toArray()
            return XposedHelpers.findAndHookMethod(className, methodName, *patternArray).toLuaObject()
        }


    }

    private class FindAndHookConstructor : VarArgFunction() {
        override fun invoke(args: Varargs): LuaValue {
            return when {
                args.isstring(1)   -> invoke(className = args.checkjstring(1), classLoader = args.checkuserdata(2) as ClassLoader, args = args)
                args.isuserdata(1) -> invoke(className = args.checkuserdata(1) as Class<*>, args = args)
                else               -> throw IllegalArgumentException()
            }
        }

        private fun invoke(className: String, classLoader: ClassLoader, args: Varargs): LuaValue {
            val clazz: Class<*> = XposedHelpers.findClass(className, classLoader)

            val patternList: ArrayList<Any> = ArrayList()
            for (i in 3 until args.narg()) {
                patternList.add(CoerceLuaToJava.coerce(args.arg(i), Any::class.java))
            }
            patternList.add(UtilLuaValue.luaTableToXcMethod(args.checktable(args.narg())))

            val patternArray: Array<Any> = patternList.toArray()
            return XposedHelpers.findAndHookConstructor(clazz, *patternArray).toLuaObject()
        }

        private fun invoke(className: Class<*>, args: Varargs): LuaValue {
            val patternList: ArrayList<Any> = ArrayList()
            for (i in 2 until args.narg()) {
                patternList.add(CoerceLuaToJava.coerce(args.arg(i), Any::class.java))
            }
            patternList.add(UtilLuaValue.luaTableToXcMethod(args.checktable(args.narg())))

            val patternArray: Array<Any> = patternList.toArray()
            return XposedHelpers.findAndHookConstructor(className, *patternArray).toLuaObject()
        }

    }

    private class FindClass : TwoArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue): LuaValue {
            return XposedHelpers.findClass(luaClassName.tojstring(), CoerceLuaToJava.coerce(luaClassLoader, ClassLoader::class.java) as ClassLoader).toLuaObject()
        }
    }

    private class FindClassIfExists : TwoArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue): LuaValue {
            return XposedHelpers.findClassIfExists(luaClassLoader.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader).toLuaObject()
        }
    }

    private class FindField : TwoArgFunction() {
        override fun call(luaClass: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.findField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class FindFieldIfExists : TwoArgFunction() {
        override fun call(luaClass: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.findFieldIfExists(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetObjectField : TwoArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getObjectField(luaObject.checkuserdata(), luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetSurroundingThis : OneArgFunction() {
        override fun call(luaObject: LuaValue): LuaValue {
            return XposedHelpers.getSurroundingThis(luaObject.checkuserdata()).toLuaObject()
        }
    }

    private class GetBooleanField : TwoArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getBooleanField(luaObject.checkuserdata(), luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetByteField : TwoArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getByteField(luaObject.checkuserdata(), luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetCharField : TwoArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getCharField(luaObject.checkuserdata(), luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetDoubleField : TwoArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getDoubleField(luaObject.checkuserdata(), luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetFloatField : TwoArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getFloatField(luaObject.checkuserdata(), luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetIntField : TwoArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getIntField(luaObject.checkuserdata(), luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetLongField : TwoArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getLongField(luaObject.checkuserdata(), luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetShortField : TwoArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getShortField(luaObject.checkuserdata(), luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetStaticObjectField : ThreeArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticObjectField(XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader), luaFieldName.checkjstring()).toLuaObject()
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticObjectField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetStaticBooleanField : VarArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticBooleanField(XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader), luaFieldName.checkjstring()).toLuaObject()
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticBooleanField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetStaticByteField : ThreeArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticByteField(XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader), luaFieldName.checkjstring()).toLuaObject()
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticByteField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetStaticCharField : ThreeArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticCharField(XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader), luaFieldName.checkjstring()).toLuaObject()
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticCharField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetStaticDoubleField : ThreeArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticDoubleField(XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader), luaFieldName.checkjstring()).toLuaObject()
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticDoubleField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetStaticFloatField : ThreeArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticFloatField(XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader), luaFieldName.checkjstring()).toLuaObject()
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticFloatField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetStaticIntField : ThreeArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticIntField(XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader), luaFieldName.checkjstring()).toLuaObject()
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticIntField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetStaticLongField : ThreeArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticLongField(XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader), luaFieldName.checkjstring()).toLuaObject()
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticLongField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class GetStaticShortField : ThreeArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticShortField(XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader), luaFieldName.checkjstring()).toLuaObject()
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue): LuaValue {
            return XposedHelpers.getStaticShortField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring()).toLuaObject()
        }
    }

    private class SetObjectField : ThreeArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            XposedHelpers.setObjectField(luaObject.checkuserdata(), luaFieldName.checkjstring(), luaValue.checkuserdata())
            return NIL
        }
    }

    private class SetBooleanField : ThreeArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            XposedHelpers.setBooleanField(luaObject.checkuserdata(), luaFieldName.checkjstring(), luaValue.checkboolean())
            return NIL
        }
    }

    private class SetByteField : ThreeArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            XposedHelpers.setByteField(luaObject.checkuserdata(), luaFieldName.checkjstring(), luaValue.checknumber().tobyte())
            return NIL
        }
    }

    private class SetCharField : ThreeArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            when {
                luaValue.isstring() -> XposedHelpers.setCharField(luaObject.checkuserdata(), luaFieldName.checkjstring(), luaValue.checkjstring().first())
                luaValue.isnumber() -> XposedHelpers.setCharField(luaObject.checkuserdata(), luaFieldName.checkjstring(), luaValue.checknumber().tochar())
            }
            return NIL
        }
    }

    private class SetDoubleField : ThreeArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            XposedHelpers.setDoubleField(luaObject.checkuserdata(), luaFieldName.checkjstring(), luaValue.checkdouble())
            return NIL
        }
    }

    private class SetFloatField : ThreeArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            XposedHelpers.setFloatField(luaObject.checkuserdata(), luaFieldName.checkjstring(), luaValue.checkdouble().toFloat())
            return NIL
        }
    }

    private class SetIntField : ThreeArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            XposedHelpers.setIntField(luaObject.checkuserdata(), luaFieldName.checkjstring(), luaValue.checkint())
            return NIL
        }
    }

    private class SetLongField : ThreeArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            XposedHelpers.setLongField(luaObject.checkuserdata(), luaFieldName.checkjstring(), luaValue.checklong())
            return NIL
        }
    }

    private class SetShortField : ThreeArgFunction() {
        override fun call(luaObject: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            XposedHelpers.setShortField(luaObject.checkuserdata(), luaFieldName.checkjstring(), luaValue.checknumber().toshort())
            return NIL
        }
    }

    private class SetStaticObjectField : VarArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            val clazz = XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader)
            XposedHelpers.setStaticObjectField(clazz, luaFieldName.checkjstring(), luaValue.checkuserdata())
            return NIL
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            XposedHelpers.setStaticObjectField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring(), luaValue.checkuserdata())
            return NIL
        }
    }

    private class SetStaticBooleanField : VarArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            val clazz = XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader)
            XposedHelpers.setStaticBooleanField(clazz, luaFieldName.checkjstring(), luaValue.checkboolean())
            return NIL
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            XposedHelpers.setStaticBooleanField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring(), luaValue.checkboolean())
            return NIL
        }
    }

    private class SetStaticByteField : VarArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            val clazz = XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader)
            XposedHelpers.setStaticByteField(clazz, luaFieldName.checkjstring(), luaValue.checknumber().tobyte())
            return NIL
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            XposedHelpers.setStaticByteField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring(), luaValue.checknumber().tobyte())
            return NIL
        }
    }

    private class SetStaticCharField : VarArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            val clazz = XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader)
            XposedHelpers.setStaticCharField(clazz, luaFieldName.checkjstring(), luaValue.checknumber().tochar())
            return NIL
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            XposedHelpers.setStaticCharField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring(), luaValue.checknumber().tochar())
            return NIL
        }
    }

    private class SetStaticDoubleField : VarArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            val clazz = XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader)
            XposedHelpers.setStaticDoubleField(clazz, luaFieldName.checkjstring(), luaValue.checkdouble())
            return NIL
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            XposedHelpers.setStaticDoubleField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring(), luaValue.checkdouble())
            return NIL
        }
    }

    private class SetStaticFloatField : VarArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            val clazz = XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader)
            XposedHelpers.setStaticFloatField(clazz, luaFieldName.checkjstring(), luaValue.checknumber().tofloat())
            return NIL
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            XposedHelpers.setStaticFloatField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring(), luaValue.checknumber().tofloat())
            return NIL
        }
    }

    private class SetStaticIntField : VarArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            val clazz = XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader)
            XposedHelpers.setStaticIntField(clazz, luaFieldName.checkjstring(), luaValue.checknumber().toint())
            return NIL
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            XposedHelpers.setStaticIntField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring(), luaValue.checknumber().toint())
            return NIL
        }
    }

    private class SetStaticLongField : VarArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            val clazz = XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader)
            XposedHelpers.setStaticLongField(clazz, luaFieldName.checkjstring(), luaValue.checknumber().tolong())
            return NIL
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            XposedHelpers.setStaticLongField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring(), luaValue.checknumber().tolong())
            return NIL
        }
    }

    private class SetStaticShortField : VarArgFunction() {
        override fun call(luaClassName: LuaValue, luaClassLoader: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            val clazz = XposedHelpers.findClass(luaClassName.checkjstring(), luaClassLoader.checkuserdata() as ClassLoader)
            XposedHelpers.setStaticShortField(clazz, luaFieldName.checkjstring(), luaValue.checknumber().toshort())
            return NIL
        }

        override fun call(luaClass: LuaValue, luaFieldName: LuaValue, luaValue: LuaValue): LuaValue {
            XposedHelpers.setStaticShortField(luaClass.checkuserdata() as Class<*>, luaFieldName.checkjstring(), luaValue.checknumber().toshort())
            return NIL
        }
    }

    private class CallMethod : VarArgFunction() {
        override fun invoke(args: Varargs): Varargs {
            val patternList: ArrayList<Any> = ArrayList()
            for (i in 3..args.narg()) {
                patternList.add(CoerceLuaToJava.coerce(args.arg(i), Any::class.java))
            }
            val patternArray: Array<Any> = patternList.toArray()
            return XposedHelpers.callMethod(args.checkuserdata(1), args.checkjstring(2), *patternArray).toLuaObject()
        }
    }

    private class CallStaticMethod : VarArgFunction() {
        override fun invoke(args: Varargs): Varargs {
            return when {
                args.isstring(1)   -> invoke(className = args.checkjstring(1), classLoader = args.checkuserdata(2) as ClassLoader, methodName = args.checkjstring(3), args = args)
                args.isuserdata(1) -> invoke(className = args.checkuserdata(1) as Class<*>, methodName = args.checkjstring(2), args = args)
                else               -> throw IllegalArgumentException()
            }
        }

        private fun invoke(className: String, classLoader: ClassLoader, methodName: String, args: Varargs): LuaValue {
            val clazz: Class<*> = XposedHelpers.findClass(className, classLoader)
            val patternList: ArrayList<Any> = ArrayList()
            for (i in 4..args.narg()) {
                patternList.add(CoerceLuaToJava.coerce(args.arg(i), Any::class.java))
            }
            val patternArray: Array<Any> = patternList.toArray()
            return XposedHelpers.callStaticMethod(clazz, methodName, *patternArray).toLuaObject()
        }

        private fun invoke(className: Class<*>, methodName: String, args: Varargs): LuaValue {
            val patternList: ArrayList<Any> = ArrayList()
            for (i in 3..args.narg()) {
                patternList.add(CoerceLuaToJava.coerce(args.arg(i), Any::class.java))
            }
            val patternArray: Array<Any> = patternList.toArray()
            return XposedHelpers.callMethod(className, methodName, *patternArray).toLuaObject()
        }
    }

    private class NewInstance : VarArgFunction() {
        override fun invoke(args: Varargs): Varargs {
            return when {
                args.isstring(1)   -> invoke(className = args.checkjstring(1), classLoader = args.checkuserdata(2) as ClassLoader, methodName = args.checkjstring(3), args = args)
                args.isuserdata(1) -> invoke(className = args.checkuserdata(1) as Class<*>, methodName = args.checkjstring(2), args = args)
                else               -> throw IllegalArgumentException()
            }
        }

        private fun invoke(className: String, classLoader: ClassLoader, methodName: String, args: Varargs): LuaValue {
            val clazz: Class<*> = XposedHelpers.findClass(className, classLoader)
            val patternList: ArrayList<Any> = ArrayList()
            for (i in 4..args.narg()) {
                patternList.add(CoerceLuaToJava.coerce(args.arg(i), Any::class.java))
            }
            val patternArray: Array<Any> = patternList.toArray()
            return XposedHelpers.newInstance(clazz, methodName, *patternArray).toLuaObject()
        }

        private fun invoke(className: Class<*>, methodName: String, args: Varargs): LuaValue {
            val patternList: ArrayList<Any> = ArrayList()
            for (i in 3..args.narg()) {
                patternList.add(CoerceLuaToJava.coerce(args.arg(i), Any::class.java))
            }
            val patternArray: Array<Any> = patternList.toArray()
            return XposedHelpers.newInstance(className, methodName, *patternArray).toLuaObject()
        }

    }


}
