package com.surenpi.jenkins.phoenix.steps;

import hudson.Extension;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Set;

public class TriggerStep extends Step
{
    private String text;

    @DataBoundConstructor
    public TriggerStep(){}

    public String getText()
    {
        return text;
    }

    @DataBoundSetter
    public void setText(String text)
    {
        this.text = text;
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception
    {
        return new Execution(this, stepContext);
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
            return "suren";
        }

        @Nonnull
        @Override
        public String getDisplayName()
        {
            return "suren display";
        }
    }

    public static class Execution extends AbstractStepExecutionImpl
    {
        private StepContext stepContext;
        private TriggerStep triggerStep;

        public Execution(TriggerStep triggerStep, StepContext stepContext)
        {
            this.triggerStep = triggerStep;
            this.stepContext = stepContext;
        }

        @Override
        public boolean start() throws Exception
        {
            System.out.println("trigger start");
            System.out.println(this.stepContext.hasBody());

            TaskListener listener = this.stepContext.get(TaskListener.class);

            PrintStream logger = listener.getLogger();
            logger.println("phoenix logger");
            logger.println(triggerStep.getText());
            stepContext.onSuccess("success result");

            return true;
        }

        @Override
        public void stop(@Nonnull Throwable throwable) throws Exception
        {
            System.out.println("trigger stop");
        }
    }
}