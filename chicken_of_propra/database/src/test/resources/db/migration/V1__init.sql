CREATE TABLE IF NOT EXISTS klausur_entity
(
    id     int auto_increment primary key,
    name   VARCHAR(400),
    datum  DATETIME,
    dauer  int,
    lsf    int,
    online boolean
);

CREATE TABLE IF NOT EXISTS student_entity
(
    id         int auto_increment primary key,
    handle     VARCHAR(400),
    resturlaub int
);

CREATE TABLE IF NOT EXISTS urlaub
(
    id int auto_increment,
    datum DATE,
    startzeit TIME,
    endzeit TIME,
    student_entity int,
    PRIMARY KEY (student_entity, id)
);

CREATE TABLE IF NOT EXISTS klausur_referenz
(
    id int,
    student_entity int,
    PRIMARY KEY (id, student_entity)
);


