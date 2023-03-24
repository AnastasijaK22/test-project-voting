package com.example.testproject.models;

import com.example.testproject.repositories.MainRepository;
import com.example.testproject.services.LoggedUserService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class LoginProcessor {
    private final LoggedUserService loggedUserService;
    private final MainRepository mainRepository;

    private String nameUser;

    public LoginProcessor(LoggedUserService loggedUserService,
                          MainRepository mainRepository) {
        this.loggedUserService = loggedUserService;
        this.mainRepository = mainRepository;
    }

    public void login() {
        Integer idUser = mainRepository.getIdUserByName(nameUser);
        if (idUser == null) {
            mainRepository.createUser(nameUser);
            idUser = mainRepository.getIdUserByName(nameUser);
        }
        loggedUserService.setIdUser(idUser);
        loggedUserService.setNameUser(nameUser);
    }

    public String getNameUser() {
        return this.nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }
}
