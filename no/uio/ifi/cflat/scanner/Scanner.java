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
            nextNextLine = CharGenerator.curLineNum(); // Denne skal være her i følge prekoden

            if (! CharGenerator.isMoreToRead()) {
                System.out.println("Last Char");
                nextNextToken = eofToken;
            } else {
                //-- Must be changed in part 0:
                //-- Skal bli på rundt 400-500 linjer, vi har mer å lese

                
                nextNextName = ""; //vi har en ny nextnext...

                CharGenerator.readNext(); //flyttet denne ut for å unngå dobbel/trippel kode
                nextNextName += CharGenerator.curC; //leser første tegn og tester på det
                
                /*OBS! denne whilen gjør den overordnede overflødig, sjekket med grlærer at det er ok*/
                if (isLetterAZ(CharGenerator.curC)) { //ENTEN int, double eller nameToken, tåler tall i navn
                    while (!(isReserved(CharGenerator.nextC)) && CharGenerator.nextC != ' ' && !(isIllegalInText(CharGenerator.curC))){
			CharGenerator.readNext();
                        nextNextName += CharGenerator.curC;
                    }
                    setTextToken(nextNextName);
		}
                else if (isReserved(CharGenerator.curC)) { //ALLE reserverte enkelt-tegn
                    //System.out.println("IS RESERVED" + CharGenerator.curC);
                    
		    // her har jeg laget en metode som skal erstatte koden under. neste 20 linjene
		    // som heter isAnotherToken
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
                        System.out.println("--------------> assignToken");
                    } else if(nextNextName.equals("/")) {
                        if (CharGenerator.nextC == '*')
                            skipComment();
                        else {
                            nextNextToken = divideToken;
                        }
                    } else if(nextNextName.equals("'")) {
                        // håndtere verdi inne i ' ' som verdi...
                        // Kanskje legge dette inne i en egen metode?
			System.out.print("--------------> fnutt --> ");
			int ch = (int) CharGenerator.nextC;
			CharGenerator.readNext();
			if((int)CharGenerator.nextC == 39) {
			    nextNextToken = numberToken;
			    nextNextName = Integer.toString(ch);
			    CharGenerator.readNext();
			} else {
			    System.out.print("ILLEGAL CHARACTER CONSTANT");
			    // kalle på ERROR
			}
			System.out.println(ch);
			
		    }
                    //...osv
                }
                else if (CharGenerator.curC == ' ') { //hopp over
                    //TODO midlertidig løsning
                }
		else if ((int)CharGenerator.curC == 9) { //hopp over
			//TODO midlertidig løsning - horizontal tab
		} 
                else {
                    Error.error(nextNextLine,"Illegal symbol: '" + CharGenerator.curC + "'!");
                }

                
            }
        }
        Log.noteToken();
    }
    private static boolean isReserved(char c) {
        int ch = (int)c;
        //ASCII values of reserved characters
        if ((ch >= 33 && ch <= 45) || (ch == 47) || (ch >= 58 && ch <= 62) || (ch >= 91 && ch <=93) || ch == 96 || (ch >= 123 && ch <=125)) {
            return true;
        }
        else
            return false;
    }
    
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

    private static void setTextToken(String txt) {
	
	if (txt.compareTo("int") == 0) {
	    System.out.println("-------------->  intToken");
	    nextNextToken = intToken;
	} else if (txt.compareTo("double") == 0) {
	    System.out.println("--------------> doubleToken");
	    nextNextToken = doubleToken;
	} else if (txt.equals("for")) {
	    System.out.println("--------------> forToken");
	    nextNextToken = forToken;
	} else if (nextNextName.compareTo("if") == 0) {
	    nextNextToken = ifToken;
	} else if (nextNextName.compareTo("else") == 0) {
	    nextNextToken = elseToken;
	} else if (nextNextName.compareTo("while") == 0) {
	    nextNextToken = whileToken;
	}else {
	    System.out.println("--------------> nameToken -> " + nextNextName);
	    nextNextToken = nameToken;
	}
    }
    





    /**
* Om vi har en /* så leser vi til vi finner avsluttningen
*/
    private static void skipComment() {
        boolean end = false;
        System.out.println("DEBUG:\tGot /*multiline*/ comment!");
        
        while (!end) {
            if (CharGenerator.curC == '*' && CharGenerator.nextC == '/') {
                CharGenerator.readNext(); CharGenerator.readNext(); //move to right curC
                end = true;
            }
            else if (CharGenerator.curC == (char)-1) {
                Error.error(nextNextLine,"Found multi line comment without end!");
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