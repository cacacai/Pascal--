package me.ichengzi.experiment.compiler;

import java.util.*;


public class TextLex {
	private static List<Token> tokenList = new ArrayList<>();
    private static List<Error> errorList = new ArrayList<Error>();
	private String text;
	private int text_length;
	private int row_number=1;
    private Map<String,String> key =new HashMap<>();
    private Map<String,String> identifierList =new HashMap<>();
    private int identifierCount=0;

	public TextLex(String text){
		this.text = text;
		text_length = text.length();
	}

	//a-z,A-Z
	public int isAlpha(char c){
		if(((c<='z')&&(c>='a')) || ((c<='Z')&&(c>='A')) || (c=='_'))
			return 1;
		else 
			return 0;
	}

	//0-9
	public int isNumber(char c){
		if((c>='0')&&(c<='9'))
			return 1;
		else 
			return 0;
	}


	public int isKey(String t){
        for (String ch : key.keySet()){
            if (t.equals(ch)){
                return Integer.parseInt(key.get(ch));
            }
        }
		return 0;
	}

	// 处理整个字符串,循环读取
	public boolean scannerAll(){
		int i=0;
		char c;
		text = text+'\0';
		//循环读取每个字符
		while(i<text_length){
			c = text.charAt(i);
			if(c==' '||c=='\t')
				i++;
			else if (c=='\r'||c=='\n') {//回车/换行
				row_number++;
				i++;
			}
			else 
				i=scannerPart(i);
		}
        //printIdenty(identifierList);
        //System.out.println((Object) identifierList);
        if (errorList.isEmpty()){
            tokenList.add(new Token("EOF", Token.TokenKind.EOF));
            return true;
        }else{
            return false;
        }
	}

	//扫描字符串
	public int scannerPart(int arg0){
		int i=arg0;
		char ch = text.charAt(i);
		String s="";
		// 第一个输入的字符是字母
		if (isAlpha(ch)==1) {
			s = ""+ch;
			return handleFirstAlpha(i, s);
		}
		// 第一个是数字的话
		else if (isNumber(ch)==1) {
			s = ""+ch;
			return handleFirstNum(i, s);
		}
		// 既不是既不是数字也不是字母
		else {
			s = ""+ch;
			switch (ch) {
			case ' ':
			case '\n':
			case '\r':
			case '\t':
				return ++i;
            case '(':
                tokenList.add(new Token(s,Token.TokenKind.LPAREN));
                return ++i;
            case ')':
                tokenList.add(new Token(s,Token.TokenKind.RPAREN));
                return ++i;
            case '{':
                return handleNote(i, s);
            case '}':
                return ++i;
			case ':':
				if(text.charAt(i+1)=='='){
					s = s+"=";
                    tokenList.add(new Token(s,Token.TokenKind.ASSIGNMENT));
					return i+2;
				}else {
                    error(":不能识别");
                    return i+1;
                }
			case ',':
                tokenList.add(new Token(s,Token.TokenKind.COMMA));
                return ++i;
			case '.':
                tokenList.add(new Token(s,Token.TokenKind.POIT));
                return ++i;
			case ';':
                tokenList.add(new Token(s,Token.TokenKind.SEMI));
                return ++i;
			case '+':
                tokenList.add(new Token(s,Token.TokenKind.PLUS));
                return ++i;
			case '*':
                tokenList.add(new Token(s,Token.TokenKind.MUL));
                return ++i;
			case '=':
                tokenList.add(new Token(s,Token.TokenKind.EQUAL));
                return ++i;
			case '>':
				return handleMore(i, s);
			case '<':
				return handleLess(i, s);
			default:
				// 输出暂时无法识别的字符
                error("输出暂时无法识别的字符"+ch);
                return ++i;
			}
		}
	}

    /**
     * 获取key或者标识符
     * @param arg 当前读取的字符位置
     * @param arg0 开始的第一个字符
     * @return
     */
	public int handleFirstAlpha(int arg, String arg0) {
        int i = arg;
        String s = arg0;
        char ch = text.charAt(++i);
        while (isAlpha(ch) == 1 || isNumber(ch) == 1) {//从开始第一个为字符类型一直读取到不是字符或者数字为止
            s = s + ch;
            ch = text.charAt(++i);
        }
        // 到了结尾
        switch (s){
            case "PROGRAM":
                tokenList.add(new Token(s, Token.TokenKind.PROGRAM)); break;
            case "BEGIN":
                tokenList.add(new Token(s, Token.TokenKind.BEGIN)); break;
            case "END":
                tokenList.add(new Token(s, Token.TokenKind.END)); break;
            case "INTEGER":
                tokenList.add(new Token(s, Token.TokenKind.INTEGER)); break;
            case "IF":
                tokenList.add(new Token(s, Token.TokenKind.IF)); break;
            case "THEN":
                tokenList.add(new Token(s, Token.TokenKind.THEN)); break;
            case "VAR":
                tokenList.add(new Token(s, Token.TokenKind.VAR));
                break;
                default:
                    if (!identifierList.containsKey(s)){
                        identifierList.put(s,identifierCount+"");
                        identifierCount++;
                    }
                    tokenList.add(new Token(s, Token.TokenKind.IDENTIFIER,Integer.parseInt(identifierList.get(s))));
                    return i;
        }
        if (ch!=' '&&ch!='\n'&&ch!='\t'&&ch!='\r'&&ch!=';'&&ch!='{'){
            error(ch+"关键字后不能有字符");
        }
        return i;
    }
	
	public int handleFirstNum(int arg, String arg0){
		int i = arg;
		char ch = text.charAt(++i);
		String s = arg0;
		while(isNumber(ch)==1){
			s = s+ch;
			ch = text.charAt(++i);
		}
        if((text.charAt(i)==' ')||(text.charAt(i)=='\t')||(text.charAt(i)=='\n')||(text.charAt(i)=='\r')||(text.charAt(i)=='\0')||(isKey(text.charAt(i)+"")>0)){
			// 到了结尾，输出数字
            tokenList.add(new Token(s, Token.TokenKind.INTEGER));
			return i;
		}
		else if (ch=='+'||ch=='-'||ch=='*'||ch=='/'||ch=='\0') {
            tokenList.add(new Token(s, Token.TokenKind.INTEGER));
			return i;
		}
		else if (ch=='+'||ch=='-'||ch=='*'||ch=='/'||ch=='\0') {
            tokenList.add(new Token(s, Token.TokenKind.INTEGER));
			return i;
		}
		else {
			do {
				ch = text.charAt(i++);
				s = s+ch;
			} while ((text.charAt(i)!=' ')&&(text.charAt(i)!='\t')&&(text.charAt(i)!='\n')&&(text.charAt(i)!='\r')&&(text.charAt(i)!='\0'));
			error( s+",错误的标识符");
			return i;
		}
	}

	// 处理注释,没有考虑不闭合的情况,注释格式{}
	public int handleNote(int arg, String arg0){
		int i = arg;
		char ch=text.charAt(++i);
		String s = arg0+ch;
		ch = text.charAt(++i);
		while (ch!='}') {
			s = s+ch;
			if (ch=='\r'||ch=='\n') {
				row_number++;
			}
			else if (ch=='\0') {
			    error(s+"注释没有闭合");
				return i;
			}
			ch = text.charAt(++i);
		}
		return i+1;
	}


    //>
	public int handleMore(int arg, String arg0){

		int i=arg;
		char ch = text.charAt(++i);
		String s = arg0;
		if (ch=='='){
			s = s+ch;
			// 输出运算符
            tokenList.add(new Token(s,Token.TokenKind.GREATER_OR_EQUAL));
            return ++i;
		}
		else if(ch=='>'){
			s = s+ch;
			// 输出运算符
            error(">> 运算符错误");
            return ++i;
		}
		else{
			// 输出运算符
            tokenList.add(new Token(s,Token.TokenKind.GREATER));
            return ++i;
		}
	}

	//<
	public int handleLess(int arg, String arg0){
		int i=arg;
		String s = arg0;
		char ch = text.charAt(++i);
		if (ch=='='){
			s = s+ch;
			// 输出运算符
            tokenList.add(new Token(s,Token.TokenKind.LESS_OR_EQUAL));
            return ++i;
		}
		else if(ch=='>'){
			s = s+ch;
			// 输出运算符
            tokenList.add(new Token(s,Token.TokenKind.NOT_EQUAL));
            return ++i;
		}
		else{
			// 输出运算符
            tokenList.add(new Token(s,Token.TokenKind.LESS));
            return ++i;
		}
	}


    public List<Token> getTokenList() {
        return tokenList;
    }

    public List<Error> getErrorList() {
        return errorList;
    }

    private void error(String message){
	    errorList.add(new Error(row_number,message));
    }

    class Error{

        int line;
        String msg;

        public Error(int line, String msg) {
            this.line = line;
            this.msg = msg;
        }

        public int getLine() {
            return line;
        }

        public String getMsg() {
            return msg;
        }
    }

}