package com.example.testproject.clients;

import com.example.testproject.models.Topic;
import com.example.testproject.models.Vote;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Client {

    ObjectMapper objectMapper = new ObjectMapper();
    private final Connector connector = new Connector();

    public void printResponse(RequestData requestData) {
        System.out.println(connector.sendRequest(requestData));
    }

    public void startSession() {
        boolean flag = true;
        Scanner scanner = new Scanner(System.in);
        while (flag) {
            String input = scanner.nextLine();
            RequestData requestData = ParserClient.parseInput(input);
            if (requestData == null) {
                System.out.println("Incorrect input");
            } else {
                if (requestData.getExpectedResult() == Expect.EXIT) {
                    break;
                }
                validator(requestData);
            }
        }
        scanner.close();
    }

    private void validator(RequestData requestData) {
        List<String> line = connector.sendRequest(requestData);
        if (line == null || line.size() == 0) {
            System.out.println("Something went wrong");
        } else {
            switch (requestData.getExpectedResult()) {
                case STR: {
                    System.out.println(line.get(0));
                    break;
                }
                case NAMES_VOTES: {
                    try {
                        List<String> votes = objectMapper.readValue(line.get(0),
                                new TypeReference<List<String>>() {
                                });
                        Map<String, String> input = objectMapper.readValue(requestData.getRequestJson(),
                                new TypeReference<Map<String, String>>() {});
                        System.out.println("< " + input.get("nameTopic"));
                        for (int ind = 0; ind < votes.size(); ind++) {
                            System.out.println((ind + 1) + " " + votes.get(ind));
                        }
                        System.out.println(" >");

                    } catch (JsonParseException e) {
                        System.out.println(line.get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case BOOL: {
                    if (line.get(0).equals("true")) {
                        try {
                            Topic topic = objectMapper.readValue(requestData.getRequestJson(), Topic.class);
                            RequestData requestCreateVote = ParserClient.createVote(topic);
                            validator(requestCreateVote);
                        } catch (JsonParseException e) {
                            System.out.println(line.get(0));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println(connector.sendRequest(requestData).get(0));
                    }
                    break;
                }
                case VOTE: {
                    try {
                        Vote vote = objectMapper.readValue(line.get(0), Vote.class);
                        System.out.println(vote);
                    } catch (JsonParseException e) {
                        System.out.println(line.get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case TOPICS: {
                    try {
                        Map<String, Integer> topics = objectMapper.readValue(line.get(0),
                                new TypeReference<Map<String, Integer>>() {
                                });
                        for (Map.Entry<String, Integer> topic : topics.entrySet()) {
                            System.out.println("< " + topic.getKey() + " (votes in topic " + topic.getValue() + ")>");
                        }
                    } catch (JsonParseException e) {
                        System.out.println(line.get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case ANSWERS: {
                    try {
                        List<String> answers = objectMapper.readValue(line.get(0),
                                new TypeReference<List<String>>() {
                                });
                        for (int ind = 0; ind < answers.size(); ind++) {
                            System.out.println((ind + 1) + ". " + answers.get(ind));
                        }
                        Map<String, String> input = objectMapper.readValue(requestData.getRequestJson(),
                                new TypeReference<Map<String, String>>() {
                                });
                        RequestData secondRequestData = ParserClient.createPutVote(answers, input);
                        validator(secondRequestData);
                        //System.out.println(connector.sendRequest(secondRequestData).get(0));

                    } catch (JsonParseException e) {
                        System.out.println(line.get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.startSession();
    }
}
