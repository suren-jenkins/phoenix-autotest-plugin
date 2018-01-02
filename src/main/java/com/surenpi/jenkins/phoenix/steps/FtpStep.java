package com.surenpi.jenkins.phoenix.steps;

import com.surenpi.jenkins.pipeline.step.DurableExecution;
import com.surenpi.jenkins.pipeline.step.DurableStep;
import com.surenpi.jenkins.pipeline.step.DurableTaskStepDescriptor;
import hudson.Extension;
import org.jenkinsci.plugins.durabletask.DurableTask;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * @author suren
 */
public class FtpStep extends DurableStep
{
    private String serverHost;
    private int serverPort = 21;
    private String credentialsId;

    private String srcFile;
    private String targetDir;

    @DataBoundConstructor
    public FtpStep(String serverHost, String srcFile, String targetDir)
    {
        this.serverHost = serverHost;
        this.srcFile = srcFile;
        this.targetDir = targetDir;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception
    {
        return new DurableExecution(context, this);
    }

    @Override
    public DurableTask task()
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

    public String getCredentialsId()
    {
        return credentialsId;
    }

    @DataBoundSetter
    public void setCredentialsId(String credentialsId)
    {
        this.credentialsId = credentialsId;
    }

    public String getSrcFile()
    {
        return srcFile;
    }

    @DataBoundSetter
    public void setSrcFile(String srcFile)
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