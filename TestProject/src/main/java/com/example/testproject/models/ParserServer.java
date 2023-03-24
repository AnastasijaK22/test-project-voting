package com.example.testproject.models;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserServer {
    private static String[] patternCommands;
    private static String[] commandNames;

    static {
        patternCommands = new String[] {
                "save\\s+<\\w+>",
                "load\\s+<\\w+>"
        };
        commandNames = new String[] {
                "save",
                "load"
        };
    }

    public static Map<String, String> parseInput(String input) {
        if (input == null) {
            return null;
        }
        if (input.equals("exit")) {
            return new HashMap<>();
        }
        for (int ind = 0; ind < patternCommands.length; ind++) {
            if (input.matches(patternCommands[ind])) {
                Map<String, String> result = new HashMap<>();
                Matcher matcher = Pattern.compile("<\\w+>").matcher(input);
                String fileName = "";
                if (matcher.find()) {
                    fileName = input.substring(matcher.start() + 1, matcher.end() - 1);
                }
                result.put(commandNames[ind], fileName);
                return result;
            }
        }
        return null;
    }
}
