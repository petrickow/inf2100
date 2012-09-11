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
	firstTime = false;
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
    public static int curLineNum() {
        return (sourceFile == null ? 0 : sourceFile.getLineNumber());
    }
    
    //Les neste char i scourceLine til nextC, om ikke tatt med i readLine(linjeskift/siste tegn), les inn neste om ikke les inn -1...
    static int i = 1;
    static boolean firstTime;
    
    public static void readNext() {

        curC = nextC;

        if (! isMoreToRead()) {
            nextC = (char)-1;
            return;
        }
        
        // tom linje eller linjen er kommentert eller vi har lest til slutten av linjen
        if (sourceLine.length() == 0 && sourceLine != null || curC == '#' || (sourcePos >= sourceLine.length() && sourcePos == 1)) {
            commentLine(true);
        } else if (sourcePos >= sourceLine.length()) {
	    commentLine(false);
	} 
	else {
	    if (firstTime || sourceLine.length() == 1) {
		System.out.println("firstTime: " + sourceLine);
		writeLog();
		firstTime = false;
	    }
            nextC = sourceLine.charAt(sourcePos++);
        }
    }

    /**
     * Send line number to log og les neste
     */
    private static void commentLine(boolean doLog) {
	if (doLog)
	    writeLog();

        try {
            sourceLine = sourceFile.readLine();         //lese ny linje
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (sourceLine == null) {                       //dersom null EOF
            nextC = (char)-1;
        } else {
            sourcePos = 0;
            if (sourceLine.length() > 0) {
		nextC = sourceLine.charAt(sourcePos);
		firstTime = true;
		if (sourceLine.length() == 1) // er kun med for å hindre duplicat av token ved linjer som er 1-tegn lang 
		    sourcePos++;             // finnes bedre måte å forhindre dette på
		return;
            }
            else {
                commentLine(true);          //rekursivt kall dersom neste linje er tom den også
                sourcePos++;            //øke teller ettersom vi allerede har hentet første tegn
            }
        }
    }

    private static void writeLog() {
	Log.noteSourceLine(sourceFile.getLineNumber(), sourceLine); //logge linjen
    }
}

