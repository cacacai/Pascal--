package pascal.textlex;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 词法分析器
 *  1500310210蔡嘉盛
 *
 */
public class Main {

    public static void main(String[] args) {
        Reader reader = null;
        Writer tokenWriter = null;
        Writer errWriter = null;
        String text="";
        try {
            String fileName = "codeLex.txt";
            Scanner in=new Scanner(new File(fileName));
            while (in.hasNextLine()){
                text+=in.nextLine();
                text+='\n';
            }
            reader = new FileReader(fileName);
            TextLex textLex=new TextLex(text);

            boolean success = textLex.scannerAll();
            List<Token> tokenList = textLex.getTokenList();
            List<TextLex.Error> errorList = textLex.getErrorList();
            tokenWriter = new FileWriter("lex.dyd");
            String temp="";
            for (Token token:tokenList){
                temp+=token.getSymbol()+" "+token.getTokenKind().no+" "+token.getIndex();
                temp+="\n";
            }
            tokenWriter.write(temp);
            tokenWriter.flush();
            //输出错误信息
            if (!success){
                String errFile = "code.err";
                errWriter = new FileWriter(errFile);
                errWriter.flush();
                for (TextLex.Error error:errorList){
                    errWriter.write("line"+error.getLine()+":"+error.getMsg()+"\n");
                    System.out.println("line"+error.getLine()+":"+error.getMsg());
                }
            }else{
                System.out.println("没有错误");
            }
            Map<Token,String> keyTemp=new HashMap<>();
            System.out.println("************变量表**********");
            for (Token token:tokenList){
                if (token.getIndex()!=-1&&!keyTemp.containsValue(token.getSymbol())){
                    System.out.println(token.getSymbol()+" "+token.getIndex());
                    keyTemp.put(token,token.getSymbol());
                }
            }
            System.out.println("************变量表**********");
            for (Token token:tokenList){
                if (token.getTokenKind()!=Token.TokenKind.EOLN){
                    System.out.format("%s\t\t\t%d\n",token.getSymbol(),token.getTokenKind().no);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (reader!=null){
                    reader.close();
                }
                if (tokenWriter!=null){
                    tokenWriter.flush();
                    tokenWriter.close();
                }
                if(errWriter!=null){
                    errWriter.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }



}
