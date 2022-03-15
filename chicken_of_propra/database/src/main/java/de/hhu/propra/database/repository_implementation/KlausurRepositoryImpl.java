package de.hhu.propra.database.repository_implementation;

import de.hhu.propra.database.dao.KlausurDao;
import de.hhu.propra.application.repositories.KlausurRepository;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.database.entities.KlausurEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class KlausurRepositoryImpl implements KlausurRepository {

    @Autowired
    private KlausurDao klausurDao;

    public KlausurRepositoryImpl(KlausurDao klausurDao) {
        this.klausurDao = klausurDao;
    }

    @Override
    public Klausur klausurMitId(Long id) {
        Klausur klausur = null;
        if(klausurDao.existsById(id)){
            klausur = build(klausurDao.findById(id).get());
        }
        return klausur;
    }

    @Override
    public List<Klausur> alleKlausuren() {
        List<Klausur> klausuren = new ArrayList<>();
        for(KlausurEntity entity: klausurDao.findAll()){
            klausuren.add(build(entity));
        }
        return klausuren;
    }

    @Override
    public void save(Klausur klausur) {
        klausurDao.save(new KlausurEntity(klausur.id(),
                klausur.name(),
                klausur.datum(),
                klausur.dauer(),
                klausur.lsf(),
                klausur.online()));
    }


    private Klausur build(KlausurEntity klausur){
        return new Klausur(klausur.id()
                ,klausur.name(),klausur.datum(),klausur.dauer(),klausur.lsf(),klausur.online());
    }
}
