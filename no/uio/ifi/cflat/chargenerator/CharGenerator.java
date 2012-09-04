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
    private static String sourceLine;
    private static int sourcePos;

    public static void init() {
        try {
            sourceFile = new LineNumberReader(new FileReader(Cflat.sourceName));
        } catch (FileNotFoundException e) {
            Error.error("Cannot read " + Cflat.sourceName + "!");
        }
        sourceLine = "";  sourcePos = 0;  curC = nextC = ' ';
        readNext();  readNext();
        //DEBUG PRINT:
        System.out.println("DEBUG: curent C: " + curC + " next C: " + nextC + " " + sourcePos);
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
//TODO maa ved linjeskift lese inn hele linjen til scourceLine og sette sourcePos = 0
    public static boolean isMoreToRead() {
        //-1 Must be changed in part 0:
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
    public static void readNext() {
        curC = nextC;
        
        if (! isMoreToRead())
            return;
          
        if (curC == '#') {
            commentLine();
            return;
        }
        
        else if (sourcePos >= sourceLine.length()) {
            try {
                sourceLine = sourceFile.readLine();
            }
            catch (IOException e) {
                //TODO
            }
            sourcePos = 0;
            
            while (sourcePos >= sourceLine.length()) {
                Log.noteSourceLine(sourceFile.getLineNumber(), sourceLine);
                try {
                    sourceLine = sourceFile.readLine();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            nextC = sourceLine.charAt(sourcePos);
        }
        
        else {
            sourcePos++;
            nextC = sourceLine.charAt(sourcePos);
        }
    }

    private static void commentLine() {
        Log.noteSourceLine(sourceFile.getLineNumber(), sourceLine);
        try {
            sourceLine = sourceFile.readLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (sourceLine == null)
            nextC = (char)-1;
        else {
            sourcePos = 0;
            nextC = sourceLine.charAt(sourcePos);
            System.out.println("Next line first char: " + curC);
        }
    }
}
