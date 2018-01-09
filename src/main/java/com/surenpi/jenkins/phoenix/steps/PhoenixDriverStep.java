package com.surenpi.jenkins.phoenix.steps;

import hudson.Extension;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

public class PhoenixDriverStep extends Step
{
    @DataBoundConstructor
    public PhoenixDriverStep(){}

    private String url;

    public String getUrl()
    {
        return url;
    }

    @DataBoundSetter
    public void setUrl(String url)
    {
        this.url = url;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception
    {
        return new PhoenixDriverExecution(this, context);
    }

    @Extension
    public static final class DescriptorImpl extends StepDescriptor
    {

        @Override
        public Set<? extends Class<?>> getRequiredContext()
        {
            return Collections.singleton(TaskListener.class);
        }

        @Override
        public String getFunctionName()
        {
            return "phoenix";
        }
    }

    public static final class PhoenixDriverExecution extends AbstractStepExecutionImpl
    {
        private final PhoenixDriverStep step;
        private final StepContext context;

        public PhoenixDriverExecution(PhoenixDriverStep step, StepContext context)
        {
            this.step = step;
            this.context = context;
        }

        @Override
        public boolean start() throws Exception
        {
//            Phoenix phoenix = new Phoenix();
//            phoenix.init();
//            phoenix.getEngine().getDriver().get("http://baidu.com");
            context.onSuccess("dd");
            return false;
        }

        @Override
        public void stop(@Nonnull Throwable cause) throws Exception
        {

        }
    }
}