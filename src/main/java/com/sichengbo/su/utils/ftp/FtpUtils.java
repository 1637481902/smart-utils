package com.sichengbo.su.utils.ftp;

import com.jcraft.jsch.*;
import com.sichengbo.su.utils.properties.PropertiesReader;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * 进行FTP操作 需要引入jsch、commons-io
 */
public class FtpUtils {

    private static final Logger log = LoggerFactory.getLogger(FtpUtils.class);

    static Channel channel = null;
    static Session session = null;

    /**
     * 创建FTP通道
     *
     * @return ChannelSftp
     * @throws JSchException
     */
    public static ChannelSftp getChannelSftp() throws JSchException {
        // 提供服务器IP、账户、密钥
        String sftpHost = PropertiesReader.get("serverIp");
        String sftpUsername = PropertiesReader.get("serverUserName");
        String path = Thread.currentThread().getContextClassLoader().getResource("prvkey").getPath();
        String prvkeySource = PropertiesReader.get("prvkeySource");
        String prvkey = path + "/sbsc_" + prvkeySource + ".pem";
        int sftpPort = 22;

        log.info("create sftp...");

        JSch jsch = new JSch();
        jsch.addIdentity(prvkey);
        session = jsch.getSession(sftpUsername, sftpHost, sftpPort);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setTimeout(30000);
        session.connect();

        channel = session.openChannel("sftp");
        channel.connect();

        log.info("sftp connect success...");
        return (ChannelSftp) channel;
    }

    /**
     * 校验文件夹是否存在
     *
     * @param channel
     * @param path
     * @return boolean
     */
    public static boolean isExist(ChannelSftp channel, String path) {
        try {
            channel.cd(path);
            return true;
        } catch (SftpException e) {
        }
        return false;
    }

    /**
     * 删除文件夹
     *
     * @param channel
     * @param path
     */
    public static void rmdir(ChannelSftp channel, String path) {
        try {
            channel.rmdir(path);
        } catch (SftpException e) {
        }
    }

    /**
     * 创建文件夹
     *
     * @param channel
     * @param path
     */
    public static void mkdir(ChannelSftp channel, String path) {
        try {
            channel.mkdir(path);
        } catch (SftpException e) {
        }
    }

    /**
     * 上传文件
     *
     * @param channel
     * @param srcFile
     * @param destFile
     */
    public static void fileUpload(ChannelSftp channel, String srcFile, String destFile) {
        /*
         * JSch支持以下三种文件传输模式：
         * OVERWRITE是完全覆盖模式，JSch的默认文件传输模式。即在对象文件已经存在情况下，传送的文件完全覆盖对象文件生成新文件。
         * RESUME恢复模式是在文件的一部分传送完成，由于网络或其他原因文件传送被中断的情况下，在下次传送了相同的文件的情况下，从上次中断的位置恢复传输时间
         * APPEND添加模式，如果目标文件已存在，则传输的文件将是目标文件添加到文件之后。
         */
        try {
            OutputStream out = channel.put(destFile, ChannelSftp.OVERWRITE);
            byte[] buff = new byte[1024 * 256];
            int read;
            if (out != null) {
                InputStream is = new FileInputStream(srcFile);
                do {
                    read = is.read(buff, 0, buff.length);
                    if (read > 0) {
                        out.write(buff, 0, read);
                    }
                    out.flush();
                } while (read >= 0);
                is.close();
            }
            channel.quit();
        } catch (Exception e) {
        }
    }

    /**
     * 上传文件
     *
     * @param channel
     * @param inputStream
     * @param destFile
     */
    public static void fileUpload(ChannelSftp channel, InputStream inputStream, String destFile) {
        /*
         * JSch支持以下三种文件传输模式：
         * OVERWRITE是完全覆盖模式，JSch的默认文件传输模式。即在对象文件已经存在情况下，传送的文件完全覆盖对象文件生成新文件。
         * RESUME恢复模式是在文件的一部分传送完成，由于网络或其他原因文件传送被中断的情况下，在下次传送了相同的文件的情况下，从上次中断的位置恢复传输时间
         * APPEND添加模式，如果目标文件已存在，则传输的文件将是目标文件添加到文件之后。
         */
        try {
            OutputStream out = channel.put(destFile, ChannelSftp.OVERWRITE);
            byte[] buff = new byte[1024 * 256];
            int read;
            if (out != null) {
                do {
                    read = inputStream.read(buff, 0, buff.length);
                    if (read > 0) {
                        out.write(buff, 0, read);
                    }
                    out.flush();
                } while (read >= 0);
            }
            channel.quit();
        } catch (Exception e) {
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 文件下载
     *
     * @param channel
     * @param destFile
     * @return InputStream
     */
    public static InputStream fileDownLoad(ChannelSftp channel, String destFile) {
        InputStream input = null;
        try {
            input = channel.get(destFile);
            channel.quit();
        } catch (Exception e) {
        }
        return input;
    }

    /**
     * 关闭通道
     */
    public static void closeChannel() {
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
        log.info("sftp close success...");
    }

    /**
     * 运行Linux命令
     *
     * @param comm 要执行的命令
     * @return String 执行后的结果
     */
    public static String excutComm(String comm) {
        String result = "";
        try {
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(comm);
            channelExec.setErrStream(System.err);
            channelExec.connect();
            InputStream in = channelExec.getInputStream();
            result = IOUtils.toString(in, Charset.defaultCharset());
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
