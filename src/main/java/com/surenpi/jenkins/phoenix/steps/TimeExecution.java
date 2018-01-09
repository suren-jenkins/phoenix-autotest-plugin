package com.surenpi.jenkins.phoenix.steps;

import hudson.EnvVars;
import org.jenkinsci.plugins.workflow.steps.AbstractStepExecutionImpl;
import org.jenkinsci.plugins.workflow.steps.BodyExecutionCallback;
import org.jenkinsci.plugins.workflow.steps.StepContext;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author suren
 */
public class TimeExecution extends AbstractStepExecutionImpl
{
    private final String var;
    private final String format;
    private final StepContext stepContext;

    public TimeExecution(StepContext context, String var, String format)
    {
        this.stepContext = context;
        this.var = var;
        this.format = format;
    }

    @Override
    public boolean start() throws Exception
    {
        stepContext.newBodyInvoker().withCallback(new Callback()).start();

        return false;
    }

    public final class Callback extends BodyExecutionCallback.TailCall
    {
        @Override
        protected void finished(StepContext context) throws Exception
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            EnvVars env = context.get(EnvVars.class);

            String timeStr = dateFormat.format(new Date());
            env.put(var, timeStr);
        }
    }
}