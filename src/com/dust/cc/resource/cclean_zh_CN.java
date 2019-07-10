package com.dust.cc.resource;

import java.util.ListResourceBundle;

public final class cclean_zh_CN extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                { "err.prefix", "\u9519\u8BEF:" },
                { "note.prefix", "\u6CE8:" },
                { "warn.prefix", "\u8B66\u544A:" },
                { "main.usage", "\u7528\u6CD5: {0} <bootstrapClassPath>\n\u5176\u4E2D, \u53EF\u80FD\u7684\u9009\u9879\u5305\u62EC:" },
                { "file.error", "\u6587\u4ef6: {0} \u53d1\u751f\u5f02\u5e38,\u5f02\u5e38\u4fe1\u606f:" },
                { "file.error.info", "\u6587\u4ef6\u5f02\u5e38 : {0}" }
        };
    }
}
