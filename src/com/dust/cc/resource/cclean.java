package com.dust.cc.resource;

import java.util.ListResourceBundle;

public final class cclean extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                { "err.prefix", "Error:" },
                { "note.prefix", "Note:" },
                { "warn.prefix", "Warning:" },
                { "main.usage", "Usage: {0} <bootstrapClassPath>\nwhere possible options include:" },
                { "file.error", "File {0} have a exception:" },
                { "file.error.info", "file exception : {0}" }
        };
    }
}
