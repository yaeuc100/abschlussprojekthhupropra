package de.hhu.propra.end_to_end;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.SilentJavaScriptErrorListener;
import de.hhu.propra.application.dto.KlausurDto;
import de.hhu.propra.application.dto.UrlaubDto;
import de.hhu.propra.application.fehler.KlausurFehler;
import de.hhu.propra.application.fehler.UrlaubFehler;
import de.hhu.propra.application.services.KlausurService;
import de.hhu.propra.application.services.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@WithMockUser(username = "oogabooga", roles = {"STUDENT"})
@AutoConfigureMockMvc
public class EndToEndTests {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  StudentService studentService;

  @Autowired
  KlausurService klausurService;

  WebClient webClient;


  @BeforeEach
  void setupWebClient() {
    webClient = MockMvcWebClientBuilder
        .mockMvcSetup(mockMvc)
        .build();
    webClient.getOptions().setThrowExceptionOnScriptError(false);
    webClient.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());
    webClient.getOptions().setCssEnabled(false);
    webClient.getOptions().setJavaScriptEnabled(false);
  }


  @Test
  @Sql({"classpath:db/migration/wipe_data.sql",
      "classpath:db/migration/V1__init.sql"})
  @DisplayName("Startseite wird korrekt angezeigt")
  void test1() throws IOException {

    UrlaubDto urlaubDto = new UrlaubDto(LocalDate.of(3000, 3, 17).toString(),
        LocalTime.of(10, 15).toString(),
        LocalTime.of(11, 0).toString());
    KlausurDto klausurDto = new KlausurDto("Betriebssysteme und Systemprogrammierung",
        LocalDate.of(2022, 3, 28).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 0).toString(),
        217480,
        true);
    if (studentService.studentMitHandle("oogabooga") == null) {
      studentService.erstelleStudent("oogabooga");
    }
    studentService.klausurErstellen("oogabooga", klausurDto);
    studentService.klausurAnmelden("oogabooga", 1L);
    studentService.urlaubAnlegen("oogabooga", urlaubDto);
    HtmlPage page = webClient.getPage("http://localhost:8080/student");

    HtmlAnchor link = page.getAnchorByHref("student/urlaubanmeldung");
    HtmlAnchor link1 = page.getAnchorByHref("student/klausuranmeldung");
    Page click = link.click();
    Page click1 = link1.click();

    //Elemente aus Urlaub Tabelle
    DomElement urlaubElement = page.getElementById("Urlaubstabelle");
    List<String> urlaubDaten = new ArrayList<>();
    for (DomElement e : urlaubElement.getElementsByTagName("td")) {
      urlaubDaten.add(e.getTextContent());
    }

    //Elemente aus Klausur Tabelle
    DomElement klausurElement = page.getElementById("Klausurtabelle");
    List<String> klausurDaten = new ArrayList<>();
    for (DomElement e : klausurElement.getElementsByTagName("td")) {
      klausurDaten.add(e.getTextContent());
    }

    assertThat(klausurDaten)
        .contains("Betriebssysteme und Systemprogrammierung", "2022-03-28, 10:30 Uhr - 11:00 Uhr",
            "10:00 Uhr - 11:00 Uhr");
    assertThat(urlaubDaten).contains("3000-03-17", "10:15", "11:00");
    assertThat(click.getUrl().toString())
        .isEqualTo("http://localhost:8080/student/urlaubanmeldung");
    assertThat(click1.getUrl().toString())
        .isEqualTo("http://localhost:8080/student/klausuranmeldung");
  }


  @Test
  @Sql({"classpath:db/migration/wipe_data.sql",
      "classpath:db/migration/V1__init.sql"})
  @DisplayName("Student beantragt einen neuen Urlaub")
  void test2() throws IOException {
    UrlaubDto urlaubDto = new UrlaubDto(LocalDate.of(3001, 3, 17).toString(),
        LocalTime.of(10, 15).toString(),
        LocalTime.of(11, 0).toString());
    if (studentService.studentMitHandle("oogabooga") == null) {
      studentService.erstelleStudent("oogabooga");
    }
    HtmlPage page = webClient.getPage("http://localhost:8080/student");
    HtmlAnchor link = page.getAnchorByHref("student/urlaubanmeldung");
    HtmlPage urlaubAnmeldung = link.click();

    //set date
    urlaubAnmeldung.getElementById("tag").setAttribute("value", urlaubDto.datum());
    //set start
    urlaubAnmeldung.getElementById("von").setAttribute("value", urlaubDto.startzeit());
    //set end
    urlaubAnmeldung.getElementById("bis").setAttribute("value", urlaubDto.endzeit());
    //post
    HtmlPage redirectErgebnis = urlaubAnmeldung.getFormByName("urlaub_anmelden")
        .getButtonByName("urlaubsanmeldung_abschicken").click();

    //Elemente aus Urlaub Tabelle
    DomElement urlaubElement = redirectErgebnis.getElementById("Urlaubstabelle");
    List<String> urlaubDaten = new ArrayList<>();
    for (DomElement e : urlaubElement.getElementsByTagName("td")) {
      urlaubDaten.add(e.getTextContent());
    }

    assertThat(redirectErgebnis.getUrl().toString()).isEqualTo("http://localhost:8080/student");
    assertThat(urlaubDaten).contains("3001-03-17", "10:15", "11:00");
  }

  @Test
  @Sql({"classpath:db/migration/wipe_data.sql",
      "classpath:db/migration/V1__init.sql"})
  @DisplayName("Student beantragt eine falsche Urlaub")
  void test3() throws IOException {
    UrlaubDto urlaubDto = new UrlaubDto(LocalDate.of(3002, 3, 17).toString(),
        LocalTime.of(10, 15).toString(),
        LocalTime.of(10, 0).toString());
    if (studentService.studentMitHandle("oogabooga") == null) {
      studentService.erstelleStudent("oogabooga");
    }
    HtmlPage page = webClient.getPage("http://localhost:8080/student");
    HtmlAnchor link = page.getAnchorByHref("student/urlaubanmeldung");
    HtmlPage urlaubAnmeldung = link.click();

    //set date
    urlaubAnmeldung.getElementById("tag").setAttribute("value", urlaubDto.datum());
    //set start
    urlaubAnmeldung.getElementById("von").setAttribute("value", urlaubDto.startzeit());
    //set end
    urlaubAnmeldung.getElementById("bis").setAttribute("value", urlaubDto.endzeit());
    //post
    HtmlPage redirectErgebnis = urlaubAnmeldung.getFormByName("urlaub_anmelden")
        .getButtonByName("urlaubsanmeldung_abschicken").click();

    //get fehler
    String redirectFehler = redirectErgebnis.getElementsByTagName("td").get(0).getTextContent();
    String fehler = UrlaubFehler.STARTZEIT_VOR_ENDZEIT;
    assertThat(redirectErgebnis.getUrl().toString())
        .isEqualTo("http://localhost:8080/student/urlaubanmeldung");
    assertThat(redirectFehler).isEqualTo(fehler);
  }

  @Test
  @Sql({"classpath:db/migration/wipe_data.sql",
      "classpath:db/migration/V1__init.sql"})
  @DisplayName("Student erstellt ein Klausur")
  void test4() throws IOException {

    KlausurDto klausurDto = new KlausurDto("Betriebssysteme und Systemprogrammierung",
        LocalDate.of(2022, 3, 28).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 0).toString(),
        217480,
        true);
    if (studentService.studentMitHandle("oogabooga") == null) {
      studentService.erstelleStudent("oogabooga");
    }

    HtmlPage page = webClient.getPage("http://localhost:8080/student");

    HtmlAnchor link = page.getAnchorByHref("student/klausuranmeldung");
    HtmlPage click1 = link.click();

    HtmlAnchor erstelungsLink = click1.getAnchorByHref("/student/klausurErstellen");
    HtmlPage erstellungsPage = erstelungsLink.click();

    //set veranstaltung
    erstellungsPage.getElementById("veranstaltung").setAttribute("value", klausurDto.name());
    //set lsfId
    erstellungsPage.getElementById("lsfId").setAttribute("value", String.valueOf(klausurDto.lsf()));
    //set online
    erstellungsPage.getElementById("online")
        .setAttribute("value", String.valueOf(klausurDto.online()));
    //set datum
    erstellungsPage.getElementById("tag").setAttribute("value", klausurDto.datum());
    //set start
    erstellungsPage.getElementById("von").setAttribute("value", klausurDto.startzeit());
    //set end
    erstellungsPage.getElementById("bis").setAttribute("value", klausurDto.endzeit());
    //post
    HtmlPage redirectErgebnis = erstellungsPage.getFormByName("klausurform")
        .getButtonByName("submitbtn").click();

    //Klausurenliste
    List<DomElement> list = redirectErgebnis.getElementsByTagName("option");
    List<String> klausuren = new ArrayList<>();

    for (DomElement element : list) {
      klausuren.add(element.getTextContent());
    }

    assertThat(redirectErgebnis.getUrl().toString())
        .isEqualTo("http://localhost:8080/student/klausuranmeldung");
    assertThat(klausuren).contains(
        "Betriebssysteme und Systemprogrammierung ( Datum 2022-03-28, 10:30 Uhr - 11:00 Uhr )");
  }

  @Test
  @Sql({"classpath:db/migration/wipe_data.sql",
      "classpath:db/migration/V1__init.sql"})
  @DisplayName("Student kann nicht ein falsches Klausur erstellen")
  void test5() throws IOException {

    KlausurDto klausurDto = new KlausurDto("Betriebssysteme und Systemprogrammierung",
        LocalDate.of(2022, 3, 28).toString(),
        LocalTime.of(11, 30).toString(),
        LocalTime.of(11, 0).toString(),
        217480,
        true);
    if (studentService.studentMitHandle("oogabooga") == null) {
      studentService.erstelleStudent("oogabooga");
    }

    HtmlPage page = webClient.getPage("http://localhost:8080/student");

    HtmlAnchor link = page.getAnchorByHref("student/klausuranmeldung");
    HtmlPage click1 = link.click();

    HtmlAnchor erstelungsLink = click1.getAnchorByHref("/student/klausurErstellen");
    HtmlPage erstellungsPage = erstelungsLink.click();

    //set veranstaltung
    erstellungsPage.getElementById("veranstaltung").setAttribute("value", klausurDto.name());
    //set lsfId
    erstellungsPage.getElementById("lsfId").setAttribute("value", String.valueOf(klausurDto.lsf()));
    //set online
    erstellungsPage.getElementById("online")
        .setAttribute("value", String.valueOf(klausurDto.online()));
    //set datum
    erstellungsPage.getElementById("tag").setAttribute("value", klausurDto.datum());
    //set start
    erstellungsPage.getElementById("von").setAttribute("value", klausurDto.startzeit());
    //set end
    erstellungsPage.getElementById("bis").setAttribute("value", klausurDto.endzeit());
    //post
    HtmlPage redirectErgebnis = erstellungsPage.getFormByName("klausurform")
        .getButtonByName("submitbtn").click();

    String pageFehler = redirectErgebnis.getElementsByTagName("td").get(0).getTextContent();

    assertThat(redirectErgebnis.getUrl().toString())
        .isEqualTo("http://localhost:8080/student/klausurErstellen");
    assertThat(pageFehler).isEqualTo(KlausurFehler.STARTZEIT_VOR_ENDZEIT);
  }


  @Test
  @Sql({"classpath:db/migration/wipe_data.sql",
      "classpath:db/migration/V1__init.sql"})
  @DisplayName("student hat sich bei einem Klausur angemeldet")
  void test6() throws IOException {
    KlausurDto klausurDto = new KlausurDto("Betriebssysteme und Systemprogrammierung",
        LocalDate.of(2022, 3, 28).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 0).toString(),
        217480,
        true);
    if (studentService.studentMitHandle("oogabooga") == null) {
      studentService.erstelleStudent("oogabooga");
    }
    studentService.klausurErstellen("oogabooga", klausurDto);

    HtmlPage page = webClient.getPage("http://localhost:8080/student/klausuranmeldung");

    //erste klausur auswählen und post
    HtmlPage redirection = page.getFormByName("klausur_anmelden")
        .getButtonByName("klausuranmeldung_abschicken").click();

    //Elemente aus Klausur Tabelle
    DomElement klausurElement = redirection.getElementById("Klausurtabelle");
    List<String> klausurDaten = new ArrayList<>();
    for (DomElement e : klausurElement.getElementsByTagName("td")) {
      klausurDaten.add(e.getTextContent());
    }

    assertThat(redirection.getUrl().toString()).isEqualTo("http://localhost:8080/student");
    assertThat(klausurDaten)
        .contains("Betriebssysteme und Systemprogrammierung", "2022-03-28, 10:30 Uhr - 11:00 Uhr",
            "10:00 Uhr - 11:00 Uhr");
  }


  @Test
  @Sql({"classpath:db/migration/wipe_data.sql",
      "classpath:db/migration/V1__init.sql"})
  @DisplayName("Klausur wird storniert")
  void test7() throws IOException {

    KlausurDto klausurDto = new KlausurDto("Betriebssysteme und Systemprogrammierung",
        LocalDate.of(2022, 3, 28).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 0).toString(),
        217480,
        true);
    KlausurDto klausurDto2 = new KlausurDto("Betriebssysteme und Systemprogrammierung",
        LocalDate.of(2022, 3, 29).toString(),
        LocalTime.of(11, 30).toString(),
        LocalTime.of(12, 0).toString(),
        217480,
        true);
    if (studentService.studentMitHandle("oogabooga") == null) {
      studentService.erstelleStudent("oogabooga");
    }
    studentService.klausurErstellen("oogabooga", klausurDto);
    studentService.klausurErstellen("oogabooga", klausurDto2);
    studentService.klausurAnmelden("oogabooga", 1L);
    studentService.klausurAnmelden("oogabooga", 2L);
    HtmlPage page = webClient.getPage("http://localhost:8080/student");

    //post
    HtmlPage ergebnis = page
        .getElementById("Klausurtabelle")
        .getElementsByTagName("form").get(0)
        .getElementsByTagName("button").get(0).click();

    //Elemente aus Klausur Tabelle
    DomElement klausurElement = page.getElementById("Klausurtabelle");
    List<String> klausurDaten = new ArrayList<>();
    for (DomElement e : klausurElement.getElementsByTagName("td")) {
      klausurDaten.add(e.getTextContent());
    }

    assertThat(klausurDaten)
        .contains("Betriebssysteme und Systemprogrammierung", "2022-03-28, 10:30 Uhr - 11:00 Uhr",
            "10:00 Uhr - 11:00 Uhr");
    assertThat(klausurDaten)
        .doesNotContain("2022-03-29, 12:30 Uhr - 13:00 Uhr", "12:00 Uhr - 13:00 Uhr");
  }

  @Test
  @Sql({"classpath:db/migration/wipe_data.sql",
      "classpath:db/migration/V1__init.sql"})
  @DisplayName("Urlaub wird storniert")
  void test8() throws IOException {

    UrlaubDto urlaubDto = new UrlaubDto(LocalDate.of(3000, 3, 17).toString(),
        LocalTime.of(10, 15).toString(),
        LocalTime.of(11, 0).toString());
    UrlaubDto urlaubDto1 = new UrlaubDto(LocalDate.of(3000, 3, 18).toString(),
        LocalTime.of(10, 0).toString(),
        LocalTime.of(11, 30).toString());

    if (studentService.studentMitHandle("oogabooga") == null) {
      studentService.erstelleStudent("oogabooga");
    }
    studentService.urlaubAnlegen("oogabooga", urlaubDto);
    studentService.urlaubAnlegen("oogabooga", urlaubDto1);

    HtmlPage page = webClient.getPage("http://localhost:8080/student");

    HtmlPage ergebnis = page
        .getElementById("Urlaubstabelle")
        .getElementsByTagName("form").get(0)
        .getElementsByTagName("button").get(0).click();

    //Elemente aus Urlaub Tabelle
    DomElement urlaubElement = page.getElementById("Urlaubstabelle");
    List<String> urlaubDaten = new ArrayList<>();
    for (DomElement e : urlaubElement.getElementsByTagName("td")) {
      urlaubDaten.add(e.getTextContent());
    }

    assertThat(urlaubDaten).doesNotContain("3000-03-17", "10:15", "11:00");
    assertThat(urlaubDaten).contains("3000-03-18", "10:00", "11:30");
  }


}
