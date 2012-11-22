package no.uio.ifi.cflat.types;

public abstract class Type {
    abstract public int size();
    abstract public String typeName();
    abstract public String typeName2();
    abstract public String typeName3();

    abstract public void checkSameType(int lineNum, Type otherType, String what);
    abstract public void checkType(int lineNum, Type correctType, String what);
    public void genJumpIfZero(String jumpLabel) {}
}
