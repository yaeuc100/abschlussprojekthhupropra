package de.hhu.propra.database.repository_implementation;

import de.hhu.propra.application.repositories.StudentRepository;
import de.hhu.propra.database.dao.StudentDao;
import de.hhu.propra.database.entities.StudentEntity;
import de.hhu.propra.domain.aggregates.student.KlausurReferenz;
import de.hhu.propra.domain.aggregates.student.Student;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentRepositoryImpl implements StudentRepository {

    private final StudentDao studentDao;

    private final JdbcTemplate db;

    public StudentRepositoryImpl(StudentDao studentDao, JdbcTemplate db) {
        this.studentDao = studentDao;
        this.db = db;
    }




    @Override
    public Student studentMitId(Long id) {
        Student student = null;
        if(studentDao.existsById(id)){
            student = buildStudent(studentDao.findById(id).get());
        }
        return student;
    }

    @Override
    public Student studentMitHandle(String handle) {
        String SQL = """
                SELECT * 
                FROM student_entity
                WHERE handle = ?;""";
        List<StudentEntity> entities = db.query(SQL, new DataClassRowMapper<>(StudentEntity.class), handle);
        if(entities.isEmpty()){
            return null;
        }
        return studentMitId(entities.get(0).getId());
    }

    @Override
    public List<Student> alleStudenten() {
        List<Student> studenten = new ArrayList<>();
        for(StudentEntity entity : studentDao.findAll()){
            studenten.add(buildStudent(entity));
        }
        return studenten;
    }

    @Override
    public void save(Student student) {
        StudentEntity entity = new StudentEntity(student.getId(),student.getHandle());
        entity.setResturlaub(student.getResturlaub());
        for (Urlaub urlaub : student.getUrlaube()){
            entity.addUrlaube(urlaub);
        }
        for (Long ref : student.getKlausuren()){
            entity.addKlausurRef(new KlausurReferenz(ref));
        }

        System.out.println("saved rest : " + student.getResturlaub());
        studentDao.save(entity);
    }

    @Override
    public void storniereUrlaub(Long studentId, Urlaub urlaub) {
        Student studentAusDb = buildStudent(studentDao.findById(studentId).get());
        studentAusDb.urlaubStornieren(urlaub.datum(),urlaub.startzeit(),urlaub.endzeit());
        save(studentAusDb);
    }

    private Student buildStudent(StudentEntity entity){
        Student student = new Student(entity.getId(),entity.getHandle());
        student.setResturlaub(entity.getResturlaub());
        //fill Urlaub
        for(Urlaub urlaub : entity.getUrlaube()){
            student.addUrlaub(urlaub.datum(),urlaub.startzeit(),urlaub.endzeit());
        }
        //fill KlausurRef
        for(KlausurReferenz ref : entity.getKlausuren()){
            student.addKlausurRef(ref);
        }
        student.berechneRestUrlaub();
        return student;

    }

}
