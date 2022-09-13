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

package io.github.uhsk.lua

import android.annotation.SuppressLint
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.uhsk.kit.toFile
import io.github.uhsk.lua.lib.XposedBridgeLib
import io.github.uhsk.lua.lib.XposedHelpersLib
import org.luaj.vm2.lib.DebugLib
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.File

/**
 * Xposed入口类
 *
 * @since  1.0.0
 * @author sollyu
 * @date   2022-09-13
 */
class Xposed : IXposedHookLoadPackage {

    @SuppressLint("SdCardPath")
    override fun handleLoadPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        handleLoadPackageLuaScriptDir(lpparam = loadPackageParam, scriptDir = "/sdcard/Android/data/${loadPackageParam.packageName}/files/xposed/".toFile())
        handleLoadPackageLuaScriptDir(lpparam = loadPackageParam, scriptDir = "/sdcard/XposedLua/".toFile())
    }

    private fun handleLoadPackageLuaScriptDir(lpparam: XC_LoadPackage.LoadPackageParam, scriptDir: File) {
        if (scriptDir.exists().not() || scriptDir.isDirectory.not()) {
            return
        }

        val appLuaScriptFileList: List<File> = scriptDir.listFiles()?.filter { it.extension == "lua" && it.canRead() } ?: emptyList()
        if (appLuaScriptFileList.isEmpty()) {
            return
        }

        for (luaScriptFile in appLuaScriptFileList) {
            val globals = JsePlatform.standardGlobals()
            globals.load(DebugLib())
            globals.load(XposedBridgeLib())
            globals.load(XposedHelpersLib())
            globals.load(luaScriptFile.reader(charset = Charsets.UTF_8).readText()).call()
            val luaHandleLoadPackage = globals["handleLoadPackage"]
            if (luaHandleLoadPackage.isfunction()) {
                luaHandleLoadPackage.call(CoerceJavaToLua.coerce(lpparam))
            }
        }

    }


}
