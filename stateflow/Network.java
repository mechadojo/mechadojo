package org.mechadojo.stateflow;

public interface Network {
    void out(String port, MessageRoute message);
    void undefined(String port, MessageRoute message);
    void idle(MessageRoute message);
    void next(MessageRoute message);

    Message getParameter(String path);
    void setParameter(String path, Message value);
    void postEvent(MessageRoute event);

    void verbose(String msg);
    void debug(String msg);
    void info(String msg);
    void warning(String msg);
    void error(String msg);

}
