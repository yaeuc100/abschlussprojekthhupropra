package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.KlausurDto;
import org.assertj.core.internal.Bytes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class LsfIdValidierungTests {

  //TODO Test getINhalt, startIndex

  @Test
  @DisplayName("Der Name der Klausur passt nicht zur LSF-ID")
  void test() throws IOException {
    //arrange
    KlausurDto klausurDto = new KlausurDto("aaksfjgakqio",
        LocalDate.of(2022, 3, 17).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 0).toString(),
        222916,
        true);

    //act
    boolean ergebnis = LsfIdValidierung.namePasstZuId(klausurDto);

    //assert
    assertThat(ergebnis).isFalse();
  }

  @Test
  @DisplayName("Der Name der Klausur passt zur LSF-ID")
  void test2() throws IOException {
    //arrange
    KlausurDto klausurDto = new KlausurDto("Einführung in die Computerlinguistik",
        LocalDate.of(2022, 3, 17).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 0).toString(),
        222916,
        true);

    //act
    boolean ergebnis = LsfIdValidierung.namePasstZuId(klausurDto);

    //assert
    assertThat(ergebnis).isTrue();
  }

  @Test
  @DisplayName("Der Name der Veranstaltung wird in der HTML Seite gefunden")
  void test3() throws IOException {
    //arrange
    KlausurDto klausurDto = new KlausurDto("Einführung in die Computerlinguistik",
        LocalDate.of(2022, 3, 17).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 0).toString(),
        222916,
        true);

    //act
    String ergebnis = LsfIdValidierung.getName(klausurDto);

    //assert
    assertThat(ergebnis).isEqualTo(
        new String("Einführung in die Computerlinguistik".getBytes(), StandardCharsets.UTF_8));
  }

  @Test
  @DisplayName("Der richtige Link zu einer gültigen LSF-ID wird zurückgegeben")
  void test4() {
    //arrange
    long lsfID = 22291;

    //act
    String ergebnis = LsfIdValidierung.erstelleUrl(22291);

    //assert
    assertThat(ergebnis)
        .isEqualTo("https://lsf.hhu.de/qisserver/rds?state=verpublish&status=init&vmfile=no&pub" +
            "lishid=22291&moduleCall=webInfo&publishConfFile=webInfo&publishSubDir=veranstaltung");
  }

//    @Test
//    @DisplayName("Der richtige HTML Inhalt wird zu einer URL geladen")
//    void test5() throws IOException {
//        //arrange
//
//        String path = new File(".").getAbsolutePath();
//        path = path.substring(0,path.length()-1).replace(Character.toString(92),"/") + "src/test/java/de/hhu/propra/application/utils/testfile.txt";
//
//        String url = "https://lsf.hhu.de/qisserver/rds?state=verpublish&status=init&vmfile=no&pub" +
//                "lishid=22291&moduleCall=webInfo&publishConfFile=webInfo&publishSubDir=veranstaltung";
//        byte[] richtigerInhaltArray = Files.readAllBytes(Path.of(path));
//        String richtigerInhalt = new String(richtigerInhaltArray);
//        richtigerInhalt = richtigerInhalt.replace(" ","");
//        richtigerInhalt = richtigerInhalt.replace("\n","");
//
//        //act
//        String ergebnis = LsfIdValidierung.getInhalt(url);
//        ergebnis = ergebnis.replace(" ","");
//        ergebnis = ergebnis.replace("\n","");
//        //assert
//        assertThat(ergebnis).contains(richtigerInhalt);
//    }

  @Test
  @DisplayName("Bei einem String wird der erste Buchstabe nach 7 Leerzeichen gefunden")
  void test6() {
    //arrange
    String string = "       Hallo";

    //act
    int ergebnis = LsfIdValidierung.startIndex(string);

    //arrange
    assertThat(ergebnis).isEqualTo(8);
  }

  @Test
  @DisplayName("Bei einem String, der nur aus Leerzeichen besteht wird -1 zurückgegeben")
  void test7() {
    //arrange
    String string = "    ";

    //act
    int ergebnis = LsfIdValidierung.startIndex(string);

    //arrange
    assertThat(ergebnis).isEqualTo(-1);
  }

  @Test
  @DisplayName("Der richtige Name zu einer gültigen LSF-ID wird aus dem LSF geholt (Einführung in die Computerlinguistik)")
  void test8() throws IOException {
    //arrange
    KlausurDto klausur = new KlausurDto("bla",
        LocalDate.of(2022, 3, 17).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 0).toString(),
        222916,
        true);

    //act
    String name = LsfIdValidierung.getName(klausur);

    //arrange
    assertThat(name).isEqualTo("Einführung in die Computerlinguistik");
  }

  @Test
  @DisplayName("Der richtige Name zu einer gültigen LSF-ID wird aus dem LSF geholt (Einführung in die Syntax)")
  void test9() throws IOException {
    //arrange
    KlausurDto klausur = new KlausurDto("bla",
        LocalDate.of(2022, 3, 17).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 0).toString(),
        222871,
        true);

    //act
    String name = LsfIdValidierung.getName(klausur);

    //arrange
    assertThat(name).isEqualTo("Einführung in die Syntax");
  }

  @Test
  @DisplayName("Der richtige Name zu einer gültigen LSF-ID wird aus dem LSF geholt (Experimentelle Onkologie in der HNO-Heilkunde)")
  void test10() throws IOException {
    //arrange
    KlausurDto klausur = new KlausurDto("bla",
        LocalDate.of(2022, 3, 17).toString(),
        LocalTime.of(10, 30).toString(),
        LocalTime.of(11, 0).toString(),
        22218,
        true);

    //act
    String name = LsfIdValidierung.getName(klausur);

    //arrange
    assertThat(name).isEqualTo("Experimentelle Onkologie in der HNO-Heilkunde");
  }


}
