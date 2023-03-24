package com.example.testproject.clients;

import com.example.testproject.models.Answer;
import com.example.testproject.models.Topic;
import com.example.testproject.models.Vote;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserClient {

    private static String[] patternCommands;
    private static List<Function<String, RequestData>> commands = new ArrayList<>();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final String address = "http://localhost:8080";

    static {
        patternCommands = new String[] {"exit",
                "login\\s+-u=\\w+",
                "create\\s+topic\\s+-n=<(.[^<>]+)>",
                "create\\s+vote\\s+-t=<(.[^<>]+)>",
                "view\\s+-t",
                "view\\s+-t=<(.[^<>]+)>",
                "view\\s+-t=<(.[^<>]+)>\\s+-v=<(.[^<>]+)>",
                "vote\\s+-t=<(.[^<>]+)>\\s+-v=<(.[^<>]+)>",
                "delete\\s+-t=<(.[^<>]+)>\\s+-v=<(.[^<>]+)>"};
        commands.add(ParserClient::parseExit);
        commands.add(ParserClient::parseLogin);
        commands.add(ParserClient::parseCreateTopic);
        commands.add(ParserClient::parseExistTopic);
        commands.add(ParserClient::parseView);
        commands.add(ParserClient::parseViewTopic);
        commands.add(ParserClient::parseViewVote);
        commands.add(ParserClient::parseVote);
        commands.add(ParserClient::parseDelete);
    }

    public static RequestData parseInput(String input) {
        if (input == null) {
            return null;
        }
        input = input.strip();
        for (int ind = 0; ind < patternCommands.length; ind++) {
            if (input.matches(patternCommands[ind])) {
                return commands.get(ind).apply(input);
            }
        }
        return null;
    }

    public static RequestData parseExit(String input) {
        RequestData requestData = new RequestData();
        requestData.setExpectedResult(Expect.EXIT);
        return requestData;
    }

    public static RequestData parseLogin(String input) {
        RequestData requestData = new RequestData();
        Matcher matcher = Pattern.compile("-u=.+").matcher(input);
        String uri = address + "/login?nameUser=";
        if (matcher.find()) {
            uri = uri + input.substring(matcher.start() + 3, matcher.end());
        }
        requestData.setUri(uri);
        requestData.setMethod("GET");
        requestData.setRequestJson(null);
        requestData.setExpectedResult(Expect.STR);
        return requestData;
    }

    public static RequestData parseCreateTopic(String input) {
        RequestData requestData = new RequestData();
        requestData.setUri(address + "/create/topic");
        requestData.setMethod("POST");
        requestData.setExpectedResult(Expect.STR);
        Matcher matcher = Pattern.compile("-n=<.+>").matcher(input);
        if (matcher.find()) {
            Topic topic = new Topic();
            topic.setNameTopic(input.substring(matcher.start() + 4,matcher.end() - 1));
            try {
                requestData.setRequestJson(objectMapper.writeValueAsString(topic));
            } catch (JsonProcessingException e) {
                // e.printStackTrace();
                System.out.println("Something went wrong");
                return null;
            }
        }
        return requestData;
    }

    public static RequestData parseExistTopic(String input) {
        RequestData requestData = new RequestData();
        requestData.setUri(address + "/exist/topic");
        requestData.setExpectedResult(Expect.BOOL);
        requestData.setMethod("POST");
        Matcher matcher = Pattern.compile("-t=<.+>").matcher(input);
        if (matcher.find()) {
            Topic topic = new Topic();
            topic.setNameTopic(input.substring(matcher.start() + 4,matcher.end() - 1));
            // Vote vote = requestVote(topic);
            try {
                requestData.setRequestJson(objectMapper.writeValueAsString(topic));
            } catch (JsonProcessingException e) {
                // e.printStackTrace();
                System.out.println("Something went wrong");
                return null;
            }
        }
        return requestData;
    }

    /*
    public static RequestData parseCreateVote(String input) {
        RequestData requestData = new RequestData();
        requestData.setUri(address + "/create/vote");
        requestData.setExpectedResult(Expect.STR);
        requestData.setMethod("POST");
        Matcher matcher = Pattern.compile("-t=<.+>").matcher(input);
        if (matcher.find()) {
            Topic topic = new Topic();
            topic.setNameTopic(input.substring(matcher.start() + 4,matcher.end() - 1));
            Vote vote = requestVote(topic);
            try {
                requestData.setRequestJson(objectMapper.writeValueAsString(vote));
            } catch (JsonProcessingException e) {
                // e.printStackTrace();
                System.out.println("Something went wrong");
                return null;
            }
        }
        return requestData;
    }*/

    public static RequestData parseView(String input) {
        RequestData requestData = new RequestData();
        requestData.setUri(address + "/view");
        requestData.setMethod("GET");
        requestData.setExpectedResult(Expect.TOPICS);
        return requestData;
    }

    public static RequestData parseViewTopic(String input) {
        RequestData requestData = new RequestData();
        requestData.setUri(address + "/view/topic");
        requestData.setMethod("GET");
        requestData.setExpectedResult(Expect.NAMES_VOTES);
        Map<String, String> request = getTopic(input);
        try {
            requestData.setRequestJson(objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            // e.printStackTrace();
            System.out.println("Something went wrong");
            return null;
        }
        return requestData;
    }

    public static RequestData parseViewVote(String input) {
        RequestData requestData = new RequestData();
        requestData.setUri(address + "/view/topic/vote");
        requestData.setMethod("POST");
        requestData.setExpectedResult(Expect.VOTE);
        Map<String, String> request = getTopicVote(input);
        try {
            requestData.setRequestJson(objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            // e.printStackTrace();
            System.out.println("Something went wrong");
            return null;
        }
        return requestData;
    }

    public static RequestData parseVote(String input) {
        RequestData requestData = new RequestData();
        requestData.setUri(address + "/view/vote/answers");
        requestData.setMethod("POST");
        requestData.setExpectedResult(Expect.ANSWERS);
        Map<String, String> request = getTopicVote(input);
        try {
            requestData.setRequestJson(objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            // e.printStackTrace();
            System.out.println("Something went wrong");
            return null;
        }
        return requestData;
    }


    public static RequestData parseDelete(String input) {
        RequestData requestData = new RequestData();
        requestData.setUri(address + "/delete/topic/vote");
        requestData.setMethod("DELETE");
        requestData.setExpectedResult(Expect.STR);
        Map<String, String> request = getTopicVote(input);
        try {
            requestData.setRequestJson(objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            // e.printStackTrace();
            System.out.println("Something went wrong");
            return null;
        }
        return requestData;
    }
    private static Vote requestVote(Topic topic) {
        Vote vote = new Vote();
        vote.setTopic(topic);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the name of the vote:");
        vote.setNameVote(scanner.nextLine());
        System.out.println("Enter description:");
        vote.setDescriptionVote(scanner.nextLine());
        int count = 0;
        while(count <= 0) {
            System.out.println("Enter count answers:");
            try {
                count = scanner.nextInt();
                if (count <= 0) {
                    System.out.println("Count must be positive!");
                }
            } catch (InputMismatchException e) {
                count = 0;
                scanner.nextLine();
                System.out.println("Incorrect input");
            }
        }
        scanner.nextLine();
        vote.setCountAnswers(count);
        List<Answer> answers = new ArrayList<>();
        for (int ind = 0; ind < count; ind++) {
            System.out.println("Answer " + (ind + 1) + ":");
            Answer answer = new Answer();
            answer.setCountVotes(0);
            answer.setNameAnswer(scanner.nextLine());
            answers.add(answer);
        }
        vote.setAnswers(answers);
        return vote;
    }

    private static String requestAnswer(List<String> answers) {
        Scanner scanner = new Scanner(System.in);
        int index = 0;
        while (index <= 0 || index > answers.size()) {
            System.out.println("Enter index answer");
            try {
                index = scanner.nextInt();
                if (index <= 0 || index > answers.size()) {
                    System.out.println("Incorrect index");
                }
            } catch (InputMismatchException e) {
                index = 0;
                scanner.nextLine();
                System.out.println("Incorrect input");
            }
        }
        return answers.get(index - 1);
    }

    public static RequestData createVote(Topic topic) {
        RequestData requestData = new RequestData();
        requestData.setUri(address + "/create/vote");
        requestData.setExpectedResult(Expect.STR);
        requestData.setMethod("POST");
        Vote vote = requestVote(topic);
        try {
            requestData.setRequestJson(objectMapper.writeValueAsString(vote));
        } catch (JsonProcessingException e) {
            // e.printStackTrace();
            System.out.println("Something went wrong");
            return null;
        }
        return requestData;
    }
    public static RequestData createPutVote(List<String> answers, Map<String, String> input) {
        RequestData requestData = new RequestData();
        requestData.setUri(address + "/vote");
        requestData.setMethod("PUT");
        requestData.setExpectedResult(Expect.STR);
        String answer = requestAnswer(answers);
        input.put("answer", answer);
        try {
            requestData.setRequestJson(objectMapper.writeValueAsString(input));
        } catch (JsonProcessingException e) {
            // e.printStackTrace();
            System.out.println("Something went wrong");
            return null;
        }
        return requestData;
    }
    private static Map<String, String> getTopicVote(String input) {
        Map<String, String> request = new HashMap<>();
        Matcher matcher = Pattern.compile("-t=<.+> ").matcher(input);
        int end = 0;
        if (matcher.find()) {
            end = matcher.end();
            request.put("nameTopic", input.substring(matcher.start() + 4,matcher.end() - 2));
        }
        matcher = Pattern.compile("-v=<.+>").matcher(input);
        if (matcher.find(end)) {
            request.put("nameVote", input.substring(matcher.start() + 4,matcher.end() - 1));
        }
        return request;
    }

    private static Map<String, String> getTopic(String input) {
        Map<String, String> request = new HashMap<>();
        Matcher matcher = Pattern.compile("-t=<.+>").matcher(input);
        if (matcher.find()) {
            request.put("nameTopic", input.substring(matcher.start() + 4,matcher.end() - 1));
        }
        return request;
    }
}
