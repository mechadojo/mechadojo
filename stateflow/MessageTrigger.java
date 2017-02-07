package org.mechadojo.stateflow;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageTrigger extends MessageRoute {
    Pattern pattern;

    public boolean eval(String path, MessageRoute msg)
    {
        if (pattern != null && path != null) {
            Matcher m = pattern.matcher(path);
            if (!m.find()) {
                return false;
            }
        }

        if (condition == null) return true;
        return condition.eval(msg);
    }

    public boolean eval(MessageRoute msg)
    {
        return eval(msg.event, msg);
    }

    public void reset()
    {
    }
}
