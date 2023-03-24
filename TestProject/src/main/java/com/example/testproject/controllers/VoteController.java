package com.example.testproject.controllers;

import com.example.testproject.models.Answer;
import com.example.testproject.models.Topic;
import com.example.testproject.models.Vote;
import com.example.testproject.repositories.MainRepository;
import com.example.testproject.services.LoggedUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class VoteController {
    private final MainRepository mainRepository;
    private final LoggedUserService loggedUserService;

    public VoteController(MainRepository mainRepository, LoggedUserService loggedUserService) {
        this.mainRepository = mainRepository;
        this.loggedUserService = loggedUserService;
    }

    @PostMapping("/create/topic")
    public String createTopic(@RequestBody Topic topic) {
        if (loggedUserService.getIdUser() == null) {
            return "Access denied\n";
        }
        boolean answer = mainRepository.createTopic(topic.getNameTopic());
        return answer ? "Topic created\n" : "Topic already exists\n";
    }

    @PostMapping("/create/vote")
    public String createVote(@RequestBody Vote vote) {
        if (loggedUserService.getIdUser() == null) {
            return "Access denied\n";
        }

        Integer idTopic = mainRepository.getIdTopicByName(vote.getTopic().getNameTopic());
        if (idTopic == null) {
            return "Topic don't exist\n";
        }
        if (vote.getCountAnswers() != vote.getAnswers().size()) {
            return "Incorrect data\n";
        }
        Set<Answer> answerSet = new HashSet<>(vote.getAnswers());
        if (answerSet.size() != vote.getCountAnswers()) {
            return "Incorrect answers\n";
        }
        boolean answer = mainRepository.createVote(loggedUserService.getIdUser(), idTopic, vote);

        return answer ? "Vote created\n" : "Vote already exist\n";
    }

    @PostMapping("/view/topic/vote")
    public String viewVote(@RequestBody Map<String, String> input) throws JsonProcessingException {
        if (loggedUserService.getIdUser() == null) {
            return "Access denied\n";
        }
        Integer[] ids = idTopicVote(input);
        if (ids[0] == null) {
            return "Topic don't exist\n";
        }
        if (ids[1] == null) {
            return "Vote don't exist\n";
        }

        Vote vote = mainRepository.getVoteById(ids[0], ids[1]);
        vote.getTopic().setNameTopic(input.get("nameTopic"));
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(vote);
    }

    @PostMapping("/view/topic")
    public String viewConcreteTopic(@RequestBody Map<String, String> input) throws JsonProcessingException {
        if (loggedUserService.getIdUser() == null) {
            return "Access denied\n";
        }
        Integer idTopic = mainRepository.getIdTopicByName(input.get("nameTopic"));
        if (idTopic == null) {
            return "Topic don't exist\n";
        }
        List<String> nameVotes = mainRepository.getNameVotesByIdTopic(idTopic);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(nameVotes);
    }

    @GetMapping("/view")
    public String viewTopics() throws JsonProcessingException {
        if (loggedUserService.getIdUser() == null) {
            return "Access denied\n";
        }
        Map<String, Integer> result = mainRepository.getAllTopics();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(result);
    }

    @PostMapping("/exist/topic")
    public String isTopicExist(@RequestBody Topic topic) {
        if (loggedUserService.getIdUser() == null) {
            return "Access denied\n";
        }
        Integer idTopic = mainRepository.getIdTopicByName(topic.getNameTopic());
        return idTopic != null ? "true" : "Topic don't exist\n";
    }

    @DeleteMapping("/delete/topic/vote")
    public String deleteVote(@RequestBody Map<String, String> input) {
        if (loggedUserService.getIdUser() == null) {
            return "Access denied\n";
        }

        Integer idUser = loggedUserService.getIdUser();
        Integer[] ids = idTopicVote(input);
        if (ids[0] == null) {
            return "Topic don't exist\n";
        }
        if (ids[1] == null) {
            return "Vote don't exist\n";
        }
        // System.out.println(idUser);
        boolean answer = mainRepository.isCreator(idUser, ids[0], ids[1]);
        if (!answer) {
            return "User not creator\n";
        }
        mainRepository.deleteVote(ids[0], ids[1]);

        return "Deleted successfully\n";
    }

    @PostMapping("/view/vote/answers")
    public String viewAnswers(@RequestBody Map<String, String> input) throws JsonProcessingException {
        if (loggedUserService.getIdUser() == null) {
            return "Access denied\n";
        }

        Integer[] ids = idTopicVote(input);
        if (ids[0] == null) {
            return "Topic don't exist\n";
        }
        if (ids[1] == null) {
            return "Vote don't exist\n";
        }

        List<String> answers = mainRepository.getAnswersByIdTopicVote(ids[0], ids[1]);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(answers);
    }

    @PutMapping("/vote")
    public String patchAnswer(@RequestBody Map<String, String> input) {
        if (loggedUserService.getIdUser() == null) {
            return "Access denied\n";
        }
        Integer[] ids = idTopicVote(input);
        String answer = input.get("answer");
        mainRepository.incrementAnswer(ids[0], ids[1], answer);
        return "Your vote has been counted\n";
    }

    private Integer[] idTopicVote(Map<String, String> input) {
        String nameTopic = input.get("nameTopic");
        String nameVote = input.get("nameVote");
        Integer[] ids = new Integer[] {null, null};
        ids[0] = mainRepository.getIdTopicByName(nameTopic);
        if (ids[0] == null) {
            return ids;
        }
        ids[1] = mainRepository.getIdVoteByName(ids[0], nameVote);
        return ids;
    }
}
