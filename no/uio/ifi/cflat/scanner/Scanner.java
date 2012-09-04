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
        while (nextNextToken == null) {
            nextNextLine = CharGenerator.curLineNum();

            if (! CharGenerator.isMoreToRead()) {
                nextNextToken = eofToken;
            } else 
                nextNextToken = CharGenerator.curC;
                //-- Must be changed in part 0:
                //-- Skal bli p� rundt 400-500 linjer, vi har mer � lese
            {
                Error.error(nextNextLine,
                        "Illegal symbol: '" + CharGenerator.curC + "'!");
            }
        }
        Log.noteToken();
    }

    private static boolean isLetterAZ(char c) {
        // -2 Must be changed in part 0:
	int iv = int(c);  // iv = isoValue
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
