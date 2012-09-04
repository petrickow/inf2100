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
	// -2 Must be changed in part 0:
	// setter -1 hvis jeg tror den er ferdig
	// setter -0 hvis jeg har begynt men ikke ferdig
	// setter -2 hvis helt ferdig
	return (t == multiplyToken || t == divideToken);
    }

    public static boolean isTermOperator(Token t) {
	// -2 Must be changed in part 0:
	return (t == addToken || t == subtractToken);
    }

    public static boolean isRelOperator(Token t) {
	// -2 Must be changed in part 0:
	return (t == equalToken || t == greaterEqualToken || t == greaterToken || t == lessEqualToken || t == lessToken ||
		t == notEqualToken);
    }

    public static boolean isOperand(Token t) {
	// -2 Must be changed in part 0:
	return (t == assignToken || t == commaToken || t == elseToken || t == eofToken || t == forToken ||
	     t == ifToken || t == leftBracketToken || t == leftCurlToken || t == leftParToken ||
	     t == numberToken || t == rightBracketToken || t == rightCurlToken || t == rightParToken || 
		t == returnToken ||  t == semicolonToken || t == whileToken);
    }

    public static boolean isTypeName(Token t) {
	// -2 Must be changed in part 0:
	return (t == intToken || t == doubleToken);
    }
	
}
