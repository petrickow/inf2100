package no.uio.ifi.cflat.log;

/*
 * module Log
 */

import java.io.*;
import no.uio.ifi.cflat.cflat.Cflat;
import no.uio.ifi.cflat.error.Error;
import no.uio.ifi.cflat.scanner.Scanner;
import static no.uio.ifi.cflat.scanner.Token.*;

/*
 * Produce logging information.
 */
public class Log {
    public static boolean doLogBinding = false, doLogParser = false, 
           doLogScanner = false, doLogTree = false;

    private static String logName, curTreeLine = "";
    private static int nLogLines = 0, parseLevel = 0, treeLevel = 0;

    private static int indent = 3;

    public static void init() {
        logName = Cflat.sourceBaseName + ".log";
    }

    public static void finish() {
        //-- Must be changed in part 0:
    }

    private static void writeLogLine(String data) {
        try {
            PrintWriter log = (nLogLines==0 ? new PrintWriter(logName) :
                    new PrintWriter(new FileOutputStream(logName,true)));
            log.println(data);  ++nLogLines;
            log.close();
        } catch (FileNotFoundException e) {
            Error.error("Cannot open log file " + logName + "!");
        }
    }

    /*
     * Make a note in the log file that an error has occured.
     *
     * @param message  The error message
     */
    public static void noteError(String message) {
        if (nLogLines > 0) 
            writeLogLine(message);
    }


    public static void enterParser(String symbol) {
        if (! doLogParser) return;

        //-- Must be changed in part 1:
        // m legge inn meg innrykk p en penere mte (programmeringsmessig)
        String spaceN = new String(new char[indent]).replace('\0', ' ');
        writeLogLine("Parser:" + spaceN + symbol);
        increaseIndent();
    }

    public static void leaveParser(String symbol) {
        if (! doLogParser) return;

        //-- Must be changed in part 1:
        // m legge inn meg innrykk p en penere mte (programmeringsmessig)
        decreaseIndent();
        String spaceN = new String(new char[indent]).replace('\0', ' ');
        writeLogLine("Parser:" + spaceN + symbol);
    }

    /**
     * Make a note in the log file that another source line has been read.
     * This note is only made if the user has requested it.
     *
     * @param lineNum  The line number
     * @param line     The actual line
     */
    public static void noteSourceLine(int lineNum, String line) {
        if (! doLogParser && ! doLogScanner) return;
        if (lineNum > 0)
            writeLogLine("   " + lineNum + ": " + line);
        //-1 Must be changed in part 0:
    }

    /**
     * Make a note in the log file that another token has been read 
     * by the Scanner module into Scanner.nextNextToken.
     * This note will only be made if the user has requested it.
     */
    public static void noteToken() {
        if (! doLogScanner) return;
        //-- Must be changed in part 0:
        if (Scanner.nextNextToken == nameToken) { // hvis nameToken m vi ogs sende med navnet p nameToken 
            writeLogLine("Scanner:  " + Scanner.nextNextToken + " " + Scanner.nextNextName);
        } else if (Scanner.nextNextToken == numberToken) { // hvis numberToken m vi ogs sende med navnet p tallet (lagt i nextNextName) 
            writeLogLine("Scanner:  " + Scanner.nextNextToken + " " + Scanner.nextNextName);
        } else {
            writeLogLine("Scanner:  " + Scanner.nextNextToken);
        }

    }

    public static void noteBinding(String name, int lineNum, int useLineNum) {
        if (! doLogBinding) return;
        //-- Must be changed in part 2:
	// Example ---> Binding: Line 9: x refers to declaration in line 8
	writeLogLine("Binding: Line " + lineNum + ": " + name + " refers to declaration in line " + useLineNum);  
    }

    public static void noteBindingLib(String name, int lineNum) {
        if (! doLogBinding) return;
        //-- Must be changed in part 2:
	writeLogLine("Binding: Line " + lineNum + ": " + name + " refers to declaration in the library");  
    }

    public static void noteBindingMain(int lineNum) {
        if (! doLogBinding) return;
        //-- Must be changed in part 2:
	writeLogLine("Binding: main refers to declaration in line " + lineNum);  
    }

    
    


    public static void wTree(String s) {
        if (curTreeLine.length() == 0) {
            for (int i = 1;  i <= treeLevel;  ++i) curTreeLine += "  ";
        }
        curTreeLine += s;
    }

    public static void wTreeLn() {
        writeLogLine("Tree:     " + curTreeLine);
        curTreeLine = "";
    }

    public static void wTreeLn(String s) {
        wTree(s);  wTreeLn();
    }

    public static void indentTree() {
        treeLevel++;
        //-- Must be changed in part 1:
    }

    public static void outdentTree() {
        treeLevel--;
        //-- Must be changed in part 1:
    }


    private static void increaseIndent() {
        indent += 3;
    }

    private static void decreaseIndent() {
        indent -= 3;
    }


}
