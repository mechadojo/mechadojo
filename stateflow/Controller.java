package org.mechadojo.stateflow;

import android.util.Log;

import org.mechadojo.utilities.RunningStatistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class Controller extends StateFlowObject implements Runnable {

    ConcurrentHashMap<String, Parameter> parameters = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Behavior> behaviors = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, ComponentLibrary> libraries = new ConcurrentHashMap<>();

    ConcurrentLinkedQueue<MessageRoute> messages = new ConcurrentLinkedQueue<>();

    ConcurrentHashMap<String, MessageTrigger> eventTriggers = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, MessageTrigger> updateTriggers = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, MessageTrigger> timeTriggers = new ConcurrentHashMap<>();

    public LogHandler logHandler;

    public long startTime;
    public long cycleTime;
    public long cyclePeriod;

    public String logTag = "StateFlow";
    public boolean enableLogging = true;

    public AtomicLong nextMessageId = new AtomicLong(1);
    public AtomicLong nextScopeId = new AtomicLong(1);
    public boolean running = false;

    public RunningStatistics stats = new RunningStatistics();

    public void initialize() {
        for(Parameter p : parameters.values()) p.setController(this);
        for(ComponentLibrary cl : libraries.values()) cl.setController(this);
        for(Behavior b: behaviors.values()) b.setController(this);
    }

    public void run() {
        running = true;
        while (running) {
            step();
            Thread.yield();
        }
    }

    public void step() {
        cycleTime = getTime();
        handleMessages();
        cyclePeriod = getTime() - cycleTime;
        stats.push(cyclePeriod / 1000000.0);
    }

    public void addMessage(MessageRoute msg)
    {
        msg.controller = this;
        if (msg.message.id == 0) {
            msg.message.id = nextMessageId.getAndIncrement();
            msg.message.timestamp = cycleTime;
        }
        messages.add(msg);

    }

    public void addMessage(String target) {
        MessageRoute mr = MessageRoute.data(target);
        addMessage(mr);
    }

    public void addMessage(String target, Message msg) {
        MessageRoute mr = MessageRoute.data(target, msg);
        addMessage(mr);
    }

    public Behavior getBehavior(String path) {
        return behaviors.get(path);
    }

    public MessageScope getNextScope(MessageScope scope, Action action) {
        MessageScope result = new MessageScope();
        result.action = action;
        result.scopeId = nextScopeId.getAndIncrement();
        result.previous = scope;
        result.length = scope.length + 1;
        return result;
    }

    public void handleMessages() {

        Collection<MessageRoute> filters = new LinkedList<>();
        Collection<MessageRoute> data = new LinkedList<>();

        while(messages.size() > 0) {
            MessageRoute m = messages.poll();
            if (m == null) break;

            switch(m.getBaseType()) {
                case "filter":
                    filters.add(m);
                    break;

                case "event":
                    handleTriggers(m, eventTriggers.values(), data);
                    break;

                case "update":
                    handleTriggers(m, updateTriggers.values(), data);
                    break;

                case "data":
                    if (!m.target.isValidPath)
                        m.target.splitInput();
                    data.add(m);
                    break;
            }
        }

        // Route timed messages
        handlePeriodic(data);

        // Sort messages by scope, behavior, action, port

        List<MessageRoute> sorted = new ArrayList<>(data);
        Collections.sort(sorted, MessageRoute.MessageRouteComparator);

        MessageRoute group = null;
        HashMap<String, ArrayList<MessageRoute>> map = new HashMap<>();

        for(MessageRoute msg : data) {
            if (msg.target == null) continue;




            msg.action = getAction(msg.target);
            if (msg.action == null) {
                Log.d("StateFlow", String.format("could not find action: %s", msg.target.path));
                continue;
            }

            //Log.d("StateFlow", String.format("%s: %s -> %s", msg.type, msg.target.path, msg.action.getPath()));

            // Apply message filters
            boolean validMessage = true;
            for(MessageRoute filter : filters ) {
                if (!handleFilter(filter, msg)) {
                    validMessage = false;
                    break;
                }
            }

            if (!validMessage) continue;

            // Recycle the message if the condition is not active
            if (msg.condition != null) {
                if (!msg.condition.eval(msg)) {
                    addMessage(msg);
                    continue;
                }

                // remove the conditition
                msg.condition = null;
            }

            // Every time a new Scope-Behavior-Action group is discovered then process the previous
            // group and start a new group
            if (group == null || !msg.scope.equals(group.scope) || msg.action != group.action) {

                if (map.size() > 0)
                {
                    group.action.run(map);
                }

                map = new HashMap<>();
                group = msg;
            }

            ArrayList<MessageRoute> ls = map.get(msg.target.port);
            if (ls == null) { ls = new ArrayList<>(); map.put(msg.target.port, ls); }
            ls.add(msg);
        }

        // Process the final Scope-Behavior-Action group
        if (group != null && map.size() > 0) {
            group.action.run(map);
        }

    }

    public Action getAction(MessagePath target) {
        if (!target.isValidPath) return null;

        Behavior b = getBehavior(target.behavior);
        if (b == null) return null;
        return b.getAction(target.action);
    }
    /*
     * Parameter Handling Methods
     */

    public Message getParameter(String path) {
        Parameter p = parameters.get(path);
        if (p == null) return null;
        return p.getValue();
    }

    public void setParameter(String path, String source, Message value) {

        Parameter p = parameters.get(path);
        if (p == null) {
            p = new Parameter();
            p.path = path;
            p.controller = this;
            parameters.put(path, p);
        }

        p.setValue(source, value);
    }

    public void updateParameter(String path, Message msg) {
        MessageRoute mr = new MessageRoute();
        mr.event = path;
        mr.type = "update";
        mr.message = msg;
        addMessage(mr);
    }

    public void handleTriggers(MessageRoute msg, Collection<MessageTrigger> triggers, Collection<MessageRoute> data) {

        //Log.d("StateFlow", "trigger: " + msg.event  );
        for (MessageTrigger trigger : triggers) {
            handleTrigger(msg, trigger, data);
        }
    }
    public void handlePeriodic(Collection<MessageRoute> data) {
        for (MessageTrigger trigger : timeTriggers.values()) {
            trigger.message = getParameter(trigger.source.path);
            handleTrigger(trigger, trigger, data);
        }
    }

    public void handleTrigger(MessageRoute msg, MessageTrigger trigger, Collection<MessageRoute> data) {
        if (trigger.eval(msg)) {

            MessageRoute mr = new MessageRoute(trigger);

            mr.controller = this;
            mr.message = msg.message;
            mr.condition = null;
            mr.event = msg.event;
            mr.source = msg.source;

            data.add(mr);
            trigger.reset();
        }
    }

    public boolean handleFilter(MessageRoute filter, MessageRoute message)
    {
        // Filters only apply to data messages
        if (!message.getBaseType().equals("data"))
            return true;

        // TODO: handle filter's target
        return filter.condition.eval(message);
    }

    /*
     * Event Handling Methods
     */

    public void postEvent(String event)
    {
        MessageRoute mr = MessageRoute.event(event);
        postEvent(mr);
    }

    public void postEvent(String event, Message message)
    {
        MessageRoute mr = MessageRoute.event(event, message);
        postEvent(mr);
    }

    public void postEvent(MessageRoute msg)
    {
        msg.controller = this;
        if (msg.message.id == 0) {
            msg.message.id = nextMessageId.getAndIncrement();
            msg.message.timestamp = cycleTime;
        }

        log("debug", "event", msg.event);
        messages.add(msg);
    }

    /*
     *  Time Methods
     */

    public void resetTime()
    {
        startTime = System.nanoTime();
        stats.clear();
    }
    public long getTime()
    {
        return System.nanoTime() - startTime;
    }
    public double getSeconds()
    {
        return (System.nanoTime() - startTime) / ((double)(1000000000));
    }
    public double getMilli()
    {
        return (System.nanoTime() - startTime) / ((double)(1000000));
    }

    /*
     * Logging Methods
     */

    public void log(String level, String source, String message) {
        if (logHandler != null)
            logHandler.log(level, source, message);

        if (enableLogging) {
            switch (level) {
                case "verbose":
                    Log.v(logTag, String.format("%s: %s", source, message));
                    break;
                case "debug":
                    Log.d(logTag, String.format("%s: %s", source, message));
                    break;
                case "info":
                    Log.i(logTag, String.format("%s: %s", source, message));
                    break;
                case "warning":
                    Log.w(logTag, String.format("%s: %s", source, message));
                    break;
                case "error":
                    Log.e(logTag, String.format("%s: %s", source, message));
                    break;
            }
        }
    }

    /*
     * Component Library Methods
     */

    public ComponentLibrary addLibrary(ComponentLibrary library) {
        libraries.put(library.name, library);
        library.setController(this);
        return library;
    }

    public ComponentLibrary addLibrary(String name) {
        ComponentLibrary cl = new ComponentLibrary();
        cl.name = name;
        return addLibrary(cl);
    }

    public Component getComponent(String path) {
        if (path == null) return null;


        for(ComponentLibrary cl : libraries.values()) {
            Component c = cl.getComponent(path);
            if (c != null) { return c;}
        }


        return null;
    }

    /*
     * Behavior Methods
     */

    public Behavior addBehavior(Behavior behavior) {
        behaviors.put(behavior.name, behavior);
        behavior.setController(this);
        return behavior;
    }

    public Behavior addBehavior(String name) {
        Behavior b = new Behavior();
        b.name = name;
        return addBehavior(b);
    }
}
