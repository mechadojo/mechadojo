package org.mechadojo.stateflow;


import java.util.HashMap;

public interface MessageHandler {
    void handle(MessageRoute msg, Action action);
}
