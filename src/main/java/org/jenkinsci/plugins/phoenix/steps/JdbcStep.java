package org.jenkinsci.plugins.phoenix.steps;

import com.surenpi.jenkins.pipeline.step.DurableExecution;
import com.surenpi.jenkins.pipeline.step.DurableStep;
import com.surenpi.jenkins.pipeline.step.DurableTaskStepDescriptor;
import hudson.Extension;
import org.jenkinsci.plugins.durabletask.DurableTask;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.logging.Logger;

/**
 * Execute sql script through jdbc.
 * @author suren
 */
public class JdbcStep extends DurableStep
{
    private static final Logger LOGGER = Logger.getLogger(JdbcStep.class.getName());

    private String url;
    private String user;
    private String password;
    private String sql;

    @DataBoundConstructor
    public JdbcStep(){}

    @Override
    public StepExecution start(StepContext context) throws Exception
    {
        return new DurableExecution(context, this);
    }

    @Override
    public DurableTask task()
    {
        return new DurableJdbcTask(url, user, password, sql);
    }

    @Extension
    public static final class DescriptorImpl extends DurableTaskStepDescriptor
    {
        @Override
        public String getFunctionName()
        {
            return "phoenixSql";
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

    public String getUser()
    {
        return user;
    }

    @DataBoundSetter
    public void setUser(String user)
    {
        this.user = user;
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

    public String getSql()
    {
        return sql;
    }

    @DataBoundSetter
    public void setSql(String sql)
    {
        this.sql = sql;
    }
}