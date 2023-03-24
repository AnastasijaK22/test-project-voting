package com.example.testproject.repositories;

import com.example.testproject.models.Answer;
import com.example.testproject.models.Topic;
import com.example.testproject.models.Vote;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MainRepository {
    private final JdbcTemplate jdbc;

    public MainRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Integer getIdUserByName(String nameUser) {
        String sql = "SELECT idUser from users WHERE nameUser=" +
                "\'" + nameUser + "\'";
        List<Integer> selected = jdbc.query(sql, (r, i) -> r.getInt("idUser"));
        if (selected.size() != 0) {
            return selected.get(0);
        }
        return null;
    }

    public Integer getIdTopicByName(String nameTopic) {
        String sql = "SELECT idTopic from topics WHERE nameTopic=" +
                "\'" + nameTopic + "\'";
        List<Integer> selected = jdbc.query(sql, (r, i) -> r.getInt("idTopic"));
        if (selected.size() != 0) {
            return selected.get(0);
        }
        return null;
    }

    public Integer getIdVoteByName(Integer idTopic, String nameVote) {
        String tableName = "topic" + idTopic.toString() + "Votes";
        String sql = "SELECT idVote from " + tableName + " WHERE nameVote=" +
                "\'" + nameVote + "\'";
        List<Integer> selected = jdbc.query(sql, (r, i) -> r.getInt("idVote"));
        if (selected.size() != 0) {
            return selected.get(0);
        }
        return null;
    }

    public Vote getVoteById(Integer idTopic, Integer idVote) {
        String tableVote = "topic" + idTopic.toString() + "Votes";

        String sqlVote = "SELECT nameVote, descriptionVote FROM " + tableVote +
                " WHERE idVote=" + idVote.toString();
        List<Vote> selected = jdbc.query(sqlVote, (r, i) -> {
            Vote temp = new Vote();
            temp.setNameVote(r.getString("nameVote"));
            temp.setDescriptionVote(r.getString("descriptionVote"));
            return temp;});

        List<Answer> answers = getAnswersAllByIdTopicVote(idTopic, idVote);
        selected.get(0).setAnswers(answers);
        selected.get(0).setCountAnswers(answers.size());
        return selected.get(0);
    }

    public List<Answer> getAnswersAllByIdTopicVote(Integer idTopic, Integer idVote) {
        String tableAnswer = "topic" + idTopic.toString() + "Answers";
        String sqlAnswers = "SELECT answer, countVotes FROM " + tableAnswer +
                " WHERE idVote=" + idVote.toString();
        List<Answer> answers = jdbc.query(sqlAnswers, (r, i) -> {
            Answer temp = new Answer();
            temp.setNameAnswer(r.getString("answer"));
            temp.setCountVotes(r.getInt("countVotes"));
            return temp;
        });
        return answers;
    }
    public List<String> getAnswersByIdTopicVote(Integer idTopic, Integer idVote) {
        String tableAnswer = "topic" + idTopic.toString() + "Answers";

        String sqlAnswers = "SELECT answer FROM " + tableAnswer +
                " WHERE idVote=" + idVote.toString();
        List<String> answers = jdbc.query(sqlAnswers, (r, i) -> r.getString("answer"));
        return answers;
    }

    public Map<String, Integer> getAllTopics() {
        String sql = "SELECT * FROM topics";
        List<Integer> idTopics = new ArrayList<>();
        List<String> selected = jdbc.query(sql, (r, i) -> {
           idTopics.add(r.getInt("idTopic"));
           return r.getString("nameTopic");
        });
        Map<String, Integer> result = new HashMap<>();
        for (int ind = 0; ind < selected.size(); ind++) {
            Integer count = getCountVotes(idTopics.get(ind));
            result.put(selected.get(ind), count);
        }
        return result;
    }

    public List<String> getNameVotesByIdTopic(Integer idTopic) {
        String tableVote = "topic" + idTopic.toString() + "Votes";
        String sql = "SELECT nameVote FROM " + tableVote;
        List<String> selected = jdbc.query(sql, (r, i) -> r.getString("nameVote"));
        return selected;
    }
    public Integer getCountVotes(Integer idTopic) {
        String tableVote = "topic" + idTopic.toString() + "Votes";
        String sql = "SELECT COUNT(*) AS countVotes FROM " + tableVote;
        List<Integer> selected = jdbc.query(sql, (r, i)-> r.getInt("countVotes"));
        if (selected.size() == 0) {
            return 0;
        }
        return selected.get(0);
    }

    public void incrementAnswer(Integer idTopic, Integer idVote, String answer) {
        String tableAnswer = "topic" + idTopic.toString() + "Answers";
        String sql = "UPDATE " + tableAnswer + " SET countVotes=countVotes + 1" +
                    " WHERE answer=" + "\'" + answer +"\'" + " AND idVote=" + idVote.toString();
        jdbc.update(sql);
    }

    public void deleteVote(Integer idTopic, Integer idVote) {
        String tableVote = "topic" + idTopic.toString() + "Votes";
        String tableAnswer = "topic" + idTopic.toString() + "Answers";
        String sql = "DELETE FROM " + tableAnswer +" WHERE idVote=" + idVote.toString();
        jdbc.update(sql);
        sql = "DELETE FROM " + tableVote +" WHERE idVote=" + idVote.toString();
        jdbc.update(sql);
    }

    public boolean isCreator(Integer idUser, Integer idTopic, Integer idVote) {
        String tableVote = "topic" + idTopic.toString() + "Votes";
        String sqlTest = "SELECT idCreator FROM " + tableVote + " WHERE idVote=" + idVote.toString();
        List<Integer> selected = jdbc.query(sqlTest, (r, i) -> r.getInt("idCreator"));
        if (selected.size() == 0) {
            return false;
        }
        return selected.get(0) == idUser;
    }

    public void createUser(String nameUser) {
        String sqlInsert = "INSERT INTO users(nameUser) VALUES(?)";
        jdbc.update(sqlInsert, nameUser);
    }

    public boolean createTopic(String nameTopic) {
        if (getIdTopicByName(nameTopic) != null) {
            return false;
        }
        String sqlInsert = "INSERT INTO topics(nameTopic) VALUES(?)";
        jdbc.update(sqlInsert, nameTopic);

        Integer id = getIdTopicByName(nameTopic);
        createTopicVotes(id);
        createTopicAnswers(id);
        return true;
    }
    public boolean createVote(Integer idCreator, Integer idTopic, Vote vote) {
        if (getIdVoteByName(idTopic, vote.getNameVote()) != null) {
            return false;
        }
        String tableVotes = "topic" + idTopic.toString() + "Votes";
        String tableAnswers = "topic" + idTopic.toString() + "Answers";

        String sql = "INSERT INTO " + tableVotes + "(nameVote, descriptionVote, idCreator) " +
                "VALUES(?, ?, ?)";
        jdbc.update(sql, vote.getNameVote(), vote.getDescriptionVote(), idCreator);

        addAnswers(tableAnswers, getIdVoteByName(idTopic, vote.getNameVote()), vote.getAnswers());

        return true;
    }

    private void addAnswers(String tableAnswers, Integer idVote, List<Answer> answers) {
        for (int ind = 0; ind < answers.size(); ind++) {
            String sql = "INSERT INTO " + tableAnswers + "(answer, countVotes, idVote) " +
                    "VALUES(?, ?, ?)";
            jdbc.update(sql, answers.get(ind).getNameAnswer(), answers.get(ind).getCountVotes(), idVote);
        }
    }
    private void createTopicVotes(Integer idTopic) {
        String name = "topic" + idTopic.toString() + "Votes";
        String sqlCreateVotes = "CREATE TABLE " + name + " (" +
                " idVote INT AUTO_INCREMENT,\n" +
                " nameVote VARCHAR(60) UNIQUE NOT NULL,\n" +
                " descriptionVote TEXT,\n" +
                " idCreator INT,\n" +
                " PRIMARY KEY(idVote),\n" +
                " FOREIGN KEY(idCreator) REFERENCES users(idUser))";
        jdbc.execute(sqlCreateVotes);
    }

    private void createTopicAnswers(Integer idTopic) {
        String name = "topic" + idTopic.toString();
        String nameVotes = name + "Votes";
        String nameAnswers = name + "Answers";
        String sqlCreateAnswers = "CREATE TABLE " + nameAnswers + " (" +
                " idAns INT AUTO_INCREMENT,\n" +
                " answer VARCHAR(60),\n" +
                " countVotes INT,\n" +
                " idVote INT,\n" +
                " PRIMARY KEY(idAns),\n" +
                " FOREIGN KEY(idVote) REFERENCES " + nameVotes + "(idVote))";
        jdbc.execute(sqlCreateAnswers);
    }

    public List<String> getAllUsers() {
        String sql = "SELECT nameUser FROM users";
        List<String> users = jdbc.query(sql, (r, i) -> r.getString("nameUser"));
        return users;
    }

    public List<Topic> getAllNameTopics() {
        String sql = "SELECT nameTopic FROM topics";
        List<Topic> topics = jdbc.query(sql, (r, i) -> {
            Topic topic = new Topic();
            topic.setNameTopic(r.getString("nameTopic"));
            return topic;
        });
        return topics;
    }

    public List<Vote> getAllVote(List<String> nameCreators) {
        String sql = "SELECT * FROM topics";
        List<String> nameTopics = new ArrayList<>();
        List<Integer> idTopics = jdbc.query(sql, (r, i) -> {
            nameTopics.add(r.getString("nameTopic"));
            return r.getInt("idTopic");
        });
        List<Vote> allVotes = new ArrayList<>();
        for (int ind = 0; ind < idTopics.size(); ind++) {
            Integer idTopic = idTopics.get(ind);
            String nameTopic = nameTopics.get(ind);
            String tableVotes = "topic" + idTopic.toString() + "Votes";
            String tableAnswers = "topic" + idTopic.toString() + "Answers";
            List<Integer> idVotes = new ArrayList<>();
            String sqlVotes = "SELECT " + tableVotes + ".idVote, " +
                    tableVotes + ".nameVote, " + tableVotes + ".descriptionVote, " +
                    "users.nameUser FROM " + tableVotes + " JOIN users ON users.idUser=" +
                    tableVotes + ".idCreator";
            List<Vote> votes = jdbc.query(sqlVotes, (r, i) -> {
               Vote vote = new Vote();
               Topic topic = new Topic();
               topic.setNameTopic(nameTopic);
               vote.setTopic(topic);
               vote.setNameVote(r.getString("nameVote"));
               vote.setDescriptionVote(r.getString("descriptionVote"));
               idVotes.add(r.getInt("idVote"));
               nameCreators.add(r.getString("nameUser"));
               return vote;
            });
            for (int jnd = 0; jnd < votes.size(); jnd++) {
                List<Answer> answers = getAnswersAllByIdTopicVote(idTopic, idVotes.get(jnd));
                votes.get(jnd).setAnswers(answers);
                votes.get(jnd).setCountAnswers(answers.size());
            }
            allVotes.addAll(votes);
        }
        return allVotes;
    }
}
