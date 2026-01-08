/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package org.jsoup.select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Evaluator;
import org.jspecify.annotations.Nullable;

public abstract class CombiningEvaluator
extends Evaluator {
    final ArrayList<Evaluator> evaluators = new ArrayList();
    final ArrayList<Evaluator> sortedEvaluators = new ArrayList();
    int num = 0;
    int cost = 0;
    private static final Comparator<Evaluator> costComparator = (o1, o2) -> o1.cost() - o2.cost();

    CombiningEvaluator() {
    }

    CombiningEvaluator(Collection<Evaluator> evaluators) {
        this();
        this.evaluators.addAll(evaluators);
        this.updateEvaluators();
    }

    @Override
    protected void reset() {
        for (Evaluator evaluator : this.evaluators) {
            evaluator.reset();
        }
        super.reset();
    }

    @Override
    protected int cost() {
        return this.cost;
    }

    @Nullable Evaluator rightMostEvaluator() {
        return this.num > 0 ? this.evaluators.get(this.num - 1) : null;
    }

    void replaceRightMostEvaluator(Evaluator replacement) {
        this.evaluators.set(this.num - 1, replacement);
        this.updateEvaluators();
    }

    void updateEvaluators() {
        this.num = this.evaluators.size();
        this.cost = 0;
        for (Evaluator evaluator : this.evaluators) {
            this.cost += evaluator.cost();
        }
        this.sortedEvaluators.clear();
        this.sortedEvaluators.addAll(this.evaluators);
        Collections.sort(this.sortedEvaluators, costComparator);
    }

    public static final class Or
    extends CombiningEvaluator {
        Or(Collection<Evaluator> evaluators) {
            if (this.num > 1) {
                this.evaluators.add(new And(evaluators));
            } else {
                this.evaluators.addAll(evaluators);
            }
            this.updateEvaluators();
        }

        Or(Evaluator ... evaluators) {
            this(Arrays.asList(evaluators));
        }

        Or() {
        }

        public void add(Evaluator e) {
            this.evaluators.add(e);
            this.updateEvaluators();
        }

        @Override
        public boolean matches(Element root, Element node) {
            for (int i = 0; i < this.num; ++i) {
                Evaluator s = (Evaluator)this.sortedEvaluators.get(i);
                if (!s.matches(root, node)) continue;
                return true;
            }
            return false;
        }

        public String toString() {
            return StringUtil.join(this.evaluators, ", ");
        }
    }

    public static final class And
    extends CombiningEvaluator {
        And(Collection<Evaluator> evaluators) {
            super(evaluators);
        }

        And(Evaluator ... evaluators) {
            this(Arrays.asList(evaluators));
        }

        @Override
        public boolean matches(Element root, Element element) {
            for (int i = 0; i < this.num; ++i) {
                Evaluator s = (Evaluator)this.sortedEvaluators.get(i);
                if (s.matches(root, element)) continue;
                return false;
            }
            return true;
        }

        public String toString() {
            return StringUtil.join(this.evaluators, "");
        }
    }
}

