package de.hhu.propra.application.dto;

import java.time.LocalDateTime;

public record KlausurDto (String name , LocalDateTime datum , int dauer , long lsf, boolean online){

    @Override
    public String toString() {
        return name +
                " ( " + datum.toLocalDate() +
                " , " + datum.toLocalTime() +
                " , " + datum.toLocalTime().plusMinutes(dauer) +
                " , " + online +
                " )";
    }
}
