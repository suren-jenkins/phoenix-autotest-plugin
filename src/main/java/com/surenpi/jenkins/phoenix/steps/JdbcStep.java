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

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Execute sql script through jdbc.
 * @author suren
 */
public class JdbcStep extends DurableStep implements Serializable
{
    private static final Logger LOGGER = Logger.getLogger(JdbcStep.class.getName());

    private String url;
    private String credentialsId;
    private String sql;

    private boolean isText = false;

    @DataBoundConstructor
    public JdbcStep(String url, String credentialsId)
    {
        this.url = url;
        this.credentialsId = credentialsId;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception
    {
        return new DurableExecution(context, this);
    }

    @Override
    public DurableTask task()
    {
        return new DurableJdbcTask(this);
    }

    @Extension
    public static final class DescriptorImpl extends DurableTaskStepDescriptor
    {
        @Override
        public String getFunctionName()
        {
            return "jdbc";
        }

        @Override
        public String getDisplayName()
        {
            return "Execute sql script through jdbc.";
        }
    }

    public String getUrl()
    {
        return url;
    }

    @DataBoundSetter
    public void setUrl(String url)
    {
        this.url = url;
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

    public String getSql()
    {
        return sql;
    }

    @DataBoundSetter
    public void setSql(String sql)
    {
        this.sql = sql;
    }

    public boolean isText()
    {
        return isText;
    }

    @DataBoundSetter
    public void setText(boolean text)
    {
        isText = text;
    }
}