function handleLoadPackage(loadPackageParam)
    XposedHelpers.findAndHookMethod('android.telephony.TelephonyManager', loadPackageParam.classLoader, 'getDeviceId', {
        beforeHookedMethod = function(methodHookParam)
            XposedBridge.log('xposed lua hook TelephonyManager:getDeviceId:beforeHookedMethod: ' .. methodHookParam.thisObject:toString())
        end,
    })
end