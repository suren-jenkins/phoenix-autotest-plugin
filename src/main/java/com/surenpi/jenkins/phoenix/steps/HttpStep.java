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
 * @author suren
 */
public class HttpStep extends DurableStep implements Serializable
{
    private static final Logger LOGGER = Logger.getLogger(HttpStep.class.getName());

    private final String url;
    private String method;

    @DataBoundConstructor
    public HttpStep(String url)
    {
        this.url = url;
    }

    @Override
    protected DurableTask task()
    {
        return new DurableHttpTask(this);
    }

    @Override
    public StepExecution start(StepContext context) throws Exception
    {
        return new DurableExecution(context, this);
    }

    @Extension
    public static final class DescriptorImpl extends DurableTaskStepDescriptor
    {
        @Override
        public String getFunctionName()
        {
            return "http";
        }

        @Override
        public String getDisplayName()
        {
            return "http tool";
        }
    }

    public String getUrl()
    {
        return url;
    }

    public String getMethod()
    {
        return method;
    }

    @DataBoundSetter
    public void setMethod(String method)
    {
        this.method = method;
    }
}