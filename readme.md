# 執行AIX上的SHELL
<pre style="color:#000000;background:#ffffff;">
public class RunShell {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public RunShell() {
        super();
    }

    /**
     * 執行AIX上的shell script
     * @param aixHost AIX主機名稱或IP
     * @param aixUser AIX登入帳號
     * @param aixPswd AIX登入密碼
     * @param prgName 要執行的shell script名稱
     */
    public void runShell(String aixHost, String aixUser, String aixPswd, String prgName) throws JSchException,
                                                                                                IOException {
        JSch jsch = new JSch();
        Session session;
        final int aixPort = 22;

        try {
            // 開啟一個連線到遠端SSH伺服器的Session
            // 設定遠端主機的使用者名稱、IP及SSH port
            session = jsch.getSession(aixUser, aixHost, aixPort);
            LOGGER.info("Creating SSH Session using Username:" + aixUser + " Server :" + aixHost + " at PORT:22");
            // 當我們第一次SSH到遠端主機或是遠端主機的金鑰有變動時，我們會被提示確認遠端主機的真實性。
            // 這個檢查功能由StrictHostKeyChecking ssh參數控制。
            // 預設StrictHostKeyChecking設為yes，作為一個安全措施。
            session.setConfig("StrictHostKeyChecking", "no");
            // 設定密碼
            session.setPassword(aixPswd);
            session.connect();
            LOGGER.info("Session connected to server");

            // 建立遠端命令執行通道
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            // 設定要在channel上執行的指令並執行
            channelExec.setCommand("sh " + prgName);
            channelExec.setInputStream(null);
            channelExec.setErrStream(System.err);
            // 開啟通道並執行遠端命令
            channelExec.connect();

            // 讀取遠端命令的輸出結果
            InputStream in = channelExec.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 讀取遠端命令的錯誤輸出
            InputStream errorStream = channelExec.getErrStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            while ((line = errorReader.readLine()) != null) {
                System.out.println("Error: " + line);
            }

            // 等待遠端命令執行完成
            while (!channelExec.isClosed()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 獲取遠端命令的退出狀態
            int exitStatus = channelExec.getExitStatus();
            if (exitStatus > 0) {
                System.out.println("Remote script exec error! " + exitStatus);
                throw new RuntimeException("遠端Script檔案不存在");
            }
            // 關閉通道
            channelExec.disconnect();
            // 中斷 SSH 連線
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
            throw new JSchException("Remote Script was not found!");
        }
    }
}
</pre>
## 執行程式
<pre style="color:#000000;background:#ffffff;">>
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
</pre>