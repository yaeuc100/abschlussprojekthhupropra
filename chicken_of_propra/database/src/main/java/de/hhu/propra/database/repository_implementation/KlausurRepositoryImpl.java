package de.hhu.propra.database.repository_implementation;

import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.database.dao.KlausurDao;
import de.hhu.propra.application.repositories.KlausurRepository;
import de.hhu.propra.domain.aggregates.klausur.Klausur;
import de.hhu.propra.database.entities.KlausurEntity;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class KlausurRepositoryImpl implements KlausurRepository {

    private final KlausurDao klausurDao;

    private final JdbcTemplate db;

    public KlausurRepositoryImpl(KlausurDao klausurDao, JdbcTemplate db) {
        this.klausurDao = klausurDao;
        this.db = db;
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
    public Klausur klausurMitDaten(KlausurDto klausurDto) {
        String SQL = """
                SELECT * 
                FROM klausur_entity
                WHERE name = ? AND
                      datum = ? AND
                      dauer = ? AND
                      lsf = ? AND 
                      online = ?;""";
        List <KlausurEntity> entities = db.query(SQL,
                new DataClassRowMapper<>(KlausurEntity.class),
                klausurDto.name(),
                klausurDto.datum(),
                klausurDto.dauer(),
                klausurDto.lsf(),
                klausurDto.online());
        if(entities.isEmpty()){
            return null;
        }
        return build(entities.get(0));
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
        return new Klausur(klausur.id(),
                klausur.name(),
                klausur.datum(),
                klausur.dauer(),
                klausur.lsf(),
                klausur.online());
    }
}
