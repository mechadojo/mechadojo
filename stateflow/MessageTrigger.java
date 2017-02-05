package org.mechadojo.stateflow;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageTrigger extends MessageRoute {
    Pattern pattern;

    public boolean eval(MessageRoute msg)
    {
        if (pattern != null) {
            Matcher m = pattern.matcher(msg.event);
            if (!m.find()) {
                return false;
            }
        }

        if (condition == null) return true;
        return condition.eval(msg);
    }

    public boolean eval(Message msg)
    {
        message = msg;
        return eval(this);
    }

    public void reset()
    {
    }
}
