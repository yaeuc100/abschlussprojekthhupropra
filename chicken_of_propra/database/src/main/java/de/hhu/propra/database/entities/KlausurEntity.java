package de.hhu.propra.database.entities;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;

public record KlausurEntity(@Id Long id, String name, LocalDateTime datum, int dauer, long lsf,
                            boolean online) {

}
