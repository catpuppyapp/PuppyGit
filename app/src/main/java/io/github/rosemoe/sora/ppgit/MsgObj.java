package io.github.rosemoe.sora.ppgit;

import io.github.rosemoe.sora.lang.analysis.StyleReceiver;


public class MsgObj {
    private StyleReceiver styleReceiver;
    private Object data;

    public MsgObj(StyleReceiver styleReceiver, Object data) {
        this.styleReceiver = styleReceiver;
        this.data = data;
    }

    public StyleReceiver getStyleReceiver() {
        return styleReceiver;
    }

    public MsgObj setStyleReceiver(StyleReceiver styleReceiver) {
        this.styleReceiver = styleReceiver;
        return this;
    }

    public Object getData() {
        return data;
    }

    public MsgObj setData(Object data) {
        this.data = data;
        return this;
    }
}

