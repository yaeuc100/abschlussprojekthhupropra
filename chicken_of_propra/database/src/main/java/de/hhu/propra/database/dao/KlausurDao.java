package de.hhu.propra.database.dao;

import de.hhu.propra.database.entities.KlausurEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;


public interface KlausurDao extends CrudRepository<KlausurEntity,Long> {
}
