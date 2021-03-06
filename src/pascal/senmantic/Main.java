package pascal.senmantic;

import java.io.*;
import java.util.*;

/**
 * 语义分析
 * 1500310210 蔡嘉盛
 * 只完成了赋值语句分析
 */
public class Main {
    private static List<Token> tokenList=null;
    private static List<Quaternary> quaternaryList=null;


    public static void main(String[] args) {
        Reader reader = null;
        Writer tokenWriter = null;
        Writer errWriter = null;
        String text="";
        try {
            String fileName = "code.txt";
            Scanner in=new Scanner(new File(fileName));
            while (in.hasNextLine()){
                text+=in.nextLine();
                text+='\n';
            }
            reader = new FileReader(fileName);
            TextLex textLex=new TextLex(text);

            boolean success = textLex.scannerAll();
            tokenList= textLex.getTokenList();
            quaternaryList=new ArrayList<>();
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
                System.out.println("词法错误");
                String errFile = "code.err";
                errWriter = new FileWriter(errFile);
                errWriter.flush();
                for (TextLex.Error error:errorList){
                    errWriter.write("line"+error.getLine()+":"+error.getMsg()+"\n");
                    System.out.println("line"+error.getLine()+":"+error.getMsg());
                }
                System.exit(0);
            }else{
                System.out.println("没有错误");
            }
            /*Map<Token,String> keyTemp=new HashMap<>();
            System.out.println("************变量表**********");
            for (Token token:tokenList){
                if (token.getIndex()!=-1&&!keyTemp.containsValue(token.getSymbol())){
                    System.out.println(token.getSymbol()+" "+token.getIndex());
                    keyTemp.put(token,token.getSymbol());
                }
            }
            System.out.println("************变量表**********");
            for (Token token:tokenList){
                if (token.getTokenKind().no!=28){
                    System.out.format("%s\t\t%d\n",token.getSymbol(),token.getTokenKind().no);
                }

            }*/

            //语法分析器 语义分析和语法分析同时完成
            Parser parser = new Parser(tokenList,quaternaryList);
            parser.parse();
            System.out.println("语法正确");
            for (Quaternary q :
                    quaternaryList) {
                System.out.println(q.toString());
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
