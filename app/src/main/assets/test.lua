

function handleLoadPackage(lpparam)
    XposedHelpers.findAndHookMethod('android.app.Activity', lpparam.classLoader, 'onCreate', 'android.os.Bundle', {
        afterHookedMethod = function(methodHookParam)
            local BuildClass = XposedHelpers.findClass('android.os.Build', lpparam.classLoader)
            XposedBridge.log('Activity, onCreate, afterHookedMethod: class=' .. XposedHelpers.getStaticObjectField(BuildClass, 'MANUFACTURER'))
            XposedBridge.log('Activity, onCreate, afterHookedMethod: class=' .. XposedHelpers.getStaticObjectField('android.os.Build', lpparam.classLoader, 'MANUFACTURER'))
        end
    })
end
