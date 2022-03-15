package repository_implementation;

import de.hhu.propra.application.repositories.StudentRepository;
import de.hhu.propra.domain.aggregates.student.Student;

import java.util.List;

public class StudentRepositoryImpl implements StudentRepository {
    @Override
    public Student studentMitId(Long id) {
        return null;
    }

    @Override
    public List<Student> alleStudenten() {
        return null;
    }

    @Override
    public List<Student> alleStudentenInGruppe(Long gruppeId) {
        return null;
    }

    @Override
    public void storniereUrlaub(Long studentId, Long urlaubId) {

    }

    @Override
    public void save(Student student) {

    }
}
