/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.utils.ticker;

import com.ticxo.modelengine.api.utils.ticker.LoadBalancer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import lombok.Generated;

public abstract class AbstractLoadBalancer<KEY, SER extends LoadBalancer.Server>
implements LoadBalancer<KEY, SER> {
    protected final Set<SER> available = new HashSet<SER>();
    protected final Map<KEY, SER> reference = new HashMap<KEY, SER>();

    public AbstractLoadBalancer(int counts) {
        if (counts <= 0) {
            throw new RuntimeException("Cannot create load balancer with less than 1 server: " + counts);
        }
        for (int i = 0; i < counts; ++i) {
            this.available.add(this.supply());
        }
    }

    @Override
    public SER getOrRegister(KEY key) {
        LoadBalancer.Server current = (LoadBalancer.Server)this.reference.get(key);
        if (current != null) {
            return (SER)current;
        }
        int load = Integer.MAX_VALUE;
        LoadBalancer.Server leastLoad = null;
        for (LoadBalancer.Server server : this.available) {
            if (server.getLoad() >= load) continue;
            leastLoad = server;
            load = server.getLoad();
        }
        if (leastLoad == null) {
            throw new RuntimeException("No server found.");
        }
        this.reference.put(key, leastLoad);
        return (SER)leastLoad;
    }

    @Override
    public SER get(KEY key) {
        return (SER)((LoadBalancer.Server)this.reference.get(key));
    }

    @Override
    public void unregister(KEY key) {
        this.reference.remove(key);
    }

    @Override
    public void execute(KEY key, BiConsumer<KEY, SER> balConsumer) {
        balConsumer.accept(key, this.getOrRegister(key));
    }

    @Generated
    public Set<SER> getAvailable() {
        return this.available;
    }
}

