/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.utils.state;

import com.ticxo.modelengine.api.utils.state.StateNode;
import java.util.function.Function;
import lombok.Generated;

public class StateMachine<T> {
    protected StateNode<T> currentNode;

    public void setEntryNode(StateNode<T> entryNode) {
        this.currentNode = entryNode;
    }

    public StateNode<T> createNode() {
        return new StateNode(this);
    }

    public void execute(T target) {
        StateNode<T> node;
        boolean updated = false;
        for (Function<T, StateNode<T>> processor : this.currentNode.getForceConnected()) {
            node = processor.apply(target);
            if (node == null) continue;
            this.currentNode.acceptExit(target);
            this.currentNode = node;
            this.currentNode.acceptEntry(target);
            updated = true;
            break;
        }
        if (!updated && this.currentNode.testCommonPredicate(target)) {
            for (Function<T, StateNode<T>> processor : this.currentNode.getConnected()) {
                node = processor.apply(target);
                if (node == null) continue;
                this.currentNode.acceptExit(target);
                this.currentNode = node;
                this.currentNode.acceptEntry(target);
                break;
            }
        }
        if (this.currentNode != null) {
            this.currentNode.acceptAction(target);
        }
    }

    @Generated
    public StateNode<T> getCurrentNode() {
        return this.currentNode;
    }
}

