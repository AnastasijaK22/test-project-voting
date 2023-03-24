package com.example.testproject.services;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

@Service
@SessionScope
public class LoggedUserService {
    private String nameUser;
    private Integer idUser;

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getNameUser() {
        return this.nameUser;
    }

    public Integer getIdUser() {
        return this.idUser;
    }
}
