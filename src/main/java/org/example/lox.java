package org.example;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class lox {
    static  boolean hadError = false;
    public static void main(String[] args) throws IOException{
        if (args.length >1){
            System.out.println("usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        }
        else {
            runPrompt();
        }
    }
    private static void runFile (String path) throws  IOException{
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if(hadError == true) System.exit(64);
    }

    private static void runPrompt() throws IOException{
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        for(;;){
            System.out.println(">");
            String line = reader.readLine();
            if(line==null) break;
            run(line);
            hadError=false;
        }
    }
    private static void run(String src){
        Scanner scanner = new Scanner(src);
        List<Token> tokens = scanner.scanTokens();

        for(Token token:tokens){
            System.out.println(token);
        }
    }
    static void error(int line,String mensaje){
        report(line,"",mensaje);
    }
    private static void report(int line , String donde, String mesg){
        System.err.println("[line" + line +"]"+ "Error" + donde + ":" + mesg);
        hadError = true;
    }
}