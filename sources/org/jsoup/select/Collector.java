/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package org.jsoup.select;

import java.util.Optional;
import java.util.stream.Collectors;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.jspecify.annotations.Nullable;

public class Collector {
    private Collector() {
    }

    public static Elements collect(Evaluator eval, Element root) {
        eval.reset();
        return root.stream().filter(eval.asPredicate(root)).collect(Collectors.toCollection(Elements::new));
    }

    public static @Nullable Element findFirst(Evaluator eval, Element root) {
        eval.reset();
        Optional<Element> first = root.stream().filter(eval.asPredicate(root)).findFirst();
        return first.orElse(null);
    }
}

