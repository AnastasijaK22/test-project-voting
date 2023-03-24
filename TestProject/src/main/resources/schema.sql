CREATE TABLE IF NOT EXISTS users (
    idUser INT AUTO_INCREMENT,
    nameUser VARCHAR(30) UNIQUE NOT NULL,
    PRIMARY KEY(idUser)
);

CREATE TABLE IF NOT EXISTS topics (
    idTopic INT AUTO_INCREMENT,
    nameTopic VARCHAR(30) UNIQUE NOT NULL,
    PRIMARY KEY(idTopic)
);



