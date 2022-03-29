package de.hhu.propra.database.repository_implementation;

import de.hhu.propra.database.dao.AuditDao;
import de.hhu.propra.domain.auditlog.AuditLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@ActiveProfiles("test")
public class AuditLogRepositoryImplTests {

  @Autowired
  AuditDao auditDao;


  @Test
  @DisplayName("Alle Audits werden geholt")
  @Sql({"classpath:db/migration/V1__init.sql",
      "classpath:db/migration/loadtest.sql"})
  void test1() {
    //arrange
    AuditLogRepositoryImpl auditLogRepository = new AuditLogRepositoryImpl(auditDao);
    AuditLog erwartet = new AuditLog("xyz", "olli");
    erwartet.setZeitpunkt(LocalDateTime.of(2021, 1, 1, 10, 0));
    AuditLog erwartet2 = new AuditLog("xyzxyz", "sabrina");
    erwartet2.setZeitpunkt(LocalDateTime.of(2021, 1, 1, 11, 0));

    //act
    List<AuditLog> logs = auditLogRepository.nachrichten();

    //assert
    assertThat(logs).contains(erwartet, erwartet2);
  }

  @Test
  @DisplayName("AuditLogs werden korrekt gespeichert")
  @Sql({"classpath:db/migration/V1__init.sql",
      "classpath:db/migration/loadtest.sql"})
  void test2() {
    //arrange
    AuditLogRepositoryImpl auditLogRepository = new AuditLogRepositoryImpl(auditDao);
    AuditLog auditLog = new AuditLog("abc", "david");
    auditLog.setZeitpunkt(LocalDateTime.of(2021, 1, 1, 10, 0));
    AuditLog erwartet = new AuditLog("xyz", "olli");
    erwartet.setZeitpunkt(LocalDateTime.of(2021, 1, 1, 10, 0));
    AuditLog erwartet2 = new AuditLog("xyzxyz", "sabrina");
    erwartet2.setZeitpunkt(LocalDateTime.of(2021, 1, 1, 11, 0));

    //act
    auditLogRepository.save(auditLog);
    List<AuditLog> logs = auditLogRepository.nachrichten();

    //assert
    assertThat(logs).contains(erwartet, erwartet2, auditLog);
  }

}
