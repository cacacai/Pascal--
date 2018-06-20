package pascal.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Test {
    public static void main(String[] args){
        String text="";
        String fileName = "E:/IJworkspace/compile-experiment-/code.txt";
        Scanner in= null;
        try {
            in = new Scanner(new File(fileName));
            while (in.hasNextLine()){
                text+=in.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(text);

    }
}
