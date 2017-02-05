package org.mechadojo.stateflow;

import java.util.Comparator;

public class MessageRoute {
    public Controller controller;

    public MessageScope scope = null;
    public MessagePath source = null;
    public MessagePath target = null;

    public String type = "";
    public String event = "";
    public MessageCondition condition;
    public Message message;
    public Action action;

    private String _baseType = null;

    public MessageRoute() {}
    public MessageRoute(MessageRoute msg) {
        scope = msg.scope;
        source = msg.source;
        target = msg.target;
        type = msg.type;
        event = msg.event;
        condition = msg.condition;
        message = msg.message;
    }

    public MessageRoute(MessageRoute msg, MessagePath target) {
        scope = msg.scope;
        source = msg.source;
        target = target;
        type = msg.type;
        event = msg.event;
        condition = msg.condition;
        message = msg.message;
    }

    public MessageRoute setTarget(MessagePath target) {
        this.target = target;
        return this;
    }

    public MessageRoute setTarget(String target) {
        this.target = new MessagePath(target);
        return this;
    }

    public MessageRoute setSource(MessagePath source) {
        this.source = source;
        return this;
    }

    public MessageRoute setSource(String source) {
        this.source = new MessagePath(source);
        return this;
    }



    public MessageRoute delay(double seconds)
    {
        final long timeout = controller.cycleTime + (long)(seconds * 1000000000);
        condition = new  MessageCondition() {
            public boolean eval(MessageRoute msg) {
                return msg.controller.cycleTime > timeout;
            }
        };

        return this;
    }

    public MessageRoute delay(long milliseconds)
    {
        final long timeout = controller.cycleTime + (milliseconds * 1000000);
        condition = new  MessageCondition() {
            public boolean eval(MessageRoute msg) {
                return msg.controller.cycleTime > timeout;
            }
        };

        return this;
    }
    public MessageRoute condition(MessageCondition condition) {
        this.condition = condition;
        return this;
    }



    public String getBaseType() {
        if (_baseType != null) return _baseType;

        _baseType = type;
        int i = _baseType.indexOf('/');
        if (i >= 0) _baseType = _baseType.substring(0, i);

        return _baseType;
    }

    public static Comparator<MessageRoute> MessageRouteComparator = new Comparator<MessageRoute>() {
        @Override
        public int compare(MessageRoute lhs, MessageRoute rhs) {
            int result = result = Long.compare(lhs.scope == null ? 0 : lhs.scope.scopeId, rhs.scope == null ? 0 : rhs.scope.scopeId);
            if (result != 0) return result;

            if (lhs.target == null && rhs.target == null) return 0;
            if (lhs.target == null) return 1;
            if (rhs.target == null) return -1;

           return lhs.target.compareTo(rhs.target);
        }
    };

    public static MessageRoute data(String target) { return data(target, new Message()); }
    public static MessageRoute data(String target, Message msg) {
        MessageRoute m = new MessageRoute();
        m.type = "data";
        m.target = new MessagePath(target);
        m.message = msg;
        return m;
    }

    public static MessageRoute event(String event) { return event(event, "", new Message()); }
    public static MessageRoute event(String event, String target) { return event(event, target, new Message()); }
    public static MessageRoute event(String event, Message m) { return event(event, "", m); }
    public static MessageRoute event(String event, String target, Message msg) {
        MessageRoute m = new MessageRoute();
        m.event = event;
        m.type = "event";
        m.target = new MessagePath(target);
        m.message = msg;
        return m;
    }

    public static MessageRoute filter(String event, MessageCondition handler) { return filter(event, "", new Message(), handler); }
    public static MessageRoute filter(String event, String target, MessageCondition handler) { return filter(event, target, new Message(), handler); }
    public static MessageRoute filter(String event, Message msg, MessageCondition handler) { return filter(event, "", msg, handler); }
    public static MessageRoute filter(String event, String target, Message msg, MessageCondition handler) {
        MessageRoute m = new MessageRoute();
        m.event = event;
        m.type = "event/filter";
        m.condition = handler;
        m.target = new MessagePath(target);
        m.message = msg;
        return m;
    }
}
