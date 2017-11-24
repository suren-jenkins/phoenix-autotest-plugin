package org.jenkinsci.plugins.phoenix.steps;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

public class TriggerStep extends Step
{
    @DataBoundConstructor
    public TriggerStep(){}

    @Override
    public StepExecution start(StepContext stepContext) throws Exception
    {
        return new Execution(stepContext);
    }

    @Extension
    public static final class DescriptorImpl extends StepDescriptor
    {

        @Override
        public Set<? extends Class<?>> getRequiredContext()
        {
            return Collections.singleton(FilePath.class);
        }

        @Override
        public String getFunctionName()
        {
            return "suren";
        }
    }

    public static class Execution extends AbstractStepExecutionImpl
    {
        private StepContext stepContext;

        public Execution(StepContext stepContext)
        {
            this.stepContext = stepContext;
        }

        @Override
        public boolean start() throws Exception
        {
            System.out.println("trigger start");
            System.out.println(this.stepContext.hasBody());

            TaskListener listener = this.stepContext.get(TaskListener.class);

            listener.getLogger().println("phoenix logger");
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