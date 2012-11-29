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
        //1- Must be changed in part 1:
        makeLibrary();
    }

    private static void makeLibrary() {
        library = new GlobalDeclList();

        /*String [] libList = {"exit","getchar","getdouble","getint","putchar","putdouble","putint"};
          for (int i = 0; i < libList.length; i++) {
          library.addDecl(new FuncDecl(libList[i]));
          }*/

        FuncDecl tempLibFunc = new FuncDecl("exit");
        tempLibFunc.paramDecl.numOfPara = 1;
        tempLibFunc.type = Types.intType;    
        library.addDecl(tempLibFunc);

        tempLibFunc = new FuncDecl("getchar");
        tempLibFunc.type = Types.intType;    
        library.addDecl(tempLibFunc);

        tempLibFunc = new FuncDecl("getdouble");
        tempLibFunc.type = Types.doubleType;    
        library.addDecl(tempLibFunc);

        tempLibFunc = new FuncDecl("getint");
        tempLibFunc.type = Types.intType;    
        library.addDecl(tempLibFunc);

        tempLibFunc = new FuncDecl("putchar");
        tempLibFunc.paramDecl.numOfPara = 1;
        tempLibFunc.type = Types.intType;    
        library.addDecl(tempLibFunc);

        tempLibFunc = new FuncDecl("putdouble");
        tempLibFunc.paramDecl.numOfPara = 1;
        tempLibFunc.type = Types.doubleType;    
        library.addDecl(tempLibFunc);

        tempLibFunc = new FuncDecl("putint");
        tempLibFunc.paramDecl.numOfPara = 1;
        tempLibFunc.type = Types.intType;    
        library.addDecl(tempLibFunc);
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

    @Override void check(DeclList curDecls) { //curDecl == library
        progDecls.check(curDecls);

        if (! Cflat.noLink) {
            // Check that 'main' has been declared properly:
            //1- Must be changed in part 2:
            Declaration tempDecl = progDecls.firstDecl;
            boolean mainFound = false;
            while (tempDecl != null) {
                if (tempDecl.name.compareTo("main") == 0 ) {
                    Log.noteBindingMain(tempDecl.lineNum);
                    mainFound = true;
                    break;
                }
                tempDecl = tempDecl.nextDecl;
            }
            if (!mainFound)
                Error.error("Name main is unknown");

        }
    }

    @Override void genCode(FuncDecl curFunc) {
        progDecls.genCode(curFunc);
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
    int totalSize = 0;
    int offset = 0;
    DeclList () {
        //?- Must be changed in part 1:
    }

    @Override void check(DeclList curDecls) {
        outerScope = curDecls;
        Declaration dx = firstDecl;
        while (dx != null) {
            dx.check(this); dx = dx.nextDecl;
        }

    }

    @Override void printTree() {
        //2- Must be changed in part 1:
        Declaration dx = firstDecl;
        while (dx != null) {
            dx.printTree();
            dx = dx.nextDecl;
        }
    }

    void addDecl(Declaration d) {
        //2- Must be changed in part 1:

        if (firstDecl == null) {
            firstDecl = d;
        } else {
            Declaration temp = firstDecl;
            while (temp.nextDecl != null) {
                if (temp.name.compareTo(d.name) == 0) {
                    Error.alreadyDecl(temp.name);
                }
                temp = temp.nextDecl;
            }
            if (temp.name.compareTo(d.name) == 0) {
                Error.alreadyDecl(temp.name);
            }
            temp.nextDecl = d;
        }
    }

    int dataSize() {
        Declaration dx = firstDecl;
        int res = 0;
        int debugCount = 0;
        while (dx != null) {
            debugCount++;
            res += dx.declSize(); dx = dx.nextDecl;
        }
        return res;
    }

    Declaration findDecl(String name, SyntaxUnit usedIn) {
        //1- Must be changed in part 2:

        ParamDeclList copyParamDecl = (ParamDeclList) outerScope;

        Declaration tempDecl = firstDecl;

        //DeclList tempOuterScope = copyParamDecl;
        while (tempDecl != null) {  // Leter igjennom de lokale
            if (tempDecl.name.compareTo(name) == 0) {
                return tempDecl;
            }
            tempDecl = tempDecl.nextDecl;
        }

        if (copyParamDecl != null) {
            tempDecl = copyParamDecl.firstDecl;
        }
        while (tempDecl != null) {  // Leter igjennom parameterne
            if (tempDecl.name.compareTo(name) == 0) {
                return tempDecl;
            }
            tempDecl = tempDecl.nextDecl;
        }

        if (copyParamDecl.outerScope != null) {
            tempDecl = copyParamDecl.outerScope.firstDecl;
        }
        while (tempDecl != null) {  // Leter igjennom de globale
            if (tempDecl.name.compareTo(name) == 0) {
                return tempDecl;
            }
            tempDecl = tempDecl.nextDecl;
        }

        if (copyParamDecl.outerScope.outerScope != null) {
            tempDecl = copyParamDecl.outerScope.outerScope.firstDecl;
        }
        while (tempDecl != null) {  // Leter igjennom de biblioteket
            if (tempDecl.name.compareTo(name) == 0) {
                return tempDecl;
            }
            tempDecl = tempDecl.nextDecl;
        }

        Error.error(usedIn.lineNum, "Name " + name + " is unknown!!");
        return null;
    }
}


/*
 * A list of global declarations.
 * (This class is not mentioned in the syntax diagrams.)
 */
class GlobalDeclList extends DeclList {

    FuncDecl fd = null;                // flyttet hit i del 2 pga maa vaere tilgjengelige for genCode
    GlobalArrayDecl gad = null;
    GlobalSimpleVarDecl gsv = null;

    @Override void genCode(FuncDecl curFunc) {
        //1- Must be changed in part 2:

        Declaration tempDecl = firstDecl;

        while (tempDecl != null) {
            tempDecl.genCode(curFunc);
            tempDecl = tempDecl.nextDecl;
        }

    }

    @Override void parse() {
        while (Token.isTypeName(Scanner.curToken)) {

            if (Scanner.nextToken == nameToken) {
                if (Scanner.nextNextToken == leftParToken) {
                    fd = new FuncDecl(Scanner.nextName);
                    fd.parse();
                    addDecl(fd);
                } else if (Scanner.nextNextToken == leftBracketToken) {
                    gad = new GlobalArrayDecl(Scanner.nextName);
                    gad.parse();
                    addDecl(gad);
                } else {
                    //1- Must be changed in part 1:
                    gsv = new GlobalSimpleVarDecl(Scanner.nextName);
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
    int offset = 0;
    @Override void genCode(FuncDecl curFunc) {
        //1- Must be changed in part 2:
        int tempOffset = 0;

        int lSize = dataSize();

        if (lSize > 0) {
            Code.genInstr("", "subl", "$"+lSize+",%esp", "Get "+lSize +" bytes local data space");
        }
        Declaration dx = firstDecl;

        while (dx != null) {
            dx.offSet =  -(tempOffset + dx.declSize());
	    tempOffset += dx.declSize();
            dx = dx.nextDecl;
        }
    }


    @Override void check(DeclList curDecls) {
        outerScope = curDecls;

        Declaration dx = firstDecl; //sjekker først de lokale decls
        while (dx != null) {
            dx.check(this);
            dx = dx.nextDecl;
        }
    }


    @Override void parse() { //TODO
        //-- Must be changed in part 1:
        while (Token.isTypeName(Scanner.curToken)) { //s lenge den er typeName
            // sjekke om den er "simple-" eller "arrarVarDecl"
            if (Scanner.nextNextToken == semicolonToken) {
                LocalSimpleVarDecl v = new LocalSimpleVarDecl(Scanner.nextName);
                v.parse();
                addDecl(v);
            } else {
                LocalArrayDecl v = new LocalArrayDecl(Scanner.nextName);
                v.parse();
                addDecl(v);
            }
        }
    }
}


/*
 * A list of parameter declarations.
 * (This class is not mentioned in the syntax diagrams.)
 */
class ParamDeclList extends DeclList {
    int numOfPara; // number of parameters
    //DeclList outerScope;

    ParamDeclList () {
    }

    @Override void check (DeclList curDecls) {
        Declaration tempDecl = firstDecl;          
        numOfPara = 0;
        outerScope = curDecls;	       //Setter paramDecl sitt outerScope til å peke på globalDeclList 

        while (tempDecl != null) {
            ((ParamDecl)tempDecl).paramNum = ++numOfPara;
            tempDecl.check(curDecls);
            tempDecl = tempDecl.nextDecl;
        }
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
        Declaration tempDecl = firstDecl;
        int tempOffset = 8; //dataSize();
        
      
	while (tempDecl != null) {
            if (tempDecl == firstDecl) {
		tempDecl.offSet = tempOffset;
	    }  else {
		tempDecl.offSet = tempOffset + tempDecl.declSize();
		tempOffset += tempDecl.declSize();
	    }
	    tempDecl.genCode(curFunc);
            tempDecl = tempDecl.nextDecl;
        }
    }


    @Override void parse() {
        //2- Must be changed in part 1:
        while (Scanner.curToken != rightParToken) {
            Declaration d = new ParamDecl(Scanner.nextName);

            d.parse();
            addDecl(d);

            if (Scanner.curToken != rightParToken)
                Scanner.skip(commaToken);
        }
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
    
    int offSet;     //hamhanding
    int negOffSet;
    
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
}


/*
 * A global array declaration
 */
class GlobalArrayDecl extends VarDecl {
    int nElems = -1;

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
        // OK 
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
        //Syntax.error(use, name + " is an array and no simple variable!");
        type.checkType(use.lineNum, Types.intType, name);
    }

    @Override void genCode(FuncDecl curFunc) {
        //1- Must be changed in part 2:
        Code.genVar(assemblerName, true, declSize(), type.typeName3() + " " + name + "["+((ArrayType)type).nElems+"];");
    }

    @Override void parse() {
        boolean neg = false;
        Log.enterParser("<var decl>");

        //2- Must be changed in part 1:

        Type arrType = Types.getType(Scanner.curToken);
        Scanner.skip(intToken, doubleToken);

        //name = Scanner.curName; //already done in constructor
        Scanner.skip(nameToken);
        Scanner.skip(leftBracketToken);
        
        if (Scanner.curName.equals("-")) {
            neg = true;
            Scanner.skip(subtractToken);
        }
        if (Scanner.curToken != numberToken) 
            Syntax.error(this, "A numberToken expected, but found a " + Scanner.curToken + "!");
        

        nElems = Integer.parseInt(Scanner.curName);
        if (neg)
            nElems = -nElems;
        type = new ArrayType(nElems, arrType);
        Scanner.skip(numberToken);

        Scanner.skip(rightBracketToken);
        Scanner.skip(semicolonToken);

        Log.leaveParser("</var decl>");
    }

    @Override void printTree() {
        //1- Must be changed in part 1:
        Log.wTreeLn(type.typeName() + " " + name + "[" + ((ArrayType)type).nElems + "];");
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
        //1- Must be changed in part 2:
        //duplicate check already in parse
        visible = true;
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
        //2- Must be changed in part 2:	
        type.checkType(use.lineNum, new ArrayType(0,Types.intType), name);
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
        /* OK */
    }


    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
        Code.genVar(assemblerName, true, declSize(), type.typeName() + " " + name + ";");
    }

    @Override void parse() {
        //1- Must be changed in part 1:
        Log.enterParser("<var decl>");
        type = Types.getType(Scanner.curToken);
        Scanner.skip(intToken, doubleToken);
        name = Scanner.curName;
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
        assemblerName = n; //right?
    }

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
        //-- Must be changed in part 2:
        // OK
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
        //1- Must be changed in part 2:
        type.checkType(use.lineNum, Types.intType, name);
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
        Code.genVar(assemblerName, false, declSize(), type.typeName3() + " " + name + "["+((ArrayType)type).nElems+"];");
    }

    @Override void parse() {
        //1- Must be changed in part 1:
        boolean neg = false;
        Log.enterParser("<var decl>");
        Type arrType = Types.getType(Scanner.curToken);
        Scanner.skip(intToken, doubleToken);

        Scanner.skip(nameToken);
        Scanner.skip(leftBracketToken);
        if (Scanner.curName.equals("-")){ //Temp solution, should be in check... scanner does not read -4, just -
            neg = true;
            Scanner.skip(subtractToken);
        }
        if (Scanner.curToken != numberToken)
            Syntax.error(this, "A numberToken expected, but found a " + Scanner.curToken + "!");
        int nElems = Integer.parseInt(Scanner.curName);
        Scanner.skip(numberToken);
        if (neg)
            nElems = -nElems;
        type = new ArrayType(nElems, arrType);

        Scanner.skip(rightBracketToken);
        Scanner.skip(semicolonToken);
        Log.leaveParser("</var decl>");
    }

    @Override void printTree() {
        //1- Must be changed in part 1:
        Log.wTreeLn(type.typeName() + " " + name + "[" + ((ArrayType)type).nElems + "];");
    }

}


/*
 * A local simple variable declaration
 */
class LocalSimpleVarDecl extends VarDecl {
    LocalSimpleVarDecl(String n) {
        super(n);
        assemblerName = n; //right?
    }

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
        //declList offset = total offset... VarDecl offSet = pos relatvie to offset
    }

    @Override void checkWhetherArray(SyntaxUnit use) {
        //-- Must be changed in part 2:
        type.checkType(use.lineNum, new ArrayType(0,Types.intType), name);
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
        //-- Must be changed in part 2:
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
        Code.genVar(assemblerName, false, declSize(), type.typeName() + " " + name + ";");
    }

    @Override void parse() {
        //1- Must be changed in part 1:
        Log.enterParser("<var decl>");
        type = Types.getType(Scanner.curToken);
        Scanner.skip(intToken, doubleToken);
        name = Scanner.curName;
        Scanner.skip(nameToken);
        Scanner.skip(semicolonToken);	
        Log.leaveParser("</var decl>");
    }

    @Override void printTree() {
        //1- Must be changed in part 1:
        Log.wTreeLn(type.typeName() + " " + name + ";");
    }

}


/*
 * A <param decl>
 */
class ParamDecl extends VarDecl {
    int paramNum = 0;
    
    ParamDecl(String n) {
        super(n);
        assemblerName = n;
    }

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2: TODO, sjekk at den faktisk finnes i det lokale skopet og type sjekk

        
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
        type = Types.getType(Scanner.curToken);    
        Scanner.skip(intToken, doubleToken);
        name = Scanner.curName;
        Scanner.skip(nameToken);
        Log.leaveParser("</param decl>");
    }
}


/*
 * A <func decl>
 */

class FuncDecl extends Declaration {

    //-- Must be changed in part 1+2:

    int stackOffset, paramSize;
    // egne
    FuncBody fb = new FuncBody();
    ParamDeclList paramDecl = new ParamDeclList();


    FuncDecl(String n) {
        // Used for user functions:
        //1- Must be changed in part 1:
        super(n);
        assemblerName = (Cflat.underscoredGlobals() ? "_" : "") + n;
    }

    @Override int declSize() {
        return 0;
    }

    @Override void check(DeclList curDecls) {
        //1- Must be changed in part 2:
        paramDecl.check(curDecls);
        paramSize = paramDecl.dataSize();
	fb.check(paramDecl);
    }


    @Override void checkWhetherArray(SyntaxUnit use) {
        //1- Must be changed in part 2:

        // Funksjon skal aldri vare en array uansett saa hvis vi kommer inn hit er det feil
        Error.error(use.lineNum, "" + this.name + " is a function and no array!");

    }

    @Override void checkWhetherFunction(int nParamsUsed, SyntaxUnit use) {
        //-- Must be changed in part 2:
        //TODO nParamsUsed
    }

    @Override void checkWhetherSimpleVar(SyntaxUnit use) {
        //-- Must be changed in part 2:

        // Funksjon skal aldri vare en simple var uansett saa hvis vi kommer inn hit er det feil
        Error.error(use.lineNum, "" + this.name + " is a function and not a simple variable");

    }

    @Override void genCode(FuncDecl curFunc) {
        Code.genInstr("", ".globl", assemblerName, "");
        Code.genInstr(assemblerName, "pushl", "%ebp", "Start function "+name);
        Code.genInstr("", "movl", "%esp,%ebp", "");
        //1- Must be changed in part 2:
        paramDecl.genCode(this);
        fb.genCode(this);

	if (type == Types.doubleType) {
	    Code.genInstr("","fldz","","");
	}
	
        Code.genInstr(".exit$" + assemblerName,"","","");
        Code.genInstr("", "movl", "%ebp,%esp", "");
        Code.genInstr("", "popl", "%ebp", "");
        Code.genInstr("", "ret","", "End function " +name);
    }



    @Override void parse() {
        //1- Must be changed in part 1:

        Log.enterParser("<func decl>");
        type = Types.getType(Scanner.curToken);
	Scanner.skip(intToken, doubleToken);
        name = Scanner.curName;
        Scanner.skip(nameToken);
        Scanner.skip(leftParToken);

        //paramDecl = new ParamDeclList();
        paramDecl.parse();


        Scanner.skip(rightParToken);
        fb.parse();

        Log.leaveParser("</func decl>");

    }

    @Override void printTree() {
        //2- Must be changed in part 1:
        Log.wTreeLn("");
        Log.wTree(type.typeName() + " " + name + " (");
        Declaration pd = paramDecl.firstDecl;
        while (pd != null) {
            Log.wTree(pd.type.typeName() + " " + pd.name);
            if (pd.nextDecl != null) {
                Log.wTree(", ");
                pd = pd.nextDecl;
            }
            else
                break;
        }
        Log.wTreeLn(")");   
        Log.wTreeLn("{");
        Log.indentTree();
        fb.printTree();
        Log.outdentTree();
        Log.wTreeLn("}");   
    }
}

class FuncBody extends SyntaxUnit {

    //DeclList declList = new DeclList();
    StatmList statmlist = new StatmList();
    LocalDeclList localDeclList = new LocalDeclList(); // LocalSimpleVarDecl eller LocalArrayVarDecl

    @Override void check(DeclList currBody) {
        //1- Must be changed in part 2:
        //Param Declerations has been checked in parsing
        // --->  Må uansett gjøre det samme i findDecl. line 200
        //DeclList copyParamDecl = (ParamDeclList) currBody;

        localDeclList.check(currBody);	

        statmlist.check(localDeclList);   //check statements
    }


    @Override void genCode(FuncDecl curBody) {
        //1- Must be changed in part 2:
        localDeclList.genCode(curBody);
        statmlist.genCode(curBody);
    }

    @Override void parse() {
        //1- Must be changed in part 1:
        Log.enterParser("<func body>");

        Scanner.skip(leftCurlToken);
        localDeclList.parse();  //parsere lokale variabler i sin klasse!
        statmlist.parse(); 

        Scanner.skip(rightCurlToken);
        Log.leaveParser("</func body>");
    }

    @Override void printTree() {
        //-- Must be changed in part 1:
        Declaration localDecl = localDeclList.firstDecl;
        while (localDecl != null) {
            localDecl.printTree();
            //Log.wTreeLn(localDecl.type.typeName() + " " + localDecl.name + ";");
            localDecl = localDecl.nextDecl;
        }
        Statement st = statmlist.firstStatm;
        while (st != null) {
            st.printTree();
            st = st.nextStatm;
        }
    }
}

/*
 * A <statm list>.
 */
class StatmList extends SyntaxUnit {
    //-- Must be changed in part 1:

    Statement firstStatm = null;

    @Override void check(DeclList curDecls) {
        //1- Must be changed in  part 2:
        Statement tempStatm = firstStatm;
        while (tempStatm != null) {
            tempStatm.check(curDecls);
            tempStatm = tempStatm.nextStatm;
        }
    }

    @Override void genCode(FuncDecl curFunc) {
        //1- Must be changed in part 2:
        Statement tempStatm = firstStatm;
        while (tempStatm != null) {
            tempStatm.genCode(curFunc);
            tempStatm = tempStatm.nextStatm;
        }
    }

    @Override void parse() {
        Log.enterParser("<statm list>");

        Statement lastStatm = null;
        while (Scanner.curToken != rightCurlToken) {
            Log.enterParser("<statement>");
            //-1 Must be changed in part 1:
            Statement statement = Statement.makeNewStatement();
            statement.parse();

            if (firstStatm == null) {
                firstStatm = lastStatm = statement;
            } else {
                lastStatm.nextStatm = lastStatm = statement;
            }

            Log.leaveParser("</statement>");
            // leser neste token s loopen ikke gr evig
        }
        Log.leaveParser("</statm list>");
    }

    @Override void printTree() {
        //1- Must be changed in part 1:

        Statement tempStatm = firstStatm;
        while (tempStatm != null) {
            tempStatm.printTree(); 
            tempStatm = tempStatm.nextStatm;
        }

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
        //1- Must be changed in part 2:

        // Do nothing
    }

    @Override void parse() {
        //1- Must be changed in part 1:
        Log.enterParser("<empty statm>");
        Scanner.skip(semicolonToken);
        Log.leaveParser("</empty statm>");

    }

    @Override void printTree() {
        //1- Must be changed in part 1:
        Log.wTreeLn(";");
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
        forControl.check(curDecls);
        statmList.check(curDecls);
    }

    @Override void genCode(FuncDecl curFunc) {
        //1- Must be changed in part 2:
	String startLabel = Code.getLocalLabel();
	String exitLabel = Code.getLocalLabel();
	forControl.assignment1.genCode(curFunc);
	Code.genInstr(startLabel, "", "", "Start for-statement");
	forControl.expression.genCode(curFunc);
	Code.genInstr("", "cmpl", "$0,%eax", "");
	Code.genInstr("", "je", exitLabel, "");
	forControl.assignment2.genCode(curFunc);
	statmList.genCode(curFunc);
	Code.genInstr("", "jmp", startLabel, "");
	Code.genInstr(exitLabel, "", "", "End for-statement");
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
        //1- Must be changed in part 1:
        Log.wTree("for ("); 
        forControl.printTree(); 
        Log.wTreeLn(") {");
        Log.indentTree();
        statmList.printTree();
        Log.outdentTree();
        Log.wTreeLn("}");
    }
}

/*
 * A <ForControl>.
 */
//-- Must be changed in part 1+2:
// Klasse vi har opprettet selv

class ForControl extends SyntaxUnit {
    //1- Must be changed in part 1+2:
    Assignment assignment1 = new Assignment();
    Expression expression = new Expression();
    Assignment assignment2 = new Assignment();

    @Override void check(DeclList curDecls) {
        //1- Must be changed in part 2:
        assignment1.check(curDecls);
        expression.check(curDecls);
        assignment2.check(curDecls);	
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
	//assignment1.genCode(curFunc);
	//expression.genCode(curFunc);
	//assignment2.genCode(curFunc);
    }

    @Override void parse() {
        //1- Must be changed in part 1:
        assignment1.parse();
        Scanner.skip(semicolonToken);
        expression.parse();
        Scanner.skip(semicolonToken);
        assignment2.parse();
    }

    @Override void printTree() {
        //1- Must be changed in part 1:
        assignment1.printTree();
        Log.wTree(";  ");
        expression.printTree();
        Log.wTree(";  ");
        assignment2.printTree();
    }
}



// Klasse laget av oss

class AssignStatm extends Statement {
    //1- Must be changed in part 1+2:
    Assignment assignment = new Assignment();


    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
        assignment.check(curDecls);
    }

    @Override void genCode(FuncDecl curFunc) {
        //1- Must be changed in part 2:
        assignment.genCode(curFunc);

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
        assignment.printTree(); 
        Log.wTreeLn(";");
    }
}


// Klasse laget av oss

class Assignment extends SyntaxUnit {
    //1- Must be changed in part 1+2:
    Variable variable = new Variable();
    Expression expression = new Expression();

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
        variable.check(curDecls);
        expression.check(curDecls);
	    /*HAIRY! For å minne oss på at hårete løsninger aldri fungerer!
        Term tempTerm = expression.firstTerm;
        Factor tempFactor = tempTerm.firstFactor;
        
        TermOperator tempTermOp = tempTerm.firstTermOp;
        FactorOperator tempFactorOp = tempFactor.firstFactorOp;
        System.out.println(variable.valType.typeName());
        
        HVIS: vi har en variabel som er av annen type enn høyre side så skal 
          utregningen på høyre side lagres som typen på venstre.
          Dersom factoroperanden
        
        if (tempFactorOp != null && (tempFactorOp.opType != variable.valType ||tempFactor.firstOperand.valType != variable.valType)) {
            
            while (tempTerm != null) {
                while (tempTermOp != null) {
                    tempTermOp.opType = variable.valType;
                    tempTermOp = tempTermOp.nextTermOp;
                }
                while (tempFactor != null) {
                    while (tempFactorOp != null) {
                        tempFactorOp.opType = variable.valType;
                        tempFactorOp = tempFactorOp.nextFactorOp;
                    }
                    
                    tempFactor = tempFactor.nextFactor;
                }
                tempTerm = tempTerm.nextTerm;
            }
        } */
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:

        expression.genCode(curFunc);
        if (expression.valType == variable.valType) {
            //SAVE DOUBLE AS DOUBLE   
            if (variable.valType == Types.doubleType) {
                if (variable.declRef.visible) {
                    Code.genInstr("", "fstpl", variable.varName, variable.varName + " =");
                } else {
                    Code.genInstr("", "fstpl", variable.declRef.offSet+"(%ebp)", variable.varName + " =");
                }
            //INT AS INT
            } else {
                if (variable.declRef.visible) {
                    Code.genInstr("", "movl", "%eax,"+variable.varName, variable.varName + " =");
                } else {
                    Code.genInstr("", "movl", "%eax,"+ variable.declRef.offSet +"(%ebp)", variable.varName + " =");
                }
            }
        } else {
            //SAVE INT AS DOUBLE
            if (variable.valType.typeName().equals("double")) {
                Code.genInstr("", "movl", "%eax,.tmp", ""); //konvertere til flyt
                Code.genInstr("", "fildl", ".tmp", "  (double)");

                if (variable.declRef.visible) {
                    Code.genInstr("", "fstpl", variable.varName, variable.varName + " =");
                } else {
                    Code.genInstr("", "fstpl", variable.declRef.offSet+"(%ebp)", variable.varName + " =");
                }
            }  
            //SAVE DOUBLE AS INT
            else {
                if (variable.declRef.visible) { 
                    Code.genInstr("", "fistpl", variable.varName, variable.varName + " = (int)");
                } else {
                    Code.genInstr("", "fistpl", variable.declRef.offSet+"(%ebp)", variable.varName + " = (int)");
                }
            }
        }
        
        //variable.genCode(curFunc);
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
        //1- Must be changed in part 1:
        variable.printTree();
        Log.wTree(" = ");
        expression.printTree();
    }
}

/*
 * An <if-statm>.
 */
class IfStatm extends Statement {
    //-- Must be changed in part 1+2:
    Expression expression = new Expression();
    StatmList statmList = new StatmList();
    ElsePart elsePart = null;

    @Override void check(DeclList curDecls) {
        //1- Must be changed in part 2:
        expression.check(curDecls);
        statmList.check(curDecls);
        if (elsePart != null)
            elsePart.check(curDecls);
    }

    @Override void genCode(FuncDecl curFunc) {
        //1- Must be changed in part 2:


        Code.genInstr("", "", "", "Start if-statement");
        expression.genCode(curFunc);

        Code.genInstr("", "cmpl", "$0,%eax", "");
        
        
        String exitLabel = Code.getLocalLabel();
        String elseLabel = "";
        
	
	if (elsePart != null) {
            elseLabel = Code.getLocalLabel();
	    Code.genInstr("", "je", elseLabel, "");
	    statmList.genCode(curFunc);
	    Code.genInstr("", "jmp", exitLabel, "");
	}
	else {
	    statmList.genCode(curFunc);
	    Code.genInstr("", "je", exitLabel, "");
	}
	
    
	if (elsePart != null) {
            Code.genInstr(elseLabel, "","","  else-part");
            elsePart.genCode(curFunc);
        }
        Code.genInstr(exitLabel, "", "", "End if-statement");

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
        Log.wTree("if ("); 
        expression.printTree(); 
        Log.wTreeLn(") {");
        Log.indentTree();
        statmList.printTree();
        Log.outdentTree();
        Log.wTree("}");
        if (elsePart != null)
            elsePart.printTree();
        else
            Log.wTreeLn("");
    }
}



class ElsePart extends SyntaxUnit {
    //-- Must be changed in part 1+2:
    StatmList statmList = new StatmList();


    @Override void check(DeclList curDecls) {
        //1- Must be changed in part 2:
        statmList.check(curDecls);
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
        statmList.genCode(curFunc);
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
        Log.wTreeLn(" else {"); 
        Log.indentTree();
        statmList.printTree();
        Log.outdentTree();
        Log.wTreeLn("}");
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
        //1- Must be changed in part 2:
        expression.check(curDecls);
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
        expression.genCode(curFunc);
        Code.genInstr("", "jmp", ".exit$"+curFunc.name, "Return-statement");
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
        //1- Must be changed in part 1:
        Log.wTree("return ");
        expression.printTree();
        Log.wTreeLn(";");
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
        //TODO Er funksjonen definert? Er argumentene riktig type?
        functionCall.check(curDecls);
    }

    @Override void genCode(FuncDecl curFunc) {
        //1- Must be changed in part 2:
        functionCall.genCode(curFunc);
    }

    @Override void parse() {
        //1- Must be changed in part 1:
        Log.enterParser("<call-statm>");
        functionCall.parse();
        Scanner.skip(semicolonToken);
        Log.leaveParser("</call-statm>");
    }

    @Override void printTree() {
        //1- Must be changed in part 1:
        functionCall.printTree();
        Log.wTreeLn(";");
    }
}


//-- Must be changed in part 1+2:


/*
 * An <expression list>.
 */

class ExprList extends SyntaxUnit {
    Expression firstExpr = null;
    Expression lastExpr = null;
    int numOfExp = 0;

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
        Expression tempExpr = firstExpr;
        while (tempExpr != null) {
            numOfExp++;
            tempExpr.check(curDecls);
            tempExpr = tempExpr.nextExpr;
        }
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
        Expression tempExpr = lastExpr;
        int paramCount = numOfExp;
        while (tempExpr != null) {
            tempExpr.genCode(curFunc);
            if (tempExpr.firstTerm.firstFactor.firstOperand.valType == Types.intType) {
                Code.genInstr("", "pushl", "%eax", "Push parameter #"+ (paramCount--));
            } else {
                Code.genInstr("", "subl", "$8,%esp", "");
                Code.genInstr("", "fstpl", "(%esp)", "Push parameter #"+(paramCount--));
            }
            tempExpr = tempExpr.prevExpr;
        }
    }

    @Override void parse() {
        Log.enterParser("<expr list>");
        int i = 0;
        //1- Must be changed in part 1:
        while (Scanner.curToken != rightParToken) {
            if (firstExpr == null) {
                firstExpr = lastExpr =  new Expression();
                firstExpr.parse();
            } else {
                Expression tmp = lastExpr;
                lastExpr.nextExpr = lastExpr = new Expression(); //put in list
                lastExpr.prevExpr = tmp;
                lastExpr.parse();
            }

            if (Scanner.curToken == commaToken) {
                Scanner.skip(commaToken);
            }
        }

        Log.leaveParser("</expr list>");
    }

    @Override void printTree() { 
        //1- Must be changed in part 1:
        Expression tempExpression = firstExpr;

        while (tempExpression != null) {
            tempExpression.printTree();
            tempExpression = tempExpression.nextExpr;
            if (tempExpression != null) {
                Log.wTree(",");
            }
        }
    }
}


/*
 * An <expression> A < B, A!=B... A == (m+n+c) B== (d*a+1)
 */
class Expression extends Operand {
    Expression nextExpr = null;
    Expression prevExpr = null;
    Term firstTerm = new Term(), secondTerm = null;
    Operator relOp = null;
    boolean innerExpr = false;

    Expression () {
        //Empty constructor just to make the Java compiler happy :)
    }

    Expression (boolean innerExpr) {
        this.innerExpr = innerExpr;
    }

    @Override void check(DeclList curDecls) {
        //1- Must be changed in part 2:
        firstTerm.check(curDecls);
        valType = firstTerm.firstFactor.firstOperand.valType;
        if (secondTerm != null) {
            secondTerm.check(curDecls);

            if (firstTerm.firstFactor.firstOperand.valType != secondTerm.firstFactor.firstOperand.valType)
                Error.error(lineNum, "Comparison operands should have the same type, not " + firstTerm.firstFactor.firstOperand.valType.typeName() + " and " + secondTerm.firstFactor.firstOperand.valType.typeName() );
            else
                relOp.opType = valType;
        }
    }

    @Override void genCode(FuncDecl curFunc) {
        //1- Must be changed in part 2:
        firstTerm.genCode(curFunc);
        if (secondTerm != null) {
            if (valType == Types.intType) {
                Code.genInstr("", "pushl", "%eax", ""); //popl?
                secondTerm.genCode(curFunc);

            } else {
                Code.genInstr("", "subl", "$8,%esp", "");
                Code.genInstr("", "fstpl", "(%esp)", "");
                secondTerm.genCode(curFunc);
            }
            relOp.genCode(curFunc);

        }
    }

    @Override void parse() {
        Log.enterParser("<expression>");
        if (innerExpr)
            Scanner.skip(leftParToken);
        firstTerm.parse();


        if (Token.isRelOperator(Scanner.curToken)) {
            relOp = new RelOperator();
            relOp.parse();
            secondTerm = new Term();
            secondTerm.parse();
        }
        if (innerExpr) {
            Scanner.skip(rightParToken);
        }
        Log.leaveParser("</expression>");
    }

    @Override void printTree() {
        //1- Must be changed in part 1:
        if (innerExpr)
            Log.wTree("(");
        firstTerm.printTree();
        if (relOp != null) {
            relOp.printTree();
        }
        if (secondTerm != null) {
            secondTerm.printTree();
        }
        if (innerExpr)
            Log.wTree(")");
    }
}


/*
 * A <term> a+b+c+d...
 */
class Term extends SyntaxUnit {
    //-- Must be changed in part 1+2:
    Factor firstFactor = new Factor();

    Term nextTerm = null;
    TermOperator firstTermOp = null; 

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
        Factor tempFactor = firstFactor;
        Factor prev = null;
        TermOperator tempOp = firstTermOp;

        while (tempFactor != null) {
            tempFactor.check(curDecls);
            if (prev != null) {
                /*TODO 2*/
                if (tempFactor.firstOperand.valType != prev.firstOperand.valType) {
                    Error.error(lineNum, "Operands should have the same type, not " + prev.firstOperand.valType.typeName() + " and " + tempFactor.firstOperand.valType.typeName());
                }
                tempOp.opType = tempFactor.firstOperand.valType;
                tempOp = tempOp.nextTermOp;
            }
            prev = tempFactor;
            tempFactor = tempFactor.nextFactor;
        }
    }

    @Override void genCode(FuncDecl curFunc) {
        //1- Must be changed in part 2:
        Factor tempFactor = firstFactor;
        TermOperator termOperator = firstTermOp; 

        int count = 0;

        while (tempFactor != null) {
            if (count == 1) {
                if (termOperator.opType == Types.intType) {
                    Code.genInstr("", "pushl", "%eax", "");
                } else {
                    Code.genInstr("", "subl", "$8,%esp","");
                    Code.genInstr("", "fstpl", "(%esp)","");
                }
            }
            tempFactor.genCode(curFunc);
            tempFactor = tempFactor.nextFactor;
            count ++;
            if (count == 2) {
                termOperator.genCode(curFunc);
                termOperator = termOperator.nextTermOp;
                count = 1;
            }
        }
    }

    //Trenger while lokke saa vi faar satt inn alle faktorer og operander, prover forst med enkle a+b a-b, saa a*b a/b
    @Override void parse() {
        //1- Must be changed in part 1:
        Log.enterParser("<term>");

        firstFactor.parse();

        if (Token.isTermOperator(Scanner.curToken)) { 
            firstTermOp = new TermOperator();           // lager firstTerm kun naar vi har en termOp,
        }

        Factor tempFactor = firstFactor;
        TermOperator tempTermOp = firstTermOp;

        while (Token.isTermOperator(Scanner.curToken)) { 
            tempTermOp.parse();
            tempTermOp.opType = tempFactor.firstOperand.valType;
            tempFactor.nextFactor = new Factor();
            tempFactor.nextFactor.parse();

            tempTermOp.nextTermOp = new TermOperator();
            tempTermOp = tempTermOp.nextTermOp;
            tempFactor = tempFactor.nextFactor;

        }
        Log.leaveParser("</term>");
    }

    @Override void printTree() {
        //1- Must be changed in part 1+2:
        Factor tempFactor = firstFactor;
        TermOperator tempTermOp = firstTermOp;

        while (tempFactor != null) {
            tempFactor.printTree();
            if (tempTermOp != null) {
                tempTermOp.printTree();
                tempTermOp = tempTermOp.nextTermOp;
            }
            tempFactor = tempFactor.nextFactor; 
        }

    }
}


// egenopprettet klasse

class Factor extends SyntaxUnit {
    //-- Must be changed in part 1+2:
    Operand firstOperand = null;

    Factor nextFactor = null;
    FactorOperator firstFactorOp = null;

    @Override void check(DeclList curDecls) {
        //1- Must be changed in part 2:
        Operand tempOperand = firstOperand;
        Operand prev = null;  

        FactorOperator tempOp = firstFactorOp;
        while (tempOperand != null) {
            tempOperand.check(curDecls);
            if (prev != null) {
                if (tempOperand.valType != prev.valType) {
                    Error.error(lineNum, "Operands should have the same type, not " + prev.valType.typeName() + " and " + tempOperand.valType.typeName());
                }
                tempOp.opType = tempOperand.valType;
                tempOp = tempOp.nextFactorOp;
            }
            prev = tempOperand;
            tempOperand = tempOperand.nextOperand;
        }
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
        Operand tempOperand = firstOperand;
        FactorOperator factorOperator = firstFactorOp;

        int count = 0;

        while (tempOperand != null) {
            if (count == 1) {
                if (factorOperator.opType == Types.intType) {
                    Code.genInstr("", "pushl", "%eax", "");
                } else {
                    Code.genInstr("", "subl", "$8,%esp","");
                    Code.genInstr("", "fstpl", "(%esp)","");
                }
            }
            tempOperand.genCode(curFunc);
            tempOperand = tempOperand.nextOperand;

            count++;
            if (count == 2) {
                factorOperator.genCode(curFunc);
                factorOperator = factorOperator.nextFactorOp;
                count = 1;
            }
        }

    }

    @Override void parse() {
        //1- Must be changed in part 1:
        Log.enterParser("<factor>");
        // lese inn [operand] og [factor opr] i while-loop
        Log.enterParser("<operand>");

        firstOperand = Operand.makeNewOperand();
        firstOperand.parse();

        if (Token.isFactorOperator(Scanner.curToken)) {
            firstFactorOp = new FactorOperator();
        }

        Operand tempOperand = firstOperand;
        FactorOperator tempFactorOp = firstFactorOp;

        while (Token.isFactorOperator(Scanner.curToken)) {
            tempFactorOp.parse();
            //tempFactorOp.opType = tempOperand.valType; //ARRRR
            tempOperand.nextOperand = Operand.makeNewOperand();
            tempOperand.nextOperand.parse();

            tempFactorOp.nextFactorOp = new FactorOperator();
            tempFactorOp = tempFactorOp.nextFactorOp;
            tempOperand = tempOperand.nextOperand;
        }

        Log.leaveParser("</operand>");

        Log.leaveParser("</factor>");
    }

    @Override void printTree() {
        //1- Must be changed in part 1+2:
        Operand tempOperand = firstOperand;
        FactorOperator tempFactorOp = firstFactorOp;

        while (tempOperand != null) {
            tempOperand.printTree();
            if (tempFactorOp != null) {
                tempFactorOp.printTree();
                tempFactorOp = tempFactorOp.nextFactorOp;
            }
            tempOperand = tempOperand.nextOperand;
        }
        //operand.printTree();
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


// egenoprettet klasse
class FactorOperator extends Operator {
    FactorOperator nextFactorOp = null;

    @Override void genCode(FuncDecl curFunc) {
        String comp = "";

        if (opType == Types.doubleType) {
            Code.genInstr("", "fldl", "(%esp)","");
            Code.genInstr("", "addl", "$8,%esp","");
            switch (opToken) {
                case multiplyToken: comp = "fmulp"; break;
                case divideToken: comp = "fdivp"; break;
            }
            Code.genInstr("", comp, "","Compute " + (comp.equals("fmulp") ? "*" : "/") );
	    Code.genInstr("", "movl", "%eax,tmp","");
	    Code.genInstr("", "fildl",".tmp","  "+opType.typeName());

        } else {
            Code.genInstr("", "movl", "%eax,%ecx", "");
            Code.genInstr("", "popl", "%eax", "");
            switch (opToken) {
                case divideToken: 
                    Code.genInstr("", "cdq", "", "");
                    Code.genInstr("", "idivl", "%ecx", "Compute " + "/");
                    break;
                case multiplyToken: 
                    Code.genInstr("", "imull", "%ecx,%eax", "Compute " + "*") ;
                    break;
            } 

        }
    }


    @Override void parse() {
        Log.enterParser("<factor operator>");
        opToken = Scanner.curToken;
        Scanner.skip(multiplyToken, divideToken);
        Log.leaveParser("</factor operator>");
    }

    @Override void printTree() {
        String op = "?";
        if (opToken != null) { 
            switch (opToken) {
                case divideToken: op = "/"; break;
                case multiplyToken: op = "*"; break;
            }
            Log.wTree(" " + op + " ");
        }
    }
}


class TermOperator extends Operator {
    TermOperator nextTermOp = null;

    @Override void genCode(FuncDecl curFunc) {

        String comp = "";

        if (opType.typeName().equals("double")) {
            //Code.genInstr("", "movl", "%eax,.tmp","");
            Code.genInstr("", "fldl", "(%esp)","");
            Code.genInstr("", "addl", "$8,%esp",""); //hardcoded double size
            switch (opToken) {
                case addToken: comp = "faddp"; break;
                case subtractToken: comp = "fsubp"; break;
            }
            Code.genInstr("", comp, "","Compute " + (comp.equals("faddp") ? "+" : "-") );

        } else {  // int
            Code.genInstr("", "movl", "%eax,%ecx", "");
            Code.genInstr("", "popl", "%eax", "");
            switch (opToken) {
                case addToken: comp = "addl"; break;
                case subtractToken: comp = "subl"; break;
            }
            Code.genInstr("", comp, "%ecx,%eax", "Compute " + (comp.equals("addl") ? "+" : "-") );
        }

    }

    @Override void parse() {
        Log.enterParser("<term operator>");
        opToken = Scanner.curToken;
        Scanner.skip(addToken, subtractToken);
        Log.leaveParser("</term operator>");
    }

    @Override void printTree() {
        String op = "?";
        if (opToken != null) { 
            switch (opToken) {
                case addToken: op = "+"; break;
                case subtractToken: op = "-"; break;
            }
            Log.wTree(" " + op + " ");
        }
    }
}

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
            opToken = Scanner.curToken;
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
        if (Scanner.curToken == numberToken || Scanner.curToken == subtractToken) {
            //1- Mustbe changed in part 1:
            return new Number();
        } else if (Scanner.curToken == nameToken && Scanner.nextToken == leftParToken) {
            //1- Must be changed in part 1:
            return new FunctionCall();
        } else if (Scanner.curToken == nameToken) {
            //1- Must be changed in part 1:
            return new Variable();
        } else if (Scanner.curToken == leftParToken) {
            return new Expression(true);
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
    String callName = "";
    int exprSize = 0;

    @Override void check(DeclList curDecls) {
        //2- Must be changed in part 2:
        Declaration d = curDecls.findDecl(callName, this);

        FuncDecl tempFuncDecl = (FuncDecl)d;
        Declaration par = tempFuncDecl.paramDecl.firstDecl;
        if (d.type == null) // function from library
            Log.noteBindingLib(callName, lineNum);
        else {
            Log.noteBinding(d.name, lineNum, d.lineNum);
        }
        exprList.check(curDecls);

        valType = tempFuncDecl.type; //FOR DENNE BLIR IKKE SATT! alltid...
	//d.checkWhetherFunction(exprList.numOfExp, this); //TODO switch the underlying if statement with this...
        if (tempFuncDecl.paramDecl.numOfPara != exprList.numOfExp) {
            Error.error(lineNum, "Calls to " + callName + " should have " + tempFuncDecl.paramDecl.numOfPara + " parameters, not " + exprList.numOfExp + "!");
        }

        Expression paramExp = exprList.firstExpr;
	if (paramExp != null)
	    exprSize += paramExp.valType.size();	
	int atParaNum = 0;
        //TODO, hvorfor får vi nullpointer her om vi ikke har med paramSize testen?        
        while (paramExp != null && tempFuncDecl.paramSize != 0) {
            if (paramExp.valType != par.type)
                Error.error(lineNum, " Parameter #"+ ++atParaNum + " is " + paramExp.valType.typeName() +  ", not " + par.type.typeName());
            else
                atParaNum++;
	    if (paramExp != exprList.firstExpr)
		exprSize += paramExp.valType.size();	
	    paramExp = paramExp.nextExpr;
            par = par.nextDecl;
        }
    }                 

    @Override void genCode(FuncDecl curFunc) {
        //1- Must be changed in part 2:
	Declaration funcDecl = curFunc.fb.localDeclList.findDecl(callName, this);
        exprList.genCode(curFunc);        
        Code.genInstr("", "call", callName, "Call " + callName);

	if (valType == Types.intType) { 
	    // dette er main sin paramsize
	    // functionCall sin exprList sin numOfExp
	    if (exprSize > 0)
		Code.genInstr("", "addl", "$"+exprSize + ",%esp", "Remove parameters"); 
	    //Code.genInstr("", "addl", "$"+((FuncDecl)funcDecl).paramSize + ",%esp", "--Remove parameters"); 
	} else 
	    Code.genInstr("", "fstps", ".tmp","Remove return value."); 
    }

    @Override void parse() {
        //1- Must be changed in part 1:
        Log.enterParser("<function call>");
        callName = Scanner.curName; 
        Scanner.skip(nameToken);
        Scanner.skip(leftParToken);
        exprList.parse();
        Scanner.skip(rightParToken);
        Log.leaveParser("</function call>");
    }

    @Override void printTree() {
        //1- Must be changed in part 1:
        Log.wTree(callName + "(");
        exprList.printTree();
        Log.wTree(")");
    }
}


/*
 * A number.
 */
class Number extends Operand {
    int numVal;

    @Override void check(DeclList curDecls) {
        //-- Must be changed in part 2:
        // TODO - Legge til noe her?
    }

    @Override void genCode(FuncDecl curFunc) {
        Code.genInstr("", "movl", "$"+numVal+",%eax", ""+numVal);
    }

    @Override void parse() {
        //1- Must be changed in part 1:
        Log.enterParser("<number>");
        boolean isNeg = false;
        if (Scanner.curToken == subtractToken) {
            Scanner.readNext();
            isNeg = true;
        }
        numVal = Integer.parseInt(Scanner.curName); 
        if (isNeg) {
            numVal = 0 - numVal;
        }
        valType = Types.intType;
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
        Declaration d = curDecls.findDecl(varName, this);
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
        Log.noteBinding(declRef.name, lineNum, declRef.lineNum);
    }

    @Override void genCode(FuncDecl curFunc) {
        //-- Must be changed in part 2:
        //ARRAY?
        if (declRef.type.typeName2().equals("array") ) {       
            System.out.println("ARRAY");
        }
        else {
            //simple global
            if (declRef.visible) {
                if (valType == Types.intType) {
                    Code.genInstr("", "movl", varName+",%eax", varName);
                } else {
                    Code.genInstr("", "fldl", varName, varName);
                }
            }
            //simple local
                else {
                    if (valType == Types.intType) {
                        Code.genInstr("", "movl", declRef.offSet+"(%ebp),%eax", varName);
                    } else {
                        Code.genInstr("", "fldl", declRef.offSet+"(%ebp)",varName);
                    }
                }
        }
    }

    @Override void parse() {
        //-- Must be changed in part 1:
        Log.enterParser("<variable>");
        varName = Scanner.curName;
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
        //1- Must be changed in part 1:
        Log.wTree(varName);
        if (index != null) {
            Log.wTree("[");
            index.printTree();
            Log.wTree("]");
        }
    }
}
