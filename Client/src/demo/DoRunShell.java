package demo;

import com.jcraft.jsch.JSchException;

import java.io.IOException;
import java.util.logging.Logger;

public class DoRunShell {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public DoRunShell() {
        super();
    }

    public static void main(String[] args) throws IOException, JSchException {
        if (args.length < 4) { // 若傳入的參數不等於4個，顯示訊息並離開程式
            System.out.print("<請提供aixHost,aixUser,aixPawd,prgName>");
            System.exit(1);
        }
        //        aixHost, aixUser, aixPswd, prgName
        String aixHost = args[0];
        String aixUser = args[1];
        String aixPswd = args[2];
        String prgName = args[3];
        RunShell run = new RunShell();
        run.runShell(aixHost, aixUser, aixPswd, prgName);
        //        System.out.println("執行完畢!!");
    }
}
