package com.surenpi.jenkins.phoenix.steps;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.surenpi.jenkins.pipeline.step.DurableController;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import hudson.security.ACL;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.durabletask.Controller;
import org.jenkinsci.plugins.durabletask.DurableTask;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

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
            publish(logger);
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

    public void publish(PrintStream logger) throws IOException, FtpProtocolException
    {
        FtpClient client = FtpClient.create();
        SocketAddress addr = new InetSocketAddress(
                ftpStep.getServerHost(), ftpStep.getServerPort());
        client.connect(addr);

        List<UsernamePasswordCredentials> allCredentials = CredentialsProvider.lookupCredentials
                (UsernamePasswordCredentials.class, Jenkins.getInstance(), ACL.SYSTEM, new ArrayList<DomainRequirement>());

        Credentials credential = CredentialsMatchers.firstOrNull(
                allCredentials, CredentialsMatchers.withId(ftpStep.getCredentialsId()));

        if(credential instanceof UsernamePasswordCredentials)
        {
            UsernamePasswordCredentials upCre = (UsernamePasswordCredentials) credential;

            String userName = upCre.getUsername();
            String password = upCre.getPassword().getPlainText();
            client.login(userName, password.toCharArray());

            client.changeDirectory(ftpStep.getTargetDir());

            File srcFile = new File(ftpStep.getSrcFile());
            try(InputStream fis = new FileInputStream(srcFile))
            {
                client.putFile(srcFile.getName(), fis, true);
            }
        }
        else
        {
            logger.println("only support user password credential.");
        }
    }
}