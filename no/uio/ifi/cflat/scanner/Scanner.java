package no.uio.ifi.cflat.scanner;

/*
 * module Scanner
 */

import no.uio.ifi.cflat.chargenerator.CharGenerator;
import no.uio.ifi.cflat.error.Error;
import no.uio.ifi.cflat.log.Log;
import static no.uio.ifi.cflat.scanner.Token.*;

/*
 * Module for forming characters into tokens.
 */

public class Scanner {
    public static Token curToken, nextToken, nextNextToken;
    public static String curName, nextName, nextNextName;
    public static int curNum, nextNum, nextNextNum;
    public static int curLine, nextLine, nextNextLine;

    public static void init() {
        //-- Must be changed in part 0:
        //--les inn tre tokens og sjekk f�rste token ettersom readNext vil g� til nextToken
        //    l�kke som g�r til vi er ferdig?
        curName = nextName = nextNextName = "";
        //readNext(); readNext();
    }


    public static void finish() {
        //-- Must be changed in part 0:
    }

    public static void readNext() {
        curToken = nextToken;  nextToken = nextNextToken;
        curName = nextName;  nextName = nextNextName;
        curNum = nextNum;  nextNum = nextNextNum;
        curLine = nextLine;  nextLine = nextNextLine;

        nextNextToken = null;
        nextNextName = "";
        while (nextNextToken == null) {
            nextNextLine = CharGenerator.curLineNum(); // Denne skal v�re her i f�lge prekoden
	    	    
            if (! CharGenerator.isMoreToRead()) {
                System.out.println("Last Char");
                nextNextToken = eofToken;
            } else { 
                //-- Must be changed in part 0:
                //-- Skal bli p� rundt 400-500 linjer, vi har mer � lese
                CharGenerator.readNext();
                nextNextName = "";
                
                nextNextName += CharGenerator.curC;
                /*Trenger h�ndtering av whitespaces og andre fylletegn*/

                if (isLetterAZ(CharGenerator.curC)) { //ENTEN int, double eller nameToken, h�ndter det
                    while (!(isReserved(CharGenerator.nextC)) || CharGenerator.nextC == ' ') {
                        CharGenerator.readNext();
                        nextNextName += CharGenerator.curC;
                    }
                    System.out.println("===============> " + nextNextName);
                    if (nextNextName.compareTo("int") == 0) {
                        System.out.println("-------------->  intToken");
                        nextNextToken = intToken;
                    } else if (nextNextName.compareTo("double") == 0) {
                        System.out.println("--------------> doubleToken");
                        nextNextToken = doubleToken;
                    } else {
                        System.out.println("--------------> nameToken -> " + nextNextName); //OBS denne behandler feks 'x' som en nametoken, det m� nesten v�re en int?
                        nextNextToken = nameToken;
                    }

                } else if (isReserved(CharGenerator.curC)) { //ALLE reserverte enkelt-tegn
                    if (nextNextName.compareTo("(") == 0) {
                        System.out.println("--------------> leftParToken");
                        nextNextToken = leftParToken;
                    } else if (nextNextName.compareTo(")") == 0) {
                        nextNextToken = rightParToken;
                        System.out.println("--------------> rightParToken");
                    } else if(nextNextName.compareTo("{") == 0) {
                        nextNextToken = leftCurlToken;
                        System.out.println("--------------> leftCurlToken");
                    } else if(nextNextName.compareTo("}") == 0) {
                        nextNextToken = rightCurlToken;
                        System.out.println("--------------> rightCurlToken");
                    } else if(nextNextName.compareTo(";") == 0) {
                        nextNextToken = semicolonToken;
                        System.out.println("--------------> semicolonToken");
                    } 
                }

                /*else { //TODO
                    Error.error(nextNextLine,"Illegal symbol: '" + CharGenerator.curC + "'!");
                }*/

                
            }
        }
        Log.noteToken();
    }
    private static boolean isReserved(char c) {
        int ch = (int)c;
        //ASCII values of reserved characters
        if ((ch >= 33 && ch <= 45) || (ch == 47) || (ch >= 91 && ch <=93) || (ch >= 123 && ch <=125)) {  
            System.out.println(c+" is: " + ch);
            return true;
        }
        else
            return false;
    }
    /**
     * Om vi har en /* s� leser vi til vi finner avsluttningen
     */
    private static void skipComment() {
        
        CharGenerator.readNext();

    }

    private static boolean isLetterAZ(char c) {
        // -2 Must be changed in part 0:
        int iv = (int)c;  // iv = isoValue
        return  ((iv >= 65 && iv <= 90) || (iv >= 97 && iv <=122)); 
    }

    public static void check(Token t) {
        if (curToken != t)
            Error.expected("A " + t);
    }

    public static void check(Token t1, Token t2) {
        if (curToken != t1 && curToken != t2)
            Error.expected("A " + t1 + " or a " + t2);
    }

    public static void skip(Token t) {
        check(t);  readNext();
    }

    public static void skip(Token t1, Token t2) {
        check(t1,t2);  readNext();
    }
}
