package org.sftp.input;

public class SftpFileDownload {

    public static void main(String[] args) {
        try {
            SftpConfig sftpConfig = new SftpConfig().readConfigurations();
            String host = sftpConfig.getHost();
            int port = sftpConfig.getPort();
            String username = sftpConfig.getUsername();
            String password = sftpConfig.getPassword();

            Sftp_Remote_to_Host remoteToHost = new Sftp_Remote_to_Host();
            Sftp_Host_to_Remote hostToRemote = new Sftp_Host_to_Remote();

            remoteToHost.pullFromSftpServer(sftpConfig, username, host, port, password);
            hostToRemote.pushToSftpServer(sftpConfig, username, host, port, password);
        }
        catch (Exception e) {
            System.out.println("Unable to fetch Config details");
            e.printStackTrace();
        }
    }

}
