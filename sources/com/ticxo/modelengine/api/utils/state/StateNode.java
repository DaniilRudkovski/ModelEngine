/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.utils.state;

import com.ticxo.modelengine.api.utils.state.StateMachine;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Generated;

public class StateNode<T> {
    private final StateMachine<T> machine;
    private final List<Function<T, StateNode<T>>> forceConnected = new ArrayList<Function<T, StateNode<T>>>();
    private final List<Function<T, StateNode<T>>> connected = new ArrayList<Function<T, StateNode<T>>>();
    private Consumer<T> entryAction;
    private Consumer<T> action;
    private Consumer<T> exitAction;
    private Predicate<T> commonPredicate;
    private Predicate<T> entryPredicate;

    public void addForceConnectedNode(Predicate<T> condition, StateNode<T> node) {
        this.addForceConnectedNode(condition, (T t) -> node);
    }

    public void addForceConnectedNode(Predicate<T> condition, Function<T, StateNode<T>> node) {
        this.addForceNodeProcessor(t -> {
            if (!condition.test(t)) {
                return null;
            }
            StateNode n = (StateNode)node.apply(t);
            return n.testEntryPredicate(t) ? n : null;
        });
    }

    public void addForceNodeProcessor(Function<T, StateNode<T>> node) {
        this.forceConnected.add(node);
    }

    public void clearForceConnectedNodes() {
        this.forceConnected.clear();
    }

    public void addConnectedNode(Predicate<T> condition, StateNode<T> node) {
        this.addConnectedNode(condition, (T t) -> node);
    }

    public void addConnectedNode(Predicate<T> condition, Function<T, StateNode<T>> node) {
        this.addNodeProcessor(t -> {
            if (!condition.test(t)) {
                return null;
            }
            StateNode n = (StateNode)node.apply(t);
            return n.testEntryPredicate(t) ? n : null;
        });
    }

    public void addNodeProcessor(Function<T, StateNode<T>> node) {
        this.connected.add(node);
    }

    public void clearConnectedNodes() {
        this.connected.clear();
    }

    public void acceptAction(T target) {
        if (this.action != null) {
            this.action.accept(target);
        }
    }

    public void acceptEntry(T target) {
        if (this.entryAction != null) {
            this.entryAction.accept(target);
        }
    }

    public void acceptExit(T target) {
        if (this.exitAction != null) {
            this.exitAction.accept(target);
        }
    }

    public boolean testCommonPredicate(T target) {
        return this.commonPredicate == null || this.commonPredicate.test(target);
    }

    public boolean testEntryPredicate(T target) {
        return this.entryPredicate == null || this.entryPredicate.test(target);
    }

    @Generated
    public StateNode(StateMachine<T> machine) {
        this.machine = machine;
    }

    @Generated
    public StateMachine<T> getMachine() {
        return this.machine;
    }

    @Generated
    public List<Function<T, StateNode<T>>> getForceConnected() {
        return this.forceConnected;
    }

    @Generated
    public List<Function<T, StateNode<T>>> getConnected() {
        return this.connected;
    }

    @Generated
    public Consumer<T> getEntryAction() {
        return this.entryAction;
    }

    @Generated
    public Consumer<T> getAction() {
        return this.action;
    }

    @Generated
    public Consumer<T> getExitAction() {
        return this.exitAction;
    }

    @Generated
    public Predicate<T> getCommonPredicate() {
        return this.commonPredicate;
    }

    @Generated
    public Predicate<T> getEntryPredicate() {
        return this.entryPredicate;
    }

    @Generated
    public void setEntryAction(Consumer<T> entryAction) {
        this.entryAction = entryAction;
    }

    @Generated
    public void setAction(Consumer<T> action) {
        this.action = action;
    }

    @Generated
    public void setExitAction(Consumer<T> exitAction) {
        this.exitAction = exitAction;
    }

    @Generated
    public void setCommonPredicate(Predicate<T> commonPredicate) {
        this.commonPredicate = commonPredicate;
    }

    @Generated
    public void setEntryPredicate(Predicate<T> entryPredicate) {
        this.entryPredicate = entryPredicate;
    }
}

