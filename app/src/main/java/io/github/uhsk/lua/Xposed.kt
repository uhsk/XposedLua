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


class Xposed : IXposedHookLoadPackage {

    @SuppressLint("SdCardPath")
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        handleLoadPackageLuaScriptDir(lpparam = lpparam, scriptDir = "/sdcard/Android/data/${lpparam.packageName}/files/xposed/".toFile())
        handleLoadPackageLuaScriptDir(lpparam = lpparam, scriptDir = "/sdcard/XposedLua/".toFile())
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
