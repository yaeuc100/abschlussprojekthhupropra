package entities;

import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.domain.aggregates.student.Anwesenheit;
import de.hhu.propra.domain.aggregates.student.KlausurReferenz;
import de.hhu.propra.domain.aggregates.student.Urlaub;
import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;

public class StudentEntity {
    @Id
    private Long id;
    private String handle;
    private int resturlaub;
    private Set<Urlaub> urlaube = new HashSet<>();
    private Set<KlausurReferenz> klausuren = new HashSet<>();

    public StudentEntity(Long id, String handle) {
        this.id = id;
        this.handle = handle;
    }

    public Long getId() {
        return id;
    }

    public String getHandle() {
        return handle;
    }

    public int getResturlaub() {
        return resturlaub;
    }

    public Set<Urlaub> getUrlaube() {
        return urlaube;
    }

    public Set<KlausurReferenz> getKlausuren() {
        return klausuren;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setResturlaub(int resturlaub) {
        this.resturlaub = resturlaub;
    }

    public void addUrlaube(Urlaub urlaub) {
        urlaube.add(urlaub);
    }

    public void addKlausur(Klausur klausur) {
        klausuren.add(new KlausurReferenz(klausur.id()));
    }

}
