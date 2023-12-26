package org.sftp.input;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class Sftp_Host_to_Remote {
    public void pushToSftpServer(SftpConfig sftpConfig, String username, String host, int port, String password) {
        try {
            String remoteInPath = sftpConfig.getRemoteInPath();
            final String localOutpath = sftpConfig.getLocalOutPath();
            final String localOutArchivePath = sftpConfig.getLocalOutArchivePath();

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

                //upload all files from 'output' folder to remote machine's 'input' folder
                File[] _files = new File(localOutpath).listFiles();
                if (_files != null) {
                    for (File file : _files) {
                        if (!file.isDirectory()) {
                            channelSftp.put(file.getAbsolutePath(), remoteInPath + "/" + file.getName());
                        }
                    }
                }
                System.out.println("All File Uploaded successfully!");
                // Move all files from "output" to "output_archive" directory
                try {
                    Path sourcePath = Paths.get(localOutpath);
                    Path destinationPath = Paths.get(localOutArchivePath);
                    try (Stream<Path> files = Files.list(sourcePath)) {
                        files.forEach(file -> {
                            try {
                                Path destFile = destinationPath.resolve(file.getFileName());
                                Files.move(file, destFile, StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
                catch (IOException e) {
                    System.out.println("Unable to move files to 'output_archive'");
                    e.printStackTrace();
                }
                //close connections
                channelSftp.disconnect();
                session.disconnect();
            }
            catch (Exception e) {
                System.out.println("Could not able to Upload the file");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Unable to set config details");
            e.printStackTrace();
        }
    }
}
