package com.basler182.spelvalidationstarter;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Holds context information for SpEL validation, specifically parameter names
 * for method validation.
 */
public class SpelContextHolder {

    private static final ThreadLocal<Deque<String[]>> PARAMETER_NAMES_STACK = ThreadLocal.withInitial(ArrayDeque::new);

    private SpelContextHolder() {
        // utility class
    }

    /**
     * Pushes parameter names onto the stack.
     * 
     * @param names the parameter names
     */
    public static void pushParameterNames(String[] names) {
        PARAMETER_NAMES_STACK.get().push(names);
    }

    /**
     * Pops parameter names from the stack.
     */
    public static void popParameterNames() {
        Deque<String[]> stack = PARAMETER_NAMES_STACK.get();
        if (!stack.isEmpty()) {
            stack.pop();
        }
        if (stack.isEmpty()) {
            PARAMETER_NAMES_STACK.remove();
        }
    }

    /**
     * Gets the current parameter names from the top of the stack.
     * 
     * @return the parameter names, or null if none are available
     */
    public static String[] peekParameterNames() {
        return PARAMETER_NAMES_STACK.get().peek();
    }
}
