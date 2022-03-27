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
    id int auto_increment primary key ,
    datum DATE,
    startzeit TIME,
    endzeit TIME,
    student_entity int
);

CREATE TABLE IF NOT EXISTS klausur_referenz
(
    id int,
    student_entity int,
    PRIMARY KEY (id, student_entity)
);

CREATE TABLE IF NOT EXISTS audit_entity
(
    id int auto_increment primary key ,
    aenderung VARCHAR(200),
    handle VARCHAR(50),
    zeitpunkt DATETIME
)



