package com.seventailed.engine.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class EngineLoggerAppender extends AppenderBase<ILoggingEvent> {
    @Override
    public void start() {
        super.start();
    }

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        if(!EngineLogging.getLoggerPrefixes().containsKey(iLoggingEvent.getLoggerName().toLowerCase())) {
            EngineLogging.log("&8[" + EngineLogging.getLoggerPrefixes().get("seventailed") + "&8] &r" + iLoggingEvent.getMessage());
        }else {
            EngineLogging.log("&8[" + EngineLogging.getLoggerPrefixes().get(iLoggingEvent.getLoggerName().toLowerCase()) + "&8] &r" + iLoggingEvent.getMessage());
        }
    }
}
