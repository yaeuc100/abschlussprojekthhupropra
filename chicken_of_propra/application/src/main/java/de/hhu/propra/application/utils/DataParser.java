package de.hhu.propra.application.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

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

  public void setEnd(LocalDate end) {
    this.end = end;
  }

  private static LocalDate parseToDate(String date) {
    LocalDate local = null;
    date = date.replace("\r", "");
    try {
      local = LocalDate.parse(date);
    } catch (Exception e) {
    }
    return local;
  }

  private static LocalTime parseToTime(String time){
    LocalTime local = null;
    time = time.replace("\r","");
    try{
      local = LocalTime.parse(time);
    }catch (Exception e){}
    return local;
  }
  private static String replaceSlash(String path) {
    return path.substring(0, path.length() - 1).replace(Character.toString(92), "/");
  }

  private static String buildPath() {
    String path = new File(".").getAbsolutePath();
    // build from main app
    if (!path.contains("chicken")) {
      path = replaceSlash(path) + "chicken_of_propra/application/src/main/resources/config.txt";
    } else {
      // build from tests
      path = replaceSlash(path).replace("spring", "application") + "/src/main/resources/config.txt";
    }
    return path;
  }

  public static DataParser readFile() {
    try {
      String path = buildPath();
      byte[] InhaltArray = Files.readAllBytes(Path.of(path));
      String Inhalt = new String(InhaltArray);
      String[] parameters = Inhalt.split("\n");
      LocalDate start = (parseToDate(parameters[0].split(" : ")[1]));
      LocalDate end = (parseToDate(parameters[1].split(" : ")[1]));
      LocalTime startZeit = (parseToTime(parameters[2].split(" : ")[1]));
      LocalTime endZeit = (parseToTime(parameters[3].split(" : ")[1]));
      return new DataParser(start, end,startZeit,endZeit);
    } catch (Exception e) {
      return null;
    }
  }
}
