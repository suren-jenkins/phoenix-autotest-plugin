package org.jenkinsci.plugins.phoenix.steps;

import com.surenpi.jenkins.pipeline.step.DurableController;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.durabletask.Controller;
import org.jenkinsci.plugins.durabletask.DurableTask;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author suren
 */
public class DurableFtpTask extends DurableTask implements Serializable
{
    private FtpStep ftpStep;

    public DurableFtpTask(FtpStep ftpStep)
    {
        this.ftpStep = ftpStep;
    }

    @Override
    public Controller launch(EnvVars env, FilePath workspace, Launcher launcher, TaskListener listener) throws IOException, InterruptedException
    {
        PrintStream logger = listener.getLogger();

        try
        {
            publish();
        }
        catch (IOException e)
        {
            logger.println(e.getMessage());
        }
        catch (FtpProtocolException e)
        {
            logger.println(e.getMessage());
        }

        return new DurableController();
    }

    @Override
    public void captureOutput() throws UnsupportedOperationException
    {
    }

    public void publish() throws IOException, FtpProtocolException
    {
        FtpClient client = FtpClient.create();
        SocketAddress addr = new InetSocketAddress(
                ftpStep.getServerHost(), ftpStep.getServerPort());
        client.connect(addr);
        client.login(ftpStep.getUsername(), ftpStep.getPassword().toCharArray());

        client.changeDirectory(ftpStep.getTargetDir());

        File srcFile = new File(ftpStep.getSrcFile());
        try(InputStream fis = new FileInputStream(srcFile))
        {
            client.putFile(srcFile.getName(), fis, true);
        }
    }
}