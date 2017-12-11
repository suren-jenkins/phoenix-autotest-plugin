package org.jenkinsci.plugins.phoenix.steps;

import hudson.Extension;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Set;

/**
 * @author suren
 */
public class DiskStep extends Step
{
    private long need;

    @DataBoundConstructor
    public DiskStep(){}

    public long getNeed()
    {
        return need;
    }

    @DataBoundSetter
    public void setNeed(long need)
    {
        this.need = need;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception
    {
        return new Execution(this, context);
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
            return "disk";
        }
    }

    public static class Execution extends AbstractStepExecutionImpl
    {
        private final DiskStep diskStep;
        private final StepContext stepContext;

        public Execution(DiskStep diskStep, StepContext stepContext)
        {
            this.diskStep = diskStep;
            this.stepContext = stepContext;
        }

        @Override
        public boolean start() throws Exception
        {
            long free = new File("/").getFreeSpace();
            System.out.println(free);
            PrintStream logger = stepContext.get(TaskListener.class).getLogger();
            logger.println("free: " + free + "; need: " + diskStep.getNeed());

            if(free > diskStep.getNeed())
            {
                stepContext.onSuccess("success");
            }
            else
            {
                stepContext.onFailure(new RuntimeException("failure"));
            }

            return true;
        }

        @Override
        public void stop(@Nonnull Throwable cause) throws Exception
        {

        }
    }
}