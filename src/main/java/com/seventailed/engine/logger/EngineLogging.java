package com.seventailed.engine.logger;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EngineLogging {
    private static HashMap<String, String> loggerPrefixes = new HashMap<>();
    private static HashMap<String, String> colors = new HashMap<>();

    private static Pattern pattern = Pattern.compile("&\\w");

    private static Terminal terminal;
    private static LineReader lineReader;

    private static String RESET = "\u001b[0m";

    private static boolean initialized = false;

    public static char escapeChar = '\u001b';

    public static void initializeColors() {
        colors.put("0", "\u001b[30m");
        colors.put("1", "\u001b[34m");
        colors.put("2", "\u001b[32m");
        colors.put("3", "\u001b[36m");
        colors.put("4", "\u001b[31m");
        colors.put("5", "\u001b[35m");
        colors.put("6", "\u001b[38;5;202m");
        colors.put("7", "\u001b[38;5;247m");
        colors.put("8", "\u001b[38;5;240m");
        colors.put("9", "\u001b[38;5;24m");

        colors.put("f", "\u001b[38;5;15m");
        colors.put("c", "\u001b[38;2;255;141;133m");
        colors.put("r", "\u001b[0m");
    }

    public static void initializeStream() throws Exception {
        terminal = TerminalBuilder.builder().dumb(true).system(true).streams(System.in, System.out).build();
        lineReader = LineReaderBuilder.builder().terminal(terminal).build();

        loggerPrefixes.put("seventailed", "&2SevenTailed");
        loggerPrefixes.put("commands","&cCommands");
        loggerPrefixes.put("authentication", "&6Authentication");
        loggerPrefixes.put("email", "&5Email");
        loggerPrefixes.put("account", "&1Account");
        loggerPrefixes.put("event", "&3Event");
        loggerPrefixes.put("chat", "&9Chat");

        initialized = true;
    }

    public static String parseColors(String message) {
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String color = matcher.group().toCharArray()[1] + "";
            if(colors.containsKey(color)) {
                message = message.replace(matcher.group(), colors.get(color));
            }
        }


        return message + RESET;
    }
    public static void clearScreen() {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.flush();
    }

    public static void log(String message) {
        lineReader.printAbove(parseColors(message));
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static LineReader getLineReader() {
        return lineReader;
    }

    public static HashMap<String, String> getLoggerPrefixes() {
        return loggerPrefixes;
    }
}
