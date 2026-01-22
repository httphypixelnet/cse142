package compsci.labs.critters.shared;

public enum OpCode {
    MOVE,
    COLOR,
    TO_STRING;
    public static OpCode fromInt(int code) throws IndexOutOfBoundsException {
        return values()[code - 1];
    }
    public static OpCode deserialize(String s) throws IndexOutOfBoundsException {
        return fromInt(Integer.parseInt(s.substring(0,1)));
    }
}
