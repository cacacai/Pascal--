package pascal.senmantic;

/**
 * 变量表
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
    public Token(String name,TokenKind kind,Integer index) {
        symbol = name;
        tokenKind = kind;
        this.index=index;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public TokenKind getTokenKind() {
        return tokenKind;
    }

    public void setTokenKind(TokenKind tokenKind) {
        this.tokenKind = tokenKind;
    }
    public int getIndex() {
        return this.index;
    }

    public void setIndex(Integer index) {
        index = index;
    }

    /**
     * 单词所有的种类
     */
    protected enum TokenKind{
        PROGRAM("PROGRAM",0),
        BEGIN("BEGIN",1),
        END("END",2),
        INTEGER("INTEGER",3),
        INT("INT",4),
        WHILE("WHILE",5),
        IF("IF",6),
        THEN("THEN",7),
        ELSE("ELSE",8),
        DO("DO",9),
        ASSIGNMENT(":=",10),
        PLUS("+",11),
        MUL("*",12),
        LPAREN("(",13),
        RPAREN(")",14),
        LESS("<",15),
        LESS_OR_EQUAL("<=",16),
        GREATER(">",17),
        GREATER_OR_EQUAL(">=",18),
        NOT_EQUAL("<>",19),
        EQUAL("=",20),
        SUB("-",21),
        SEMI(";",22),
        COMMA(",",23),
        IDENTIFIER("IDENTIFIER",24),
        POIT(".",25),
        EOLN("EOLN",26),
        EOF("EOF",27),
        COLON(":",28),
        DIV("/",29),
        VAR("VAR",30);
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

    @Override
    public String toString() {
        return "Token{" +
                "symbol='" + symbol + '\'' +
                ", tokenKind=" + tokenKind.toString() +
                '}';
    }
}
