package com.hyperrealm.kiwi.plugin;

import package com.hyperrealm.kiwi.plugin.ITestPlugin;

public class TestPlugin implements ITestPlugin {

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public int doPluginWork() {
        return 2;
    }
}
