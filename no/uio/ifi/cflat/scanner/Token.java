package no.uio.ifi.cflat.scanner;

/*
 * class Token
 */

/*
 * The different kinds of tokens read by Scanner.
 */
public enum Token { 
    addToken, assignToken,   
    commaToken,             
    divideToken, doubleToken,
    elseToken, eofToken, equalToken, 
    forToken, 
    greaterEqualToken, greaterToken, 
    ifToken, intToken, 
    leftBracketToken, leftCurlToken, leftParToken, lessEqualToken, lessToken, 
    multiplyToken, 
    nameToken, notEqualToken, numberToken, 
    rightBracketToken, rightCurlToken, rightParToken, returnToken, 
    semicolonToken, subtractToken, 
    whileToken;

    public static boolean isFactorOperator(Token t) {
	// -1 Must be changed in part 0:
	// if (t.compareTo(multiplyToken) == 0 || t.compareTo(divideToken) == 0) {
	if (t.compareTo("*") == 0 || t.compareTo("/") == 0) {
	    return true;
	} else {
	    return false;
	}
    }

    public static boolean isTermOperator(Token t) {
	// -1 Must be changed in part 0:
	// addToken, substractToken
	if (t.compareTo("+") == 0 || t.compareTo("-") == 0) {
	    return true;
	} else {
	    return false;
	}
    }

    public static boolean isRelOperator(Token t) {
	// -1 Must be changed in part 0:
	// equalToken(==), greaterEqualToken(>=), greaterToken(>), lessEqualToken(<=), lessToken(<), notEqualToken(!=)
	if (t.compareTo("==") == 0 || t.compareTo(">=") == 0 || t.compareTo(">") == 0 ||
	    t.compareTo("<=") == 0 || t.compareTo("<") == 0 || t.compareTo("!=") == 0) {
	    return true;
	} else {
	    return false;
	}
    }

    public static boolean isOperand(Token t) {
	// -- Must be changed in part 0:
	// assignToken, commaToken, elseToken, eofToken, forToken, ifToken,
	// leftBracketToken, leftCurlToken, leftParToken, numberToken, 
	// rightBracketToken, rightCurlToken, rightParToken, returnToken, semicolonToken, whileToken
	return false;
    }

    public static boolean isTypeName(Token t) {
	// -1 Must be changed in part 0:
	// intToken, doubleToken
	if (t.compareTo("int") == 0 || t.compareTo("double") == 0) {
	    return true;
	} else {
	    return false;
	}
    }
	
}
