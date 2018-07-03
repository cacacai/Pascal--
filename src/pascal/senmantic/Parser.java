package pascal.senmantic;

import java.util.ArrayList;
import java.util.List;

/**
 *  语法分析器。
 * @author cai
 */
public class Parser {

    private List<Token> tokenList;
    private List<Quaternary> quaternaryList;
    private int NXQ = 0;
    private int temp=0;
    private int lineNum = 1;
    private Token current;
    private String arg2="";


    public Parser(List<Token> tokenList,List<Quaternary> quaternaryList) {
        this.tokenList = tokenList;
        this.quaternaryList=quaternaryList;
    }

    //获取下一个字符
    private void next(){
        current = tokenList.remove(0);//删除的同时返回被删除的元素
        while (current.getTokenKind()== Token.TokenKind.EOLN){
            lineNum++;
            current = tokenList.remove(0);
        }
    }

    /**
     * 用于判断多个候选式的时候向前预测
     * @param index
     * @return
     */
    private Token peek(int index){
        int len = tokenList.size();
        int i = 0;
        Token result = null;
        while(index >=0 && i<len){
            result = tokenList.get(i++);
            if (result.getTokenKind() != Token.TokenKind.EOLN){
                index--;
            }
        }
        if (index>=0){
            return null;
        }else{
            return result;
        }
    }

    private Token peek(){
        return peek(0);
    }

    /**
     *  语义分析
     */
    //根据给的实参产生一个四元式
    int gen(String op,String arg1,String arg2,String result){
        Quaternary quaternary=new Quaternary(op, arg1, arg2, result);
        quaternaryList.add(quaternary);
        NXQ++;
        return NXQ;
    }

    //产生临时变量的函数
    int newTemp(){
        return temp++;
    }

    //开始
    public void parse(){
        program();
    }

    //语法分析
    private void program(){
        next();
        //程序->PROGRAM<标识符>;<分程序>
        if (current.getTokenKind()==Token.TokenKind.PROGRAM){//program
            next();//标识符
            if (isIDENTIFIER()){
                next();//IDENTIFIER
                if (isSEMI()){
                    //分程序-><变量说明>BEGIN<语句表>END
                    next();
                    //如果存在着变量说明
                    if (current.getTokenKind()==Token.TokenKind.VAR){
                        //变量说明
                        declarationTable();
                    }else{
                        error("ERROR：缺少VAR!违背产生式：<变量说明>->VAR<变量说明表>");
                        return;
                    }
                    //BEGIN<语句表>END
                    subProgram();
                }else {
                    error("ERROR：程序名后缺少“;”！违背产生式：<程序>->PROGRAM<标识符>;<分程序>");
                }
                //next();
            }else{
                error("ERROR：缺少程序名! 违背产生式：<程序>->PROGRAM<标识符>;<分程序>");
            }
        }else{
                error("ERROR：开始缺少PROGRAM! 违背产生式：<程序>->PROGRAM<标识符>;<分程序>");
        }

    }

    //BEGIN<语句表>END.
    private void subProgram(){
        next();
        if (isBEGIN()){
            //语句表
            next();
            try {//捕抓异常，强行中断递归
                statementTable();
            }catch (Exception e){
            }
            //next();
            Token token=peek();
            //END.
            if (isEND() &&token.getTokenKind()==Token.TokenKind.POIT){
                // successful
            }else {
                error("ERROR：缺少结束标记“END.”!违背产生式：<分程序>-><变量说明>BEGIN<语句表>END");
            }
        }else{
            error("ERROR：变量说明后缺少“BEGIN”!违背产生式：<分程序>-><变量说明>BEGIN<语句表>END");
        }
    }

    //语句表-><语句>|<语句>;<语句表>
    private void statementTable(){
        //语句
        statement();
        Token token=peek();
        if (isSEMI() &&token.getTokenKind()!=Token.TokenKind.END){
            next();
            statementTable();
        }else if(isEND()){

        }else if (isSEMI() &&token.getTokenKind()==Token.TokenKind.END){
            statement();
        }else {
            error("ERROR：缺少“;”！违背产生式：<语句表>-><语句>|<语句>;<语句表>");
        }


    }

    //语句->赋值语句/条件语句/while语句/复合语句
    private void statement(){
        if (isIDENTIFIER()){
            assignStatement();
        }else if (current.getTokenKind()==Token.TokenKind.IF){//if
            ifStatement();
        }else if (current.getTokenKind()==Token.TokenKind.WHILE){//while
            whileStatement();
        }else if (isBEGIN()){//复合语句
            complexStatement();
        }else if (isEND()){
            return;//current.getTokenKind()==Token.TokenKind.SEMI||
        }else if (current.getTokenKind()==Token.TokenKind.EOLN){
            lineNum++;
        }else{
            if (current.getTokenKind()==Token.TokenKind.EOF){
                error("ERROR： 代码结束");
            }
            error("ERROR：非法的语句");
        }
    }

    /**
     *
     *     赋值语句->变量:=算术表达式
     */
    private void assignStatement(){
        String op=":=";
        String arg1= current.getSymbol();
        next();
        //匹配 :=
        if (current.getTokenKind()==Token.TokenKind.ASSIGNMENT){
            //算术表达式
            next();
            arithmeticExpression();
        }else{
            error(" ERROR：变量后缺少赋值符号“:=”! 违背产生式：<赋值语句>-><变量>:=<算术表达式>");
            return;
        }
        gen(op,arg1,arg2,"-");

    }

    //if
    // 条件语句
    private void ifStatement(){
        //关系表达式
        next();
        conditionExpression();
        if (current.getTokenKind() == Token.TokenKind.THEN){
            next();
        }else {
            error("ERROR：关系表达式后缺少“THEN”! 违背产生式：<条件语句>->IF<关系表达式>THEN<语句>ELSE<语句>");
        }
        statement();
        //next();//吃掉 ；
        if (current.getTokenKind()==Token.TokenKind.ELSE){
            next();
        }else{
            error("ERROR：语句后缺少“ELSE”!违背产生式：<条件语句>->IF<关系表达式>THEN<语句>ELSE<语句>");
        }
        statement();
    }

    //while
    private void whileStatement(){
        next();
        conditionExpression();
        if (current.getTokenKind()==Token.TokenKind.DO){
            next();
            statement();
        }else {
            error("ERROR：关系表达式后缺少“DO”!违背产生式：<While语句>->WHILE<关系表达式>DO<语句>");
        }
    }

    //复合语句
    private void complexStatement(){
        //语句表
        next();
        statementTable();
        if (isEND()){
            next();
        }else{
            error("ERROR：缺少“END”!违背产生式：<复合语句>->BEGIN<语句表>END");
            return;
        }

    }

    //变量说明表->变量表:类型|变量表:类型;变量说明表
    // 默认只处理Integer变量类型
    private void declarationTable(){
        //变量表
        declarationStatement();//一直读取到 : 结束
        //多个变量表
        //next();
        if (isCOLON()){
            //处理 :Integer
            next();
            Token token = peek();
            //INTEGER;    结尾
            if (isINTEGER()){
                next();
                token = peek();
                //变量表结束了
                if (isSEMI() &&token.getTokenKind()==Token.TokenKind.BEGIN){
                    return;
                }else if (!isSEMI()){
                    error("ERROR：缺少“;”!违背产生式：<变量说明表>-><变量表>:<类型>|<变量表>:<类型>;<变量说明表>");
                }
                declarationTable();
            }else {
                error("ERROR：缺少类型INTEGER!违背产生式：<类型>->INTEGER;");
            }
        }else{
            error("ERROR：变量表后缺少“:”!违背产生式：<变量说明表>-><变量表>:<类型>|<变量表>:<类型>;<变量说明表>");
            return;
        }

    }




    /**
     * 第一次进来current为VAR
     * 读取变量说明
     * 变量说明->VAR 变量说明表
     */
    private void declarationStatement(){
        next();
        if (isIDENTIFIER()){
            next();
            if (isCOMMA()){
                declarationStatement();//如果后面是分号说明还有变量
            }else if (isCOLON()){

            }else{
                error("ERROR：缺少 , 违背产生式：<变量表>-><变量>|，<变量>;变量表");
            }
        }else{
            error("ERROR：缺少标识符!违背产生式：<变量>-><标识符>");
        }
    }

    //<算术表达式>-><项>|<算术表达式>+<项>|<算术表达式>-<项>
    private void arithmeticExpression(){
        //因式->变量/数字/(因式)
        if (isIDENTIFIER() || isINT() || isLPAREN()){
            //项
            item();
        }else{
            error("ERROR：算术表达式不合法，缺少项！违背产生式：<算术表达式>-><项>|<算术表达式>+<项>|<算术表达式>-<项>");
            return;
        }
        if (current.getTokenKind()==Token.TokenKind.PLUS||current.getTokenKind()==Token.TokenKind.SUB){
            String op=current.getSymbol();
            String arg1=arg2;
            next();
            arithmeticExpression();
            int quaternaryNum=newTemp();
            String result="T"+quaternaryNum;
            gen(op,arg1,arg2,result);
            arg2=result;
        }
    }

    //项  <项>-><因式>|<项>*<因式>|<项>/<因式>
    private void item(){
        if (isIDENTIFIER()||isINT()||isLPAREN()){
            arg2=current.getSymbol();
            //因式
            factor();
        }else{
            error("ERROR：算术表达式不合法，缺少项！违背产生式：<项>-><因式>|<项>*<因式>|<项>/<因式>");
            return;
        }
        Token.TokenKind temp=current.getTokenKind();
        if (temp==Token.TokenKind.MUL||temp==Token.TokenKind.DIV){
            String op=current.getSymbol();
            String arg1=arg2;
            next();
            item();
            int quaternaryNum=newTemp();
            String result="T"+quaternaryNum;
            gen(op,arg1,arg2,result);
            arg2=result;
        }
    }

    //因式->变量|常数|（算术表达式）
    private void factor(){
        //变量或者常数
        if (isIDENTIFIER() || isINT()){
            arg2=current.getSymbol();
            next();
        }else if(isLPAREN()){//(
            next();
            arithmeticExpression();
            if (current.getTokenKind()==Token.TokenKind.RPAREN){
                next();
            }else {
                error("ERROR：缺少“)”！");
                return;
            }
        }else{
            error("ERROR：这个因式不合法!!!!!！");
            return;
        }
    }



    //关系表达式->算术表达式 关系符 算术表达式
    private void conditionExpression(){
        String op=current.getSymbol();
        //算术表达式
        arithmeticExpression();
        //关系符
        relationOperator();
        //算术表达式
        if (isINT() || isIDENTIFIER()){
            arithmeticExpression();
        }else{
            error("ERROR：算术表达式非法!违背产生式：<关系表达式>-><算术表达式><关系符><算术表达式>");
        }
    }

    //关系符
    private void relationOperator(){
        if (current.getTokenKind() == Token.TokenKind.LESS//<
                || current.getTokenKind() == Token.TokenKind.LESS_OR_EQUAL//<=
                || current.getTokenKind() == Token.TokenKind.GREATER//>
                || current.getTokenKind() == Token.TokenKind.GREATER_OR_EQUAL//>=
                || current.getTokenKind() == Token.TokenKind.EQUAL//=
                || current.getTokenKind() == Token.TokenKind.NOT_EQUAL//不等于
                ){
            next();
        }else{
            error("ERROR：缺少关系符号!违背产生式：<关系表达式>-><算术表达式><关系符><算术表达式>");
        }
    }

    //报错直接退出
    private void error(String err_msg){
        System.out.println(lineNum +" "+err_msg+" currentToken:"+current.getSymbol());
        System.exit(0);
    }

    private boolean isBEGIN() {
        return current.getTokenKind()==Token.TokenKind.BEGIN;
    }
    private boolean isEND() {
        return current.getTokenKind() == Token.TokenKind.END;
    }
    private boolean isIDENTIFIER() {
        return current.getTokenKind() == Token.TokenKind.IDENTIFIER;
    }
    private boolean isCOMMA() {
        return current.getTokenKind()==Token.TokenKind.COMMA;
    }
    private boolean isLPAREN() {
        return current.getTokenKind() == Token.TokenKind.LPAREN;
    }
    private boolean isINT() {
        return current.getTokenKind()==Token.TokenKind.INT;
    }
    private boolean isCOLON() {
        return current.getTokenKind()==Token.TokenKind.COLON;
    }
    private boolean isSEMI() {
        return current.getTokenKind()==Token.TokenKind.SEMI;
    }
    private boolean isINTEGER() {
        return current.getTokenKind() == Token.TokenKind.INTEGER;
    }
}