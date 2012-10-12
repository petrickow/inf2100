package no.uio.ifi.cflat.syntax;

/*
 * module Syntax
 */

import no.uio.ifi.cflat.cflat.Cflat;
import no.uio.ifi.cflat.code.Code;
import no.uio.ifi.cflat.error.Error;
import no.uio.ifi.cflat.log.Log;
import no.uio.ifi.cflat.scanner.Scanner;
import no.uio.ifi.cflat.scanner.Token;
import static no.uio.ifi.cflat.scanner.Token.*;
import no.uio.ifi.cflat.types.*;

/*
 * Creates a syntax tree by parsing;
 * prints the parse tree (if requested);
 * checks it;
 * generates executable code.
 */



public class Syntax {
    static DeclList library;
    static Program program;

    public static void init() {
        //-- Must be changed in part 1:
    }

    public static void finish() {
        //-- Must be changed in part 1:
    }

    public static void checkProgram() {
        program.check(library);
    }

    public static void genCode() {
        program.genCode(null);
    }

    public static void parseProgram() {
        program = new Program();
        program.parse();
    }

    public static void printProgram() {
        program.printTree();
    }

    static void error(SyntaxUnit use, String message) {
        Error.error(use.lineNum, message);
    }
}


/*
 * Master class for all syntactic units.
 * (This class is not mentioned in the syntax diagrams.)
 */
abstract class SyntaxUnit {
    int lineNum;

    SyntaxUnit() {
        lineNum = Scanner.curLine;
    }

    abstract void check(DeclList curDecls);
    abstract void genCode(FuncDecl curFunc);
    abstract void parse();
    abstract void printTree();
}


/*
 * A <program>
 */
class Program extends SyntaxUnit {
    DeclList progDecls = new GlobalDeclList();

    @Override void check(DeclList curDecls) {
        progDecls.check(curDecls);

        if (! Cflat.noLink) {
            // Check that 'main' has been declared properly:
            //-- Must be changed in part 2:
        }
    }

    @Override void genCode(FuncDecl curFunc) {
        progDecls.genCode(null);
    }

    @Override void parse() {
        Log.enterParser("<program>");

        progDecls.parse();
        if (Scanner.curToken != eofToken)
            Error.expected("A decalaration");

        Log.leaveParser("</program>");
    }

    @Override void printTree() {
        progDecls.printTree();
    }
}


/*
 * A declaration list.
 * (This class is not mentioned in the syntax diagrams.)
 */

abstract class DeclList extends SyntaxUnit {
    Declaration firstDecl = null;
    DeclList outerScope;

    DeclList () {
        //-- Must be changed in part 1:
    }

    @Override void check(DeclList curDecls) {
        outerScope = curDecls;

        Declaration dx = firstDecl;
        while (dx != null) {
            dx.check(this); dx = dx.nextDecl;
        }
    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }

    void addDecl(Declaration d) {
        //1- Must be changed in part 1:

        if (firstDecl == null) {
            firstDecl = d;
        } else {
            Declaration temp = firstDecl;
            while (temp.nextDecl != null) {
                temp = temp.nextDecl;
	    }
            temp.nextDecl = d;
        }


    }

    int dataSize() {
        Declaration dx = firstDecl;
        int res = 0;

        while (dx != null) {
            res += dx.declSize(); dx = dx.nextDecl;
        }
        return res;
    }

    Declaration findDecl(String name, SyntaxUnit usedIn) {
        //-- Must be changed in part 2:
        return null;
    }
}


/*
 * A list of global declarations.
 * (This class is not mentioned in the syntax diagrams.)
 */
class GlobalDeclList extends DeclList {
    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {

        while (Token.isTypeName(Scanner.curToken)) {
            if (Scanner.nextToken == nameToken) {
                if (Scanner.nextNextToken == leftParToken) {
                    FuncDecl fd = new FuncDecl(Scanner.nextName);
                    fd.parse();
                    addDecl(fd);
                } else if (Scanner.nextNextToken == leftBracketToken) {
                    GlobalArrayDecl gad = new GlobalArrayDecl(Scanner.nextName);
                    gad.parse();
                    addDecl(gad);
                } else {
		    //1- Must be changed in part 1:
		    // her kommer globale var deklarasjoner
		    GlobalSimpleVarDecl gsv = new GlobalSimpleVarDecl(Scanner.nextName);
		    gsv.parse();
		    addDecl(gsv);
		}
            } else {
                Error.expected("A declaration");
            }
        }
    }
}


/*
 * A list of local declarations.
 * (This class is not mentioned in the syntax diagrams.)
 */
class LocalDeclList extends DeclList {
    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //-- Must be changed in part 1:
    }
}


/*
 * A list of parameter declarations.
 * (This class is not mentioned in the syntax diagrams.)
 */
class ParamDeclList extends DeclList {
    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //-- Must be changed in part 1:
        // Log.enterParser("<param decl>");
        // Scanner.skip(intToken, doubleToken);
	// Scanner.skip(nameToken);
	// Log.leaveParser("</param decl>");
    }
}


/*
 * Any kind of declaration.
 * (This class is not mentioned in the syntax diagrams.)
 */
abstract class Declaration extends SyntaxUnit {
    String name, assemblerName;
    Type type;
    boolean visible = false;
    Declaration nextDecl = null;

    Declaration(String n) {
        name = n;
    }

    abstract int declSize();

    /**
     * checkWhetherArray: Utility method to check whether this Declaration is
     * really an array. The compiler must check that a name is used properly;
     * for instance, using an array name a in "a()" or in "x=a;" is illegal.
     * This is handled in the following way:
     * <ul>
     * <li> When a name a is found in a setting which implies that should be an
     * array (i.e., in a construct like "a["), the parser will first
     * search for a's declaration d.
     * <li> The parser will call d.checkWhetherArray(this).
     * <li> Every sub-class of Declaration will implement a checkWhetherArray.
     * If the declaration is indeed an array, checkWhetherArray will do
     * nothing, but if it is not, the method will give an error message.
     * </ul>
     * Examples
     * <dl>
     * <dt>GlobalArrayDecl.checkWhetherArray(...)</dt>
     * <dd>will do nothing, as everything is all right.</dd>
     * <dt>FuncDecl.checkWhetherArray(...)</dt>
     * <dd>will give an error message.</dd>
     * </dl>
     */
    abstract void checkWhetherArray(SyntaxUnit use);

    /**
     * checkWhetherFunction: Utility method to check whether this Declaration
     * is really a function.
     *
     * @param nParamsUsed Number of parameters used in the actual call.
     * (The method will give an error message if the
     * function was used with too many or too few parameters.)
     * @param use From where is the check performed?
     * @see checkWhetherArray
     */
    abstract void checkWhetherFunction(int nParamsUsed, SyntaxUnit use);

    /**
     * checkWhetherSimpleVar: Utility method to check whether this
     * Declaration is really a simple variable.
     *
     * @see checkWhetherArray
     */
    abstract void checkWhetherSimpleVar(SyntaxUnit use);
}


/*
 * A <var decl>
 */
abstract class VarDecl extends Declaration {
    VarDecl(String n) {
        super(n);
    }

    @Override int declSize() {
        return type.size();
    }

    @Override void checkWhetherFunction(int nParamsUsed, SyntaxUnit use) {
        Syntax.error(use, name + " is a variable and no function!");
    }

    @Override void printTree() {
        Log.wTree(type.typeName() + " " + name);
        Log.wTreeLn(";");
    }

    //-- Must be changed in part 1+2:
}


/*
 * A global array declaration
 */
class GlobalArrayDecl extends VarDecl {
    GlobalArrayDecl(String n) {
        super(n);
        assemblerName = (Cflat.underscoredGlobals() ? "_" : "") + n;
    }

    @Override void check(DeclList curDecls) {
        visible = true;
        if (((ArrayType)type).nElems < 0)
            Syntax.error(this, "Arrays cannot have negative size!");
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
        /* OK */
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
        Syntax.error(use, name + " is an array and no simple variable!");
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        Log.enterParser("<var decl>");
	
        //1- Must be changed in part 1:
	Scanner.skip(intToken, doubleToken);
	Scanner.skip(nameToken);
	Scanner.skip(leftBracketToken);
	Scanner.skip(numberToken);
	Scanner.skip(rightBracketToken);
	Scanner.skip(semicolonToken);

        Log.leaveParser("</var decl>");
    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }
}


/*
 * A global simple variable declaration
 */
class GlobalSimpleVarDecl extends VarDecl {
    GlobalSimpleVarDecl(String n) {
        super(n);
        assemblerName = (Cflat.underscoredGlobals() ? "_" : "") + n;
    }

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
        //-- Must be changed in part 2:
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
        /* OK */
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //1- Must be changed in part 1:
	Log.enterParser("<var decl>");
	Scanner.skip(intToken, doubleToken);
	Scanner.skip(nameToken);
        Scanner.skip(semicolonToken);
	Log.leaveParser("</var decl>");
    }
}


/*
 * A local array declaration
 */
class LocalArrayDecl extends VarDecl {
    LocalArrayDecl(String n) {
        super(n);
    }

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
        //-- Must be changed in part 2:
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //1- Must be changed in part 1:
	Log.enterParser("<var decl>");
	Scanner.skip(intToken, doubleToken);
	Scanner.skip(nameToken);
	Scanner.skip(leftBracketToken);
	Scanner.skip(numberToken);
	Scanner.skip(rightBracketToken);
	Scanner.skip(semicolonToken);
	Log.leaveParser("</var decl>");
    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }

}


/*
* A local simple variable declaration
*/
class LocalSimpleVarDecl extends VarDecl {
    LocalSimpleVarDecl(String n) {
        super(n);
    }

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
        //-- Must be changed in part 2:
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //1- Must be changed in part 1:
	Log.enterParser("<var decl>");
	Scanner.skip(intToken, doubleToken);
	Scanner.skip(nameToken);
	Scanner.skip(semicolonToken);	
	Log.leaveParser("</var decl>");
    }
}


/*
* A <param decl>
*/
class ParamDecl extends VarDecl {
    int paramNum = 0;

    ParamDecl(String n) {
        super(n);
    }

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
        //-- Must be changed in part 2:
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
         //1- Must be changed in part 1:
	Log.enterParser("<param decl>");
        Scanner.skip(intToken, doubleToken);
	Scanner.skip(nameToken);
	Log.leaveParser("</param decl>");
        
    }
}


/*
* A <func decl>
*/
class FuncDecl extends Declaration {

    //-- Must be changed in part 1+2:

    // egne
    FuncBody fb = new FuncBody();
    ParamDecl paramDecl;

    FuncDecl(String n) {
        // Used for user functions:

        super(n);
        assemblerName = (Cflat.underscoredGlobals() ? "_" : "") + n;
        //-- Must be changed in part 1:
    }

    @Override int declSize() {
        return 0;
    }

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
        //-- Must be changed in part 2:
    }

    @Override void checkWhetherFunction(int nParamsUsed, SyntaxUnit use) {
        //-- Must be changed in part 2:
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        Code.genInstr("", ".globl", assemblerName, "");
        Code.genInstr(assemblerName, "pushl", "%ebp", "Start function "+name);
        Code.genInstr("", "movl", "%esp,%ebp", "");
        //-- Must be changed in part 2:
    }



    @Override void parse() {
        //-- Must be changed in part 1:

        Log.enterParser("<func decl>");
        
        Scanner.skip(intToken, doubleToken);
        Scanner.skip(nameToken);
        Scanner.skip(leftParToken);

        while (Token.isTypeName(Scanner.curToken)) {
	    paramDecl = new ParamDecl(Scanner.curName);
	    paramDecl.parse();
            if (Scanner.curToken == commaToken) {
		Scanner.skip(commaToken);
	    }
	}
        Scanner.skip(rightParToken);
	fb.parse();
	
        Log.leaveParser("</func decl>");
	
    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }
}


class FuncBody extends SyntaxUnit {

    // DeclList declList = new DeclList();
    StatmList stmlist = new StatmList();
    

    LocalDeclList localDeclList = new LocalDeclList(); // LocalSimpleVarDecl eller LocalArrayVarDecl
    
    @Override void check(DeclList currBody) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl currBody) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //-- Must be changed in part 1:
        Log.enterParser("<func body>");

        Scanner.skip(leftCurlToken);
	
	while (Token.isTypeName(Scanner.curToken)) {
            // TODO - gå igjennom alle parameterene i declList
            // sjekke om den er "simple-" eller "arrarVarDecl"
	    
	    if (Scanner.nextNextToken == semicolonToken) {
		LocalSimpleVarDecl v = new LocalSimpleVarDecl(Scanner.nextName);
		v.parse();
		localDeclList.addDecl(v);
	    } else {
		LocalArrayDecl v = new LocalArrayDecl(Scanner.nextName);
		v.parse();
		localDeclList.addDecl(v);
	    }
        }
        
	stmlist.parse();

        Scanner.skip(rightCurlToken);
        Log.leaveParser("</func body>");
    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }
}




/*
* A <statm list>.
*/
class StatmList extends SyntaxUnit {
    //-- Must be changed in part 1:

    Statement firstStatm = null;

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        Log.enterParser("<statm list>");

        Statement lastStatm = null;
        while (Scanner.curToken != rightCurlToken) {
            Log.enterParser("<statement>");
            //-1 Must be changed in part 1:
            Statement statement = Statement.makeNewStatement();

            statement.parse();

            if (lastStatm == null) {
                firstStatm = lastStatm = statement;
            } else {
                lastStatm.nextStatm = lastStatm = statement;
            }

            Log.leaveParser("</statement>");
            // leser neste token så loopen ikke går evig
	}
        Log.leaveParser("</statm list>");
    }
    
    @Override void printTree() {
        //-- Must be changed in part 1:
    }
}


/*
* A <statement>.
*/
abstract class Statement extends SyntaxUnit {
    Statement nextStatm = null;

    static Statement makeNewStatement() {
        if (Scanner.curToken==nameToken &&
                Scanner.nextToken==leftParToken) {
            //1- Must be changed in part 1:
            return new CallStatm();
        } else if (Scanner.curToken == nameToken) {
            //1- Must be changed in part 1:
            return new AssignStatm();
        } else if (Scanner.curToken == forToken) {
            //1- Must be changed in part 1:
            return new ForStatm();
        } else if (Scanner.curToken == ifToken) {
            return new IfStatm();
        } else if (Scanner.curToken == returnToken) {
            //1- Must be changed in part 1:
            return new ReturnStatm();
        } else if (Scanner.curToken == whileToken) {
            return new WhileStatm();
        } else if (Scanner.curToken == semicolonToken) {
            return new EmptyStatm();
        } else {
            Error.expected("A statement");
        }
        return null; // Just to keep the Java compiler happy. :-)
    }
}




/*
* An <empty statm>.
*/
class EmptyStatm extends Statement {
    //-- Must be changed in part 1+2:

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //1- Must be changed in part 1:
        Log.enterParser("<empty statm>");
        Scanner.skip(semicolonToken);
        Log.leaveParser("</empty statm>");

    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }
}


/*
* A <for-statm>.
*/
//-- Must be changed in part 1+2:
// Klasse vi har opprettet selv

class ForStatm extends Statement {
    //1- Must be changed in part 1+2:
    ForControl forControl = new ForControl();
    StatmList statmList = new StatmList();

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //1- Must be changed in part 1:
	Log.enterParser("<for-statm>");
	Scanner.skip(forToken);	
	Scanner.skip(leftParToken);
	forControl.parse();
	Scanner.skip(rightParToken);
	Scanner.skip(leftCurlToken);
	statmList.parse();
	Scanner.skip(rightCurlToken);
	Log.leaveParser("</for-statm>");
    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }
}

/*
* A <ForControl>.
*/
//-- Must be changed in part 1+2:
// Klasse vi har opprettet selv

class ForControl extends SyntaxUnit {
    //1- Must be changed in part 1+2:
    Assignment assignment = new Assignment();
    Expression expression = new Expression();

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //1- Must be changed in part 1:

	assignment.parse();
	Scanner.skip(semicolonToken);
	expression.parse();
	Scanner.skip(semicolonToken);
	assignment.parse();


    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }
}



// Klasse laget av oss

class AssignStatm extends Statement {
    //1- Must be changed in part 1+2:
    Assignment assignment = new Assignment();
    
    
    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //1- Must be changed in part 1:
	Log.enterParser("<assign-statm>");
	assignment.parse();
	Scanner.skip(semicolonToken);
	Log.leaveParser("</assign-statm>");
	
    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }
}


// Klasse laget av oss

class Assignment extends SyntaxUnit {
    //1- Must be changed in part 1+2:
    Variable variable = new Variable();
    Expression expression = new Expression();
    
    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //1- Must be changed in part 1:
	Log.enterParser("<assignnment>");
	variable.parse();
	Scanner.skip(assignToken);
	expression.parse();
	Log.leaveParser("</assigment>");
	
    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }
}





/*
* An <if-statm>.
*/
class IfStatm extends Statement {
    //-- Must be changed in part 1+2:
    Expression expression = new Expression();
    StatmList statmList = new StatmList();
    ElsePart elsePart;

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //1- Must be changed in part 1:
	Log.enterParser("<if-statm>");
	Scanner.skip(ifToken);
	Scanner.skip(leftParToken);
	expression.parse();
	Scanner.skip(rightParToken);
	Scanner.skip(leftCurlToken);
	statmList.parse();
	Scanner.skip(rightCurlToken);
	if (Scanner.curToken == elseToken) {
	    elsePart = new ElsePart();
	    elsePart.parse();
	}
	Log.leaveParser("</if-statm>");
    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }
}



class ElsePart extends SyntaxUnit {
    //-- Must be changed in part 1+2:
    StatmList statmList = new StatmList();
    

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //1- Must be changed in part 1:
	Log.enterParser("<else-part>");
	Scanner.skip(elseToken);
	Scanner.skip(leftCurlToken);
	statmList.parse();
	Scanner.skip(rightCurlToken);
	Log.leaveParser("</else-part>");
    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }
}



/*
* A <return-statm>.
*/
//-- Must be changed in part 1+2:
// klasse vi har laget selv

class ReturnStatm extends Statement {
    //1- Must be changed in part 1+2:
    Expression expression = new Expression();

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //1- Must be changed in part 1:
	Log.enterParser("<return-statm>");
	Scanner.skip(returnToken);
	expression.parse();
	Scanner.skip(semicolonToken);
	Log.leaveParser("</return-statm>");
    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }
}




/*
* A <while-statm>.
*/
class WhileStatm extends Statement {
    Expression expression = new Expression();
    StatmList statmList = new StatmList();

    @Override void check(DeclList curDecls) {
        expression.check(curDecls);
        statmList.check(curDecls);
    }

    @Override void genCode(FuncDecl curFunc) {
        String testLabel = Code.getLocalLabel(),
               endLabel = Code.getLocalLabel();

        Code.genInstr(testLabel, "", "", "Start while-statement");
        expression.genCode(curFunc);
        expression.valType.genJumpIfZero(endLabel);
        statmList.genCode(curFunc);
        Code.genInstr("", "jmp", testLabel, "");
        Code.genInstr(endLabel, "", "", "End while-statement");
    }

    @Override void parse() {
        Log.enterParser("<while-statm>");

        Scanner.skip(whileToken);
        Scanner.skip(leftParToken);
        expression.parse();
        Scanner.skip(rightParToken);
        Scanner.skip(leftCurlToken);
        statmList.parse();
        Scanner.skip(rightCurlToken);

        Log.leaveParser("</while-statm>");
    }

    @Override void printTree() {
        Log.wTree("while ("); expression.printTree(); Log.wTreeLn(") {");
        Log.indentTree();
        statmList.printTree();
        Log.outdentTree();
        Log.wTreeLn("}");
    }
}

// CallStatm -> klasse som er opprettet av oss
class CallStatm extends Statement {
    //1- Must be changed in part 1+2:
    FunctionCall functionCall = new FunctionCall();

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //1- Must be changed in part 1:
        Log.enterParser("<call-statm>");
	functionCall.parse();
        Scanner.skip(semicolonToken);
        Log.leaveParser("</call-statm>");
    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }
}


//-- Must be changed in part 1+2:


/*
* An <expression list>.
*/

class ExprList extends SyntaxUnit {
    Expression firstExpr = null;

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        Expression lastExpr = null;

        Log.enterParser("<expr list>");

        //1- Must be changed in part 1:
        while (Scanner.curToken != rightParToken) {
            if (firstExpr == null) {
                firstExpr = lastExpr = new Expression();
                firstExpr.parse();
            } else {
                lastExpr.nextExpr = lastExpr = new Expression(); //put in list
		lastExpr.parse();
            }
	    // TODO -- må også ta høyde for at det kan være flere expressions her med komma imellom
	    if (Scanner.curToken == commaToken) {
		Scanner.skip(commaToken);
	    }
	    //System.out.print(Scanner.curToken + " " + Scanner.nextToken + " " + Scanner.nextNextToken);
	}
        
        Log.leaveParser("</expr list>");
    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }
    //-- Must be changed in part 1:
}


/*
* An <expression>
*/
class Expression extends Operand {
    Expression nextExpr = null;
    Term firstTerm = new Term(), secondTerm = null;
    Operator relOp = null;
    boolean innerExpr = false;

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        Log.enterParser("<expression>");
	
	firstTerm.parse();
        if (Token.isRelOperator(Scanner.curToken)) {
	    relOp = new RelOperator();
            relOp.parse();
            secondTerm = new Term();
            secondTerm.parse();
        }

        Log.leaveParser("</expression>");
    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }
}


/*
* A <term>
*/
class Term extends SyntaxUnit {
    //-- Must be changed in part 1+2:
    Factor factor = new Factor();
    
    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //1- Must be changed in part 1:
        Log.enterParser("<term>");
        
	factor.parse();
	while (Token.isTermOperator(Scanner.curToken)) {
	    Log.enterParser("<term operator>");
	    Scanner.skip(addToken, subtractToken);
	    Log.leaveParser("</term operator>");
	    factor.parse();
	}
	
	
	
        Log.leaveParser("</term>");
    }

    @Override void printTree() {
        //-- Must be changed in part 1+2:
    }
}


// egenopprettet klasse

class Factor extends SyntaxUnit {
    //-- Must be changed in part 1+2:
    Operand operand;
    
    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //1- Must be changed in part 1:
        Log.enterParser("<factor>");
	// lese inn [operand] og [factor opr] i while-loop
	

	// Skal kanskje ikke ligge her, men fant ikke noe annet logisk sted å putte den
	// siden klassen operand er abstract
	Log.enterParser("<operand>");
	operand = Operand.makeNewOperand();
	operand.parse();
	Log.leaveParser("</operand>");

        Log.leaveParser("</factor>");
    }
    
    @Override void printTree() {
        //-- Must be changed in part 1+2:
    }
}





//-- Must be changed in part 1+2:

/*
* An <operator>
*/
abstract class Operator extends SyntaxUnit {
    Operator nextOp = null;
    Type opType;
    Token opToken;

    @Override void check(DeclList curDecls) {}
}


//-- Must be changed in part 1+2:


/*
* A relational operator (==, !=, <, <=, > or >=).
*/

class RelOperator extends Operator {
    @Override void genCode(FuncDecl curFunc) {
        if (opType == Types.doubleType) {
            Code.genInstr("", "fldl", "(%esp)", "");
            Code.genInstr("", "addl", "$8,%esp", "");
            Code.genInstr("", "fsubp", "", "");
            Code.genInstr("", "fstps", Code.tmpLabel, "");
            Code.genInstr("", "cmpl", "$0,"+Code.tmpLabel, "");
        } else {
            Code.genInstr("", "popl", "%ecx", "");
            Code.genInstr("", "cmpl", "%eax,%ecx", "");
        }
        Code.genInstr("", "movl", "$0,%eax", "");
        switch (opToken) {
            case equalToken:
                Code.genInstr("", "sete", "%al", "Test =="); break;
            case notEqualToken:
                Code.genInstr("", "setne", "%al", "Test !="); break;
            case lessToken:
                Code.genInstr("", "setl", "%al", "Test <"); break;
            case lessEqualToken:
                Code.genInstr("", "setle", "%al", "Test <="); break;
            case greaterToken:
                Code.genInstr("", "setg", "%al", "Test >"); break;
            case greaterEqualToken:
                Code.genInstr("", "setge", "%al", "Test >="); break;
        }
    }

    @Override void parse() {
        Log.enterParser("<rel operator>");
	
        if (Token.isRelOperator(Scanner.curToken)) {
	    Scanner.readNext();
	} else {
	    Error.expected("A rel operator");
	}

        Log.leaveParser("</rel operator>");
    }

    @Override void printTree() {
        String op = "?";
        switch (opToken) {
            case equalToken: op = "=="; break;
            case notEqualToken: op = "!="; break;
            case lessToken: op = "<"; break;
            case lessEqualToken: op = "<="; break;
            case greaterToken: op = ">"; break;
            case greaterEqualToken: op = ">="; break;
        }
        Log.wTree(" " + op + " ");
    }
}


/*
* An <operand>
*/
abstract class Operand extends SyntaxUnit {
    Operand nextOperand = null;
    Type valType;

    // egenopprettet metode
    static Operand makeNewOperand() {

        if (Scanner.curToken == numberToken) {
            //1- Must be changed in part 1:
            return new Number();
        } else if (Scanner.curToken == nameToken && Scanner.nextToken == leftParToken) {
            //1- Must be changed in part 1:
            return new FunctionCall();
        } else if (Scanner.curToken == nameToken) {
            //1- Must be changed in part 1:
            return new Variable();
        } else if (Scanner.curToken == ifToken) {
            //return new IfStatm(); // Må også ha med operandtypen ( expression )
	    return null;
} else {
	    Error.expected("A operand");
        }
        return null; // Just to keep the Java compiler happy. :-)
    }

}


/*
* A <function call>.
*/
class FunctionCall extends Operand {
    //-- Must be changed in part 1+2:
    ExprList exprList = new ExprList();

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //1- Must be changed in part 1:
        Log.enterParser("<function call>");
        Scanner.skip(nameToken);
        Scanner.skip(leftParToken);
        exprList.parse();
        Scanner.skip(rightParToken);
        Log.leaveParser("</function call>");
    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }
    //-- Must be changed in part 1+2:
}


/*
* A <number>.
*/
class Number extends Operand {
    int numVal;

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        Code.genInstr("", "movl", "$"+numVal+",%eax", ""+numVal);
    }

    @Override void parse() {
        //1- Must be changed in part 1:
        Log.enterParser("<number>");
	Scanner.skip(numberToken);
	Log.leaveParser("</number>");
    }

    @Override void printTree() {
        Log.wTree("" + numVal);
    }
}


/*
* A <variable>.
*/

class Variable extends Operand {
    String varName;
    VarDecl declRef = null;
    Expression index = null;
    

    @Override void check(DeclList curDecls) {
        Declaration d = curDecls.findDecl(varName,this);
        if (index == null) {
            d.checkWhetherSimpleVar(this);
            valType = d.type;
        } else {
            d.checkWhetherArray(this);
            index.check(curDecls);
            index.valType.checkType(lineNum, Types.intType, "Array index");
            valType = ((ArrayType)d.type).elemType;
        }
        declRef = (VarDecl)d;
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
    }

    @Override void parse() {
        //-- Must be changed in part 1:
	Log.enterParser("<variable>");
	Scanner.skip(nameToken);
	if (Scanner.curToken == leftBracketToken) { 
	    Scanner.skip(leftBracketToken);
	    index = new Expression();
	    index.parse();
	    Scanner.skip(rightBracketToken);
	}
	Log.leaveParser("</variable>");
        
    }

    @Override void printTree() {
        //-- Must be changed in part 1:
    }
}


