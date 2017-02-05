package org.mechadojo.stateflow;


public class MessageScope {
    public long scopeId;
    public MessageScope previous;
    public Action action;
    public long length = 0;
}
