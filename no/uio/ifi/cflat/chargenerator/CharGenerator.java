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
        readNext();
	readNext();
	//DEBUG PRINT:
        //System.out.println("DEBUG: curent C: " + curC + " next C: " + nextC + " " + sourcePos);
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
    static int i = 1;
    
    public static void readNext() {
	
	curC = nextC;
	
	if (! isMoreToRead())
	    return;
	
	System.out.println( " ==================================== ");
	System.out.println(i++ + ".gang og sourceline: " + sourceLine);
	System.out.println("--(length)--> " + sourceLine.length());
	System.out.println("Sourcepos: " + sourcePos);
	System.out.println("curC:  " + curC);
	System.out.println("nextC: " + nextC + "\n");
	
	if (sourceLine.length() == 0 && sourceLine != null) { // Vi har en tom linje
	    commentLine();
	    return;
	}
		          
        if (curC == '#') {
	    commentLine();
            return;
        }
	
	if (sourcePos < 1) {
	    Log.noteSourceLine(sourceFile.getLineNumber(), sourceLine);
	}
	
        while (sourcePos >= sourceLine.length()) {
	    try {
                sourceLine = sourceFile.readLine();
		if (sourceLine == null) {
		    nextC = (char)-1;
		    return;
		}
		sourcePos = 0;
		return;
	    }
            catch (IOException e) {
                //TODO
            }
            
	}
        
	nextC = sourceLine.charAt(sourcePos++);
	
    }

    private static void commentLine() {
	Log.noteSourceLine(sourceFile.getLineNumber(), sourceLine);
        try {
            sourceLine = sourceFile.readLine();
	}
        catch (IOException e) {
            e.printStackTrace();
        }
        if (sourceLine == null) {
	    nextC = (char)-1;
        } else {
            sourcePos = 0;
	    if (sourceLine.length() > 0) {
		nextC = sourceLine.charAt(sourcePos);
	    }
	}
    }
}
