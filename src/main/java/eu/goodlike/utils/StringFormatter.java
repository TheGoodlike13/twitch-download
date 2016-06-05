package eu.goodlike.utils;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public final class StringFormatter {

    public static String format(String string, Object any) {
        return format(MessageFormatter.format(string, any));
    }

    public static String format(String string, Object any1, Object any2) {
        return format(MessageFormatter.format(string, any1, any2));
    }

    public static String format(String string, Object... any) {
        return format(MessageFormatter.arrayFormat(string, any));
    }

    // PRIVATE

    private StringFormatter() {
        throw new AssertionError("Do not instantiate, use static methods!");
    }

    private static String format(FormattingTuple formattingTuple) {
        String result = formattingTuple.getMessage();
        if (result == null)
            throw new IllegalArgumentException("Given string cannot be formatter using given object");

        return result;
    }

}
