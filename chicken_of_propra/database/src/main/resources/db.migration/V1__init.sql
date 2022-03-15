-- Das Klausur Aggregat
CREATE TABLE klausur_entity
(
    id     int auto_increment primary key,
    name   VARCHAR(400),
    datum  DATETIME,
    dauer  int,
    lsf    int,
    online boolean
);

-- Das Student Aggregat;
CREATE TABLE student_entity
(
    id         int auto_increment primary key,
    handle     VARCHAR(400),
    resturlaub int
);

CREATE TABLE urlaub_value
(
    id int,
    datum DATE,
    startzeit TIME,
    endzeit TIME,
    student_entity int,
    PRIMARY KEY (student_entity, id),
    FOREIGN KEY (student_entity) references student_entity(id)
);

CREATE TABLE klausur_referenz
(
    id int,
    klausur_entity int,
    PRIMARY KEY (klausur_entity, id),
    FOREIGN KEY (klausur_entity) references klausur_entity(id)

);


