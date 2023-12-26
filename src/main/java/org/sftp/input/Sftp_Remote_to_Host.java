package org.sftp.input;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.util.Vector;

public class Sftp_Remote_to_Host {
    public void pullFromSftpServer(SftpConfig sftpConfig, String username, String host, int port, String password) {
        try {
            String outPath = sftpConfig.getRemoteOutPath();
            String inPath = sftpConfig.getLocalInPath();
            String outArchivePath = sftpConfig.getRemoteOutArchivePath();
            try {
                JSch jsch = new JSch();
                Session session = jsch.getSession(username, host, port);
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setConfig("PreferredAuthentications", "password");
                session.connect();
                System.out.println("Connected to Server Machine");
                ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
                channelSftp.connect();

                @SuppressWarnings("unchecked")
                Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(outPath);
                for (ChannelSftp.LsEntry entry : fileList) {
                    String filename = entry.getFilename();
                    if (!entry.getAttrs().isDir()) {
                        //push file from "output" folder to local folder "input"
                        channelSftp.get(outPath + "/" + filename, inPath + "/" + filename);
                        //move file to "output_archive" folder from "output folder"
                        channelSftp.rename(outPath + "/" + filename, outArchivePath + "/" + filename);
                    }
                }
                System.out.println("All File downloaded successfully!");

                //close connections
                channelSftp.disconnect();
                session.disconnect();
            }
            catch (Exception e) {
                System.out.println("Unable to download the files");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Unable to set Session");
            e.printStackTrace();
        }
    }
}
