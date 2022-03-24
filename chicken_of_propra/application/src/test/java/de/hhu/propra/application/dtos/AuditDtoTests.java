package de.hhu.propra.application.dtos;

import de.hhu.propra.application.dto.AuditDto;
import de.hhu.propra.domain.auditLog.AuditLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AuditDtoTests {

    @Test
    @DisplayName("Das AuditLog wird richtig zu einem AuditDto übersetzt")
    void test1(){
        //arrange
        AuditLog auditLog = new AuditLog("Urlaub angemeldet", "Fred");

        //act
        AuditDto auditDto = AuditDto.toAuditDto(auditLog);

        //assert
        assertThat(auditDto).isEqualTo(new AuditDto(auditLog.getZeitpunkt(), "Fred", "Urlaub angemeldet"));
    }
}
