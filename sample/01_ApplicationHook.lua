function handleLoadPackage(loadPackageParam)
    XposedHelpers.findAndHookMethod('android.app.Application', loadPackageParam.classLoader, 'onCreate', {
        beforeHookedMethod = function(methodHookParam)
            XposedBridge.log('xposed lua hook beforeHookedMethod: ' .. methodHookParam.thisObject:toString())
        end,
        afterHookedMethod = function(methodHookParam)
            XposedBridge.log('xposed lua hook afterHookedMethod: ' .. methodHookParam.thisObject:toString())
        end
    })
end
