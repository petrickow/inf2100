package no.uio.ifi.cflat.chargenerator;

/*
 * module CharGenerator
 */

import java.io.*;
import no.uio.ifi.cflat.cflat.Cflat;
import no.uio.ifi.cflat.error.Error;
import no.uio.ifi.cflat.log.Log;

/*
 * Module for reading single characters.
 */
public class CharGenerator {
    public static char curC, nextC;

    private static LineNumberReader sourceFile = null;
    public static String sourceLine;
    private static int sourcePos;

    public static void init() {
        try {
            sourceFile = new LineNumberReader(new FileReader(Cflat.sourceName));
        } catch (FileNotFoundException e) {
            Error.error("Cannot read " + Cflat.sourceName + "!");
        }
        sourceLine = "";  sourcePos = 0;  curC = nextC = ' ';
        readNext(); 
        readNext();
    }

    public static void finish() {
        if (sourceFile != null) {
            try {
                sourceFile.close();
            } catch (IOException e) {
                Error.error("Could not close source file!");
            }
        }
    }
    
    public static boolean isMoreToRead() {
        //-2 Must be changed in part 0:
        return (nextC != (char)-1);
    }

    /**
     * Reads current line number
     * @param
     *
     */

     /*Filosofi: TODO delete comment
        hold funksjonalitet (i dette tilfellet logging) samlet p ett sted/metode.
        Ikke la mange iftester bestemme om det skal logges eller ikke slik det var
        med log eller ikke log i nextLine for det av og til ble logget i readNext, blir
        fort mye forvirring
        som her s logger vi hver gang vi skifter linje ettersom alt skal inn i loggen
        */
    public static int curLineNum() {
        return (sourceFile == null ? 0 : sourceFile.getLineNumber());
    }
    
    //Les neste char i scourceLine til nextC, om ikke tatt med i readLine(linjeskift/siste tegn), les inn neste om ikke les inn -1...
    
    public static void readNext() {

        curC = nextC;

        if (! isMoreToRead()) {
            nextC = (char)-1; //EOF
            return;
        }
        // tom linje eller linjen er kommentert eller vi har lest til slutten av linjen
        if (sourceLine.length() == 0 && sourceLine != null || curC == '#' || (sourcePos >= sourceLine.length())) {
            nextLine();
        } else { //vi er i starten/midt i en linje
            nextC = sourceLine.charAt(sourcePos++);
        }
    }

    /**
     * Send line number to log og les neste
     */
    private static void nextLine() {
        //tatt vekk logLinemetode, da den ikke trengs  gjenbrukes s lenge vi gjr all logging her, slipper fare for dobbel-log
        Log.noteSourceLine(sourceFile.getLineNumber(), sourceLine); //for hvert linjeskift, log foregende linje, da er vi sikker p  f med alle

        try {
            sourceLine = sourceFile.readLine();         //lese ny linje
            sourcePos = 0;                              //flyttet hit da pos alltid skal vre 0
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (sourceLine == null) {                       //dersom null EOF
            nextC = (char)-1;                           //sett nextC til EOF
        } else {
            if (sourceLine.length() > 0) {              //neste linje inneholder noe
                nextC = sourceLine.charAt(sourcePos++); //sett nextC til frste tegn i linjen og inc sourcePos
                return;
            }
            else {                  //dersom linjen er tom men ikke null
                nextLine();         //rekursivt kall dersom neste linje er tom den ogs, sourcePos oppdateres i if-blocken over
            }
        }
    }
}

