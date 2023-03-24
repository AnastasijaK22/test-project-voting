package com.example.testproject.services;

import com.example.testproject.models.Topic;
import com.example.testproject.models.Vote;
import com.example.testproject.repositories.MainRepository;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServerCommand {

    String separator = File.separator;
    String nameDirectory = "saves";
    MainRepository mainRepository;

    public ServerCommand(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }

    public void save(String filename) {
        List<String> users = mainRepository.getAllUsers();
        List<Topic> topics = mainRepository.getAllNameTopics();
        List<String> nameUsers = new ArrayList<>();
        List<Vote> votes = mainRepository.getAllVote(nameUsers);
        try(ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(nameDirectory + separator + filename + ".bin"))) {
            oos.writeObject(users);
            oos.writeObject(topics);
            oos.writeObject(nameUsers);
            oos.writeObject(votes);
            System.out.println("Success save");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void load(String filename) {
        try(ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream( nameDirectory + separator + filename + ".bin"))) {
            List<String> users = (List<String>) ois.readObject();
            List<Topic> topics = (List<Topic>) ois.readObject();
            List<String> nameUsersCreators = (List<String>) ois.readObject();
            List<Vote> votes = (List<Vote>) ois.readObject();

            Map<String, Integer> usersMap = new HashMap<>();
            for (String user : users) {
                Integer idUser = mainRepository.getIdUserByName(user);
                if (idUser == null) {
                    mainRepository.createUser(user);
                }
                idUser = mainRepository.getIdUserByName(user);
                usersMap.put(user, idUser);
            }
            Map<String, Integer> topicsMap = new HashMap<>();
            for (Topic topic : topics) {
                String nameTopic = topic.getNameTopic();
                mainRepository.createTopic(nameTopic);
                topicsMap.put(nameTopic, mainRepository.getIdTopicByName(nameTopic));
            }
            for (int ind = 0; ind < votes.size(); ind++) {
                Vote vote = votes.get(ind);
                String nameCreator = nameUsersCreators.get(ind);
                Integer idCreator = usersMap.get(nameCreator);
                String nameTopic = vote.getTopic().getNameTopic();
                Integer idTopic = topicsMap.get(nameTopic);
                mainRepository.createVote(idCreator, idTopic, vote);
            }
            System.out.println("Success load");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
