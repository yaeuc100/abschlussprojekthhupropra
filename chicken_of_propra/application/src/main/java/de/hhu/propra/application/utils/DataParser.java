package de.hhu.propra.application.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

public class DataParser {

  private LocalDate start;
  private LocalDate end;
  private LocalTime startZeit;
  private LocalTime endZeit;

  public DataParser(LocalDate start, LocalDate end, LocalTime startZeit, LocalTime endZeit) {
    this.start = start;
    this.end = end;
    this.startZeit = startZeit;
    this.endZeit = endZeit;
  }

  public LocalTime getStartZeit() {
    return startZeit;
  }

  public void setStartZeit(LocalTime startZeit) {
    this.startZeit = startZeit;
  }

  public LocalTime getEndZeit() {
    return endZeit;
  }

  public void setEndZeit(LocalTime endZeit) {
    this.endZeit = endZeit;
  }

  public LocalDate getStart() {
    return start;
  }

  public void setStart(LocalDate start) {
    this.start = start;
  }

  public LocalDate getEnd() {
    return end;
  }

  private static LocalDate uebersetzeDatum(String date) {
    LocalDate local = null;
    date = date.replace("\r", "");
    try {
      local = LocalDate.parse(date);
    } catch (Exception e) {
    }
    return local;
  }

  private static LocalTime uebersetzeZeit(String time) {
    LocalTime local = null;
    time = time.replace("\r", "");
    try {
      local = LocalTime.parse(time);
    } catch (Exception e) {
    }
    return local;
  }

  private static String replaceSlash(String path) {
    return path.substring(0, path.length() - 1).replace(Character.toString(92), "/");
  }

  private static String erstellePfad() {
    String path = new File(".").getAbsolutePath();
    // erstelle Pfad von Main Application
    if (!path.contains("chicken")) {
      path = replaceSlash(path) + "chicken_of_propra/application/src/main/resources/config.txt";
    } else {
      // erstelle von Tests
      path = replaceSlash(path).replace("spring", "application") + "/src/main/resources/config.txt";
    }
    return path;
  }

  public static DataParser leseDatei() {
    try {
      String path = erstellePfad();
      byte[] InhaltArray = Files.readAllBytes(Path.of(path));
      String Inhalt = new String(InhaltArray);
      String[] parameters = Inhalt.split("\n");
      LocalDate start = (uebersetzeDatum(parameters[0].split(" : ")[1]));
      LocalDate end = (uebersetzeDatum(parameters[1].split(" : ")[1]));
      LocalTime startZeit = (uebersetzeZeit(parameters[2].split(" : ")[1]));
      LocalTime endZeit = (uebersetzeZeit(parameters[3].split(" : ")[1]));
      return new DataParser(start, end, startZeit, endZeit);
    } catch (Exception e) {
      return null;
    }
  }
}
