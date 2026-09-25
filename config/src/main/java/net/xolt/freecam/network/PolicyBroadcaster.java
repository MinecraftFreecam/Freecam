package net.xolt.freecam.network;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiPredicate;

/** Tracks delivery per connection, retrying until the peer advertises support. Server-thread only. */
public final class PolicyBroadcaster<P> {
    private final Map<P, ServerPolicy> delivered = new IdentityHashMap<>();

    public void tick(Collection<P> players, ServerPolicy policy, BiPredicate<P, ServerPolicy> send) {
        Set<P> connected = Collections.newSetFromMap(new IdentityHashMap<>());
        connected.addAll(players);
        delivered.keySet().removeIf(player -> !connected.contains(player));
        for (P player : players) {
            if (!policy.equals(delivered.get(player)) && send.test(player, policy)) {
                delivered.put(player, policy);
            }
        }
    }
}
