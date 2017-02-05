package org.mechadojo.stateflow;

public interface MessageCondition {
    boolean eval(MessageRoute msg );
}
