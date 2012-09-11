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
        //--les inn tre tokens og sjekk første token ettersom readNext vil gå til nextToken
        //    løkke som går til vi er ferdig?
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

	boolean keepReading;

        nextNextToken = null;
        nextNextName = "";
        while (nextNextToken == null) {
            nextNextLine = CharGenerator.curLineNum(); // Denne skal være her i følge prekoden
	    	    
            if (! CharGenerator.isMoreToRead()) {
                System.out.println("FUNNET SISTE TEGN i linje -> " + CharGenerator.sourceLine);
                nextNextToken = eofToken;
            } else { 
                //-- Must be changed in part 0:
                //-- Skal bli på rundt 400-500 linjer, vi har mer å lese
		
		CharGenerator.readNext();
		
		nextNextName = "";
                if (isLetterAZ(CharGenerator.curC)) {
		    nextNextName += CharGenerator.curC;
		    CharGenerator.readNext();
		    
		    keepReading = true;
		    
		    while (keepReading) {
			switch (CharGenerator.curC) {
			    /*
			     * Først kommer ulovlige tegn hvor programmet 
	 		     * skal stoppe med en gang
			     */
			
			case '"':
			    keepReading = false;
			    break;
			case '?':
			    keepReading = false;
			    break;
			case '|':
			    keepReading = false;
			    break;
			case '§':
			    keepReading = false;
			    break;
			case '%':
			    keepReading = false;
			    break;
			case '!':
			    keepReading = false;
			    break;
			case '@':
			    keepReading = false;
			    break;
			case '&':
			    keepReading = false;
			    break;
			case ',':
			    keepReading = false;
			    break;

			    /*
			     * Andre tokens funnet. Programmet skal stoppe med en gang
			     *
			     */

			case '/':
			    keepReading = false;
			    break;
			case '*':
			    keepReading = false;
			    break;
			case '(':
			    keepReading = false;
			    break;
			case ')':
			    keepReading = false;
			    break;
			case '{':
			    keepReading = false;
			    break;
			case '}':
			    keepReading = false;
			    break;

			    /*
			     * Lovlige, men hvor strengen er ferdig
			     *
			     */
			    
			
			case ' ':
			    keepReading = false;
			    break;
			case ';':
			    keepReading = false;
			    break;
			    
			    /*
			     * Hvis ingen av testene ovenfor slår inn,
			     * legg til curC til nextNextName
			     */
			    
			default:
			    nextNextName += CharGenerator.curC;
			    CharGenerator.readNext();
			}
		    }
		    /*
		    while (isLetterAZ(CharGenerator.nextC)) {
			CharGenerator.readNext();
			nextNextName += CharGenerator.curC;
		    }
		    */
		    if (nextNextName.compareTo("int") == 0) {
                        //System.out.println("-------------->  intToken");
                        nextNextToken = intToken;
                    } else if (nextNextName.compareTo("double") == 0) {
                        //System.out.println("--------------> doubleToken");
                        nextNextToken = doubleToken;
                    } else if (nextNextName.compareTo("while") == 0) {
			nextNextToken = whileToken;
                    } else if (nextNextName.compareTo("for") == 0) {
			nextNextToken = forToken;
                    } else if (nextNextName.compareTo("if") == 0) {
			nextNextToken = ifToken;
                    } else if (nextNextName.compareTo("else") == 0) {
			nextNextToken = elseToken;
                    } else  {
			System.out.println("--------------> nameToken -> " + nextNextName);
                        nextNextToken = nameToken;
                    } 

                } else {
                    nextNextName += CharGenerator.curC;
                    

		    /*
		     * Først en liste over ulovlige tegn
		     * Avbryt med en gang
		     *
		     */
		    
		    if ((nextNextName.compareTo("@") == 0)) {
			// TODO - husk alle andre ulovlige tegn
		    }

		    /*
		     * relOperators 
		     * !=, ==, <, <=, >, >=
		     *
		     */

		    else if ((nextNextName.compareTo("!") == 0)) {
			if (CharGenerator.nextC == '=') {
			    nextNextToken = notEqualToken;
			} else {
			    // TODO Ulovlig med noe annet enn '=' etter '!'
			}
		    } else if ((nextNextName.compareTo("=") == 0)) {
			if (CharGenerator.nextC == '=') {
			    nextNextToken = equalToken;
			} else {
			    // TODO Ulovlig med noe annet enn '=' etter '='
			}
		    } else if ((nextNextName.compareTo("<") == 0)) {
			if (CharGenerator.nextC == '=') {
			    nextNextToken = lessEqualToken;
			} else {
			    nextNextToken = lessToken;
			}
		    } else if ((nextNextName.compareTo(">") == 0)) {
			if (CharGenerator.nextC == '=') {
			    nextNextToken = greaterEqualToken;
			} else {
			    nextNextToken = greaterToken;
			}
		    }
		    
		    

		    else if (nextNextName.compareTo("(") == 0) {
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
