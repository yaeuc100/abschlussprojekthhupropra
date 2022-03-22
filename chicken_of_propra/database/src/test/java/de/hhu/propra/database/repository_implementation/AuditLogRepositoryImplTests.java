package de.hhu.propra.database.repository_implementation;

import de.hhu.propra.database.dao.AuditDao;
import de.hhu.propra.domain.auditLog.AuditLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Profile;
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
    void test1(){
        AuditLogRepositoryImpl auditLogRepository = new AuditLogRepositoryImpl(auditDao);
        List<AuditLog> logs = auditLogRepository.nachrichten();
        AuditLog expected = new AuditLog("xyz","olli");
        expected.setZeitpunkt(LocalDateTime.of(2021,1,1,10,0));
        AuditLog expected2 = new AuditLog("xyzxyz","sabrina");
        expected2.setZeitpunkt(LocalDateTime.of(2021,1,1,11,0));

        assertThat(logs).contains(expected,expected2);
    }

    @Test
    @DisplayName("AuditLog wird gespeichert")
    @Sql({"classpath:db/migration/V1__init.sql",
            "classpath:db/migration/loadtest.sql"})
    void test2(){
        AuditLogRepositoryImpl auditLogRepository = new AuditLogRepositoryImpl(auditDao);
        AuditLog audit = new AuditLog("abc","david");
        audit.setZeitpunkt(LocalDateTime.of(2021,1,1,10,0));
        AuditLog expected = new AuditLog("xyz","olli");
        expected.setZeitpunkt(LocalDateTime.of(2021,1,1,10,0));
        AuditLog expected2 = new AuditLog("xyzxyz","sabrina");
        expected2.setZeitpunkt(LocalDateTime.of(2021,1,1,11,0));

        auditLogRepository.save(audit);
        List<AuditLog> logs = auditLogRepository.nachrichten();
        assertThat(logs).contains(expected,expected2,audit);
    }

}
