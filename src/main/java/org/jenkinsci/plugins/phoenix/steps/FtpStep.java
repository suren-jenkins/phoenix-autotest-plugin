package org.jenkinsci.plugins.phoenix.steps;

import hudson.Extension;
import org.jenkinsci.plugins.durabletask.DurableTask;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.File;

/**
 * @author suren
 */
public class FtpStep extends DurableStep
{
    private String serverHost;
    private int serverPort;

    private String username;
    private String password;

    private File srcFile;
    private String targetDir;

    @DataBoundConstructor
    public FtpStep(){}

    @Override
    public StepExecution start(StepContext context) throws Exception
    {
        return new Execution(context, this);
    }

    @Override
    DurableTask task()
    {
        return new DurableFtpTask(this);
    }

    @Extension
    public static final class DescriptorImpl extends DurableTaskStepDescriptor
    {
        @Override
        public String getFunctionName()
        {
            return "phoenixFtp";
        }

        @Override
        public String getDisplayName()
        {
            return "ftp publish tool";
        }
    }

    public String getServerHost()
    {
        return serverHost;
    }

    @DataBoundSetter
    public void setServerHost(String serverHost)
    {
        this.serverHost = serverHost;
    }

    public int getServerPort()
    {
        return serverPort;
    }

    @DataBoundSetter
    public void setServerPort(int serverPort)
    {
        this.serverPort = serverPort;
    }

    public String getUsername()
    {
        return username;
    }

    @DataBoundSetter
    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    @DataBoundSetter
    public void setPassword(String password)
    {
        this.password = password;
    }

    public File getSrcFile()
    {
        return srcFile;
    }

    @DataBoundSetter
    public void setSrcFile(File srcFile)
    {
        this.srcFile = srcFile;
    }

    public String getTargetDir()
    {
        return targetDir;
    }

    @DataBoundSetter
    public void setTargetDir(String targetDir)
    {
        this.targetDir = targetDir;
    }
}