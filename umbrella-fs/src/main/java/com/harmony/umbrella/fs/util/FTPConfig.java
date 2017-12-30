package com.harmony.umbrella.fs.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;

/**
 * @author wuxii@foxmail.com
 */
public class FTPConfig {

    protected FTPClientConfig clientConfig;

    public FTPClient newClient() {
        // FTPClient ftp = new FTPClient();
        // FTPClientConfig config = new FTPClientConfig();
        // config.setXXX(YYY); // change required options
        // // for example config.setServerTimeZoneId("Pacific/Pitcairn")
        // ftp.configure(config );
        // boolean error = false;
        // try {
        // int reply;
        // String server = "ftp.example.com";
        // ftp.connect(server);
        // System.out.println("Connected to " + server + ".");
        // System.out.print(ftp.getReplyString());
        //
        // // After connection attempt, you should check the reply code to
        // verify
        // // success.
        // reply = ftp.getReplyCode();
        //
        // if(!FTPReply.isPositiveCompletion(reply)) {
        // ftp.disconnect();
        // System.err.println("FTP server refused connection.");
        // System.exit(1);
        // }
        // ... // transfer files
        // ftp.logout();
        // } catch(IOException e) {
        // error = true;
        // e.printStackTrace();
        // } finally {
        // if(ftp.isConnected()) {
        // try {
        // ftp.disconnect();
        // } catch(IOException ioe) {
        // // do nothing
        // }
        // }
        // System.exit(error ? 1 : 0);
        // }
        return null;
    }

}
