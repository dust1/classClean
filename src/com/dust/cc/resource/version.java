package com.dust.cc.resource;

import java.util.ListResourceBundle;

public final class version extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
            { "full", "12.0.1+12" },
            { "jdk", "12.0.1" },
            { "release", "12.0.1" },
        };
    }
}
