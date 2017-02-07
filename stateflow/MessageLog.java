package org.mechadojo.stateflow;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageLog {
    Controller controller;
    public ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<>();
    public int flushCount = 1000;
    public Pattern pattern;

    public MessageLog() {}
    public MessageLog(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    public void handle(String path, Message msg) {
        if (pattern != null && path != null) {
            Matcher m = pattern.matcher(path);
            if (m.find()) {
                messages.add(msg);
            }
        }

        if (messages.size() > flushCount) flush();
    }

    public void flush() {
        messages.clear();
    }

}
