package de.hhu.propra.application.utils;

import de.hhu.propra.application.dto.KlausurDto;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class LsfIdValidierung {

    static String erstelleUrl(long lsf){
        return "https://lsf.hhu.de/qisserver/rds?state=verpublish&status=init&vmfile=no&publishid="
                + lsf + "&moduleCall=webInfo&publishConfFile=webInfo&publishSubDir=veranstaltung";
    }

    private static String getInhalt(String url) throws IOException {
        return new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A").next();
    }

    static String getName(KlausurDto klausurDto) throws IOException {
        String content = getInhalt(erstelleUrl(klausurDto.lsf()));
        String ergebnis = "";
        String[] lines = content.split("\n");
        for(String line : lines){
            if(line.contains(" - Einzelansicht"))
                ergebnis = line.substring(startIndex(line),line.indexOf(" - Einzelansicht"));
        }
        return ergebnis;
    }

    static int startIndex(String line){
        for(int i = 0 ; i< line.length() ;i++){
            if(line.charAt(i) != ' ') {
                return i+1;
            }
        }
        return -1;
    }


    public static boolean namePasstZuId(KlausurDto klausurDto) throws IOException {
        String input = klausurDto.name() + " - Einzelansicht";
        byte[] byteArr = input.getBytes();
        String str = new String(byteArr,StandardCharsets.UTF_8);

        return getInhalt(erstelleUrl(klausurDto.lsf())).contains(str);
    }
}
