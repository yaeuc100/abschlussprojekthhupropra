
INSERT INTO klausur_entity (id, name, datum, dauer, lsf, online)
VALUES ('1','Rechnernetze','2020-01-01 10:00:00.000000','60','123456','true');

INSERT INTO klausur_entity (id, name, datum, dauer, lsf, online)
VALUES ('2','BS','2021-01-01 10:00:00.000000','90','666666','false');

INSERT INTO klausur_entity (id, name, datum, dauer, lsf, online)
VALUES ('3','WA','2022-01-01 10:00:00.000000','120','112233','true');



INSERT INTO student_entity
VALUES ('1','olli',0);

INSERT INTO student_entity
VALUES ('2','david',240);

INSERT INTO student_entity
VALUES ('3','yassine',240);

INSERT INTO student_entity
VALUES ('4','farouk',210);

INSERT INTO student_entity
VALUES ('5','Sabrina',240);


INSERT INTO urlaub
VALUES ('1', '2021-01-01' ,'10:00:00.000000', '10:30:00.000000',4);


INSERT INTO urlaub
VALUES ('2', '2021-01-01' ,'8:30:00.000000', '10:30:00.000000',1);
INSERT INTO urlaub
VALUES ('3', '2021-01-01' ,'10:30:00.000000', '12:30:00.000000',1);

INSERT INTO klausur_referenz
VALUES ('1','5');

INSERT INTO klausur_referenz
VALUES ('2','1');
INSERT INTO klausur_referenz
VALUES ('3','1');




