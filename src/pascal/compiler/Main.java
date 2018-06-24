package pascal.compiler;

import java.io.*;
import java.util.List;
import java.util.Scanner;

/**
 * Coding is pretty charming when you love it!
 *
 * 驱动Shell程序
 *
 * @author Chengzi Start
 * @date 2017/4/27
 * @time 17:38
 */
public class Main {

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
            char[] cs = new char[1024];
            int len = reader.read(cs);
            //CharReader cReader = new CharReader(cs,len);
            //TokenizerBak tokenizer = new TokenizerBak(cReader);
            TextLex textLex=new TextLex(text);

            //boolean success = tokenizer.readAllToken();
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
                }
            }
            //测试语法分析器
            Parser parser = new Parser(tokenList);
            parser.parse();
            System.out.println("size:"+tokenList.size());

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
