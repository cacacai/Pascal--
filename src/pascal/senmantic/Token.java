package pascal.senmantic;

/**
 * 符号表
 *
 */
public class Token {

    private String symbol;
    private TokenKind tokenKind;
    private Integer index=-1;//标识符的序号


    public Token(String name,TokenKind kind) {
        symbol = name;
        tokenKind = kind;
    }
    public Token(String name,TokenKind kind,Integer in) {
        symbol = name;
        tokenKind = kind;
        index=in;
    }

    public String getSymbol() {
        return symbol;
    }

    public TokenKind getTokenKind() {
        return tokenKind;
    }

    public int getIndex() {
        return index;
    }
    @Override
    public String toString() {
        return "Token{" +
                "symbol='" + symbol + '\'' +
                ", tokenKind=" + tokenKind.toString() +
                '}';
    }
    /**
     * 单词所有的种类
     */
    protected enum TokenKind{
        PROGRAM("PROGRAM",0),
        BEGIN("BEGIN",1),
        END("END",2),
        VAR("VAR",3),
        INTEGER("INTEGER",4),
        IDENTIFIER("IDENTIFIER",5),
        WHILE("WHILE",6),
        IF("IF",7),
        THEN("THEN",8),
        ELSE("ELSE",9),
        DO("DO",10),
        PLUS("+",11),
        SUB("-",12),
        MUL("*",13),
        DIV("/",14),
        ASSIGNMENT(":=",15),
        LESS("<",16),
        LESS_OR_EQUAL("<=",17),
        GREATER(">",18),
        GREATER_OR_EQUAL(">=",19),
        NOT_EQUAL("<>",20),
        EQUAL("=",21),
        SEMI(";",22),
        COMMA(",",23),
        LPAREN("(",24),
        RPAREN(")",25),
        POIT(".",26),
        COLON(":",27),
        EOLN("EOLN",28),
        EOF("EOF",29);
        public final String name;
        public final int no;
        TokenKind(String name, int no) {
            this.name = name;
            this.no = no;
        }

        @Override
        public String toString() {
            return "TokenKind{" +
                    "name='" + name + '\'' +
                    ", no=" + no +
                    '}';
        }
    }


}
