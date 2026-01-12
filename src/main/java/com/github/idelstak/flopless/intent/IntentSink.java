package com.github.idelstak.flopless.intent;

import com.github.idelstak.flopless.state.*;

public interface IntentSink {

    public void accept(Intend intend);

    FloplessState latestState();
}
