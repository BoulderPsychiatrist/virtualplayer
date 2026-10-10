package com.xminer.virtualplayer.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Client-side copy of the active bot names (no client-only classes here). */
public final class BotClientState {
    private static volatile List<String> bots = Collections.emptyList();

    private BotClientState() {}

    public static void setBots(List<String> names) {
        bots = Collections.unmodifiableList(new ArrayList<String>(names));
    }

    public static List<String> getBots() {
        return bots;
    }
}
