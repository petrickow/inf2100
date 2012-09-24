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
        // løkke som går til vi er ferdig?
        curName = nextName = nextNextName = "";
        //readNext(); readNext();
    }


    public static void finish() {
        //-- Must be changed in part 0:
    }

    public static void readNext() {
        curToken = nextToken; nextToken = nextNextToken;
        curName = nextName; nextName = nextNextName;
        curNum = nextNum; nextNum = nextNextNum;
        curLine = nextLine; nextLine = nextNextLine;

        nextNextToken = null;
        nextNextName = "";
        while (nextNextToken == null) {
            nextNextLine = CharGenerator.curLineNum();

            if (! CharGenerator.isMoreToRead()) {
                System.out.println("Last Char");
                nextNextToken = eofToken;
            } else {
                //-- Must be changed in part 0:
                //-- Skal bli på rundt 400-500 linjer, vi har mer å lese


                nextNextName = ""; //vi har en ny nextnext...
                CharGenerator.readNext();
                nextNextName += CharGenerator.curC; //leser første tegn og tester på det

                if (isLetterAZ(CharGenerator.curC)) { //ENTEN int, double eller nameToken, tåler tall i navn
                    int startLine = CharGenerator.curLineNum();
                    while (!(isReserved(CharGenerator.nextC)) && CharGenerator.nextC != ' ' && !(isIllegalInText(CharGenerator.curC))){
                        CharGenerator.readNext();
                        if (CharGenerator.curLineNum() == startLine)
                            nextNextName += CharGenerator.curC;
                        else
                            Error.error(nextNextLine,"Token without valid ending!");
                    }
                    setTextToken(nextNextName);
                    break;
                    // nextNextToken blir satt i metoden hvis true
                }
                else if (isReserved(CharGenerator.curC)) { //ALLE reserverte enkelt-tegn
                    if (isRelOperator()) {
                        // nextNextToken blir satt i metoden hvis true
                        break;
                    }
                    // her har jeg laget en metode som skal erstatte koden under. neste 20 linjene
                    // som heter isAnotherToken... finnes det ikke en fellesnevner for de tokens?

                    else if (isAnotherToken()) {
                        break;
                        // nextNextToken blir satt i metoden hvis true
                    }
                }
                else if (isNumber()) {
                    break;
                    // nextNextToken blir satt til numberToken hvis true
                } 
                else if (CharGenerator.curC == ' ' || (int)CharGenerator.curC == 9) { //hopp over whitespace og tab
                    //TODO midlertidig løsning
                }
                else {
                    Error.error(nextNextLine,"Illegal symbol: '" + CharGenerator.curC + "'!");
                }
            }
        }
        Log.noteToken();
    }
    /**
     * Check if a character is one of c flats reserved characters
     * @param   char c the char from source code
     * @return  true if reserved
     */
    private static boolean isReserved(char c) {
        int ch = (int)c;
        //ASCII values of reserved characters
        if ((ch >= 33 && ch <= 45) || (ch == 47) || (ch >= 58 && ch <= 62) || (ch >= 91 && ch <=93) || ch == 96 || (ch >= 123 && ch <=125)) {
            return true;
        }
        else
            return false;
    }
    /**
     * Makes sure all the chars in a text string i valid characters
     * @param char c    a character in a string
     * @return          returns true if character is illegal
     */
    private static boolean isIllegalInText(char c) {
        int ch = (int)c;
        // 
        return ((ch >= 33 && ch <= 39) ||
                (ch == 46) ||
                (ch == 58) ||
                (ch >= 63 && ch <= 64) ||
                (ch == 92) ||
                (ch >= 94 && ch <= 96) ||
                (ch == 124) ||
                (ch == 126)); 
    }
    /**
     * Checks scanners nextNextName to see if it is one of the following tokens...
     * (, ), {, }, ;, -, +, ,, =, ', / and in the last case checks if it is a comment. If so
     * the method will scan for the ending of comment and log the commented lines. Will
     * send error if end does not exist. It will also read the value of x if incased in 'x'.
     * Error will be given if the value is invalid.
     */
    private static boolean isAnotherToken() {
        
        if (nextNextName.equals("(")) {
            System.out.println("--------------> leftParToken");
            nextNextToken = leftParToken;
        } else if (nextNextName.equals(")")) {
            nextNextToken = rightParToken;
            System.out.println("--------------> rightParToken");
        } else if(nextNextName.equals("{")) {
            nextNextToken = leftCurlToken;
            System.out.println("--------------> leftCurlToken");
        } else if(nextNextName.equals("}")) {
            nextNextToken = rightCurlToken;
            System.out.println("--------------> rightCurlToken");
        } else if(nextNextName.equals(";")) {
            nextNextToken = semicolonToken;
            System.out.println("--------------> semicolonToken");
        } else if(nextNextName.equals("-")) {
            nextNextToken = subtractToken;
            System.out.println("--------------> subtractToken");
        } else if(nextNextName.equals("+")) {
            nextNextToken = addToken;
            System.out.println("--------------> addToken");
        } else if(nextNextName.equals(",")) {
            nextNextToken = commaToken;
            System.out.println("--------------> commaToken");
        } else if(nextNextName.equals("=")) {
            nextNextToken = assignToken;
            System.out.println("--------------> assignToken " + nextNextName);
        } else if(nextNextName.equals("/")) {
            if (CharGenerator.nextC == '*')
                skipComment();
            else {
                nextNextToken = divideToken;
            }
            //Denne bryter litt med overordnet design... TODO
            //Skal compilatoren håndtere \n \t osv som verdier?
        } else if(nextNextName.equals("'")) {
            // håndtere verdi inne i ' ' som verdi...
            // Kanskje legge dette inne i en egen metode?
            System.out.print("--------------> fnutt --> ");
            
            CharGenerator.readNext();
            int ch = (int) CharGenerator.curC;
            if((int)CharGenerator.nextC == 39) {
                nextNextToken = numberToken;
                nextNextName = Integer.toString(ch);
                CharGenerator.readNext();
            } else {
                System.out.print("ILLEGAL CHARACTER CONSTANT");
                Error.error(nextNextLine, "Illegal stuff goin on: '" + CharGenerator.curC + "'!");
                return false;
            }
            System.out.println(ch);
        }
        return true;
    }

    /** 
     * Interprete a textstring and assign the right token
     * @param String txt    The textstring containing the identifier for nextNextToken
     */
    private static void setTextToken(String txt) {

        if (txt.compareTo("int") == 0) {
            System.out.println("--------------> intToken");
            nextNextToken = intToken;
        } else if (txt.compareTo("double") == 0) {
            System.out.println("--------------> doubleToken");
            nextNextToken = doubleToken;
        } else if (txt.equals("for")) {
            System.out.println("--------------> forToken");
            nextNextToken = forToken;
        } else if (nextNextName.compareTo("if") == 0) {
            System.out.println("--------------> ifToken");
            nextNextToken = ifToken;
        } else if (nextNextName.compareTo("else") == 0) {
            System.out.println("--------------> elseToken");
            nextNextToken = elseToken;
        } else if (nextNextName.compareTo("while") == 0) { 
            System.out.println("--------------> whileToken");
            nextNextToken = whileToken;
        } else if (nextNextName.compareTo("return") == 0) { 
            System.out.println("--------------> returnToken");
            nextNextToken = returnToken;
        } else {
            System.out.println("--------------> nameToken -> " + nextNextName);
            nextNextToken = nameToken;
        }
    }
    
    /**
     * Assigns relation tokens their right value by checking nextC
     */
    private static boolean isRelOperator() {
        if (CharGenerator.curC == '!') {
            if (CharGenerator.nextC == '=') {
                CharGenerator.readNext();
                nextNextToken = notEqualToken;
                return true;
            } 
        } else if (CharGenerator.curC == '=') {
            if (CharGenerator.nextC == '=') {
                CharGenerator.readNext();
                nextNextToken = equalToken;
                return true;
            } 
        } else if (CharGenerator.curC == '<') {
            if (CharGenerator.nextC == '=') {
                CharGenerator.readNext();
                nextNextToken = lessEqualToken;
                return true;
            } else {
                nextNextToken = lessToken;
                return true;
            }
        } else if (CharGenerator.curC == '>') {
            if (CharGenerator.nextC == '=') {
                CharGenerator.readNext();
                nextNextToken = greaterEqualToken;
                return true;
            } else {
                nextNextToken = greaterToken;
                return true;
            }	    
        }
        return false;
    }
    /**
     * Method to check and read a whole number. 
     * @ return   true if it is a complete number without any illegal chars,
     * nextNextToken set to numbertoken with name containing the text representation of the number
     */
    private static boolean isNumber() {
        int ascVal = (int)CharGenerator.curC;
        int nextAscVal;
        if (ascVal >= 48 && ascVal <= 57) {
            nextAscVal = (int)CharGenerator.nextC;
            while (nextAscVal >= 48 && nextAscVal <= 57) {
                CharGenerator.readNext();
                nextNextName += CharGenerator.curC;
                nextAscVal = (int)CharGenerator.nextC;
            }
            nextNextToken = numberToken;
            return true;
        }
        return false;
    }



    /**
     * When we find /* we read to the end of comment.
     * Error given when we reach the end of the file without closing comment
     */

    private static void skipComment() {
        boolean end = false;
        System.out.println("DEBUG:\tGot /*multiline*/ comment!");
        int startLine = CharGenerator.curLineNum();
        while (!end) {
            if (CharGenerator.curC == '*' && CharGenerator.nextC == '/') {
                CharGenerator.readNext();  //move to right curC
                end = true;
            }
            else if (CharGenerator.curC == (char)-1) {
                Error.error(nextNextLine,"Found multi line comment in line "+startLine+ " with no end in sight!");
                break;
            }
            else {
                CharGenerator.readNext();
            }
        }
    }

    private static boolean isLetterAZ(char c) {
        // -2 Must be changed in part 0:
        int iv = (int)c; // iv = isoValue
        return ((iv >= 65 && iv <= 90) || (iv >= 97 && iv <=122));
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
        check(t); readNext();
    }

    public static void skip(Token t1, Token t2) {
        check(t1,t2); readNext();
    }
}
