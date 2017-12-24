package org.jenkinsci.plugins.phoenix.steps;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import org.apache.commons.net.ftp.FTPClient;
import org.jenkinsci.plugins.durabletask.Controller;
import org.jenkinsci.plugins.durabletask.DurableTask;

import java.io.*;

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

        return new DurableController();
    }

    @Override
    public void captureOutput() throws UnsupportedOperationException
    {
    }

    private void publish() throws IOException
    {
        FTPClient client = new FTPClient();
        client.connect(ftpStep.getServerHost(), ftpStep.getServerPort());
        client.login(ftpStep.getUsername(), ftpStep.getPassword());
        client.enterLocalPassiveMode();

        client.changeWorkingDirectory(ftpStep.getTargetDir());
        client.setFileType(FTPClient.BINARY_FILE_TYPE);

        File srcFile = ftpStep.getSrcFile();

        try(InputStream fis = new FileInputStream(srcFile))
        {
            client.storeFile(srcFile.getName(), fis);
        }
    }
}