package ru.vyarus.spock.jupiter.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper storage for collecting lifecycle events.
 *
 * @author Vyacheslav Rusakov
 * @since 26.11.2021
 */
public class ActionHolder {

    private static ThreadLocal<List<String>> STATE = new ThreadLocal<>();

    public static void cleanup() {
        STATE.remove();
    }

    public static void add(String state) {
        List<String> st = STATE.get();
        if (st == null) {
            st = new ArrayList<>();
            STATE.set(st);
        }
        st.add(state);
    }

    public static List<String> getState() {
        return STATE.get() == null ? Collections.emptyList() : new ArrayList<>(STATE.get());
    }
}
