package de.hhu.propra.application.fehler;

public class UrlaubFehler {

    public static final String DATUM_FALSCH = "Das Datum ist falsch formatiert";
    public static final String AM_WOCHENENDE = "Der ausgewählte Urlaub liegt am Wochenende";
    public static final String VIELFACHES_VON_15 = "Der genommene Urlaub muss ein Vielfaches von 15 sein";
    public static final String DAUER_IST_VALIDE = "Die Dauer kann nur 240 min betragen oder kleiner/gleich 150 min ";
    public static final String ANTRAG_RECHTZEITIG = "Der Urlaub kann nur bis zu einem Tag vorher beantragt werden";
    public static final String STARTZEIT_VOR_ENDZEIT = "Die Startzeit des Urlaubs muss vor der Endzeit des Urlaubs liegen";
    public static final String URLAUB_IN_ZEITRAUM = "Der beantragte Urlaub muss im Praktikumszeitraum liegen";
    public static final String ZWEI_URLAUBE_AN_TAG = "Zwei genommene Urlaube an einem Tag können nur " +
            "am Anfang und am Ende des Tages liegen, " +
            "außerdem müssen 90 min dazwischen liegen";
    public static final String NICHT_GENUG_URLAUB_VORHANDEN = "Der Resturlaub reicht nicht für diesen Urlaubsantrag aus";
    public static final String MAX_ZWEI_URLAUBE = "Es darf nur max. zwei Urlaube an einem Tag geben";

    public static final String STONIERUNG_RECHTZEITIG = "Der Urlaub kann nur bis zu einem Tag vorher storniert werden";


}
