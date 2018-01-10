package com.surenpi.jenkins.phoenix.steps;

import hudson.EnvVars;
import org.jenkinsci.plugins.workflow.steps.AbstractStepExecutionImpl;
import org.jenkinsci.plugins.workflow.steps.BodyExecutionCallback;
import org.jenkinsci.plugins.workflow.steps.EnvironmentExpander;
import org.jenkinsci.plugins.workflow.steps.StepContext;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author suren
 */
public class TimeExecution extends AbstractStepExecutionImpl
{
    private final String var;
    private final String format;

    public TimeExecution(StepContext context, String var, String format)
    {
        super(context);
        this.var = var;
        this.format = format;
    }

    @Override
    public boolean start() throws Exception
    {
        EnvVars envOverride = new EnvVars();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        String timeStr = dateFormat.format(new Date());
        envOverride.put(var, timeStr);

        getContext().newBodyInvoker().withContext(envOverride).withCallback(new Callback()).start();

        return false;
    }

    private static final class ExpanderImpl extends EnvironmentExpander {
        private static final long serialVersionUID = 1;
        private final Map<String, String> overrides;

        private ExpanderImpl(EnvVars overrides) {
            this.overrides = new HashMap<>();
            for (Map.Entry<String, String> entry : overrides.entrySet()) {
                this.overrides.put(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public void expand(EnvVars env) throws IOException, InterruptedException {
            env.overrideAll(overrides);
        }
    }

    public final class Callback extends BodyExecutionCallback.TailCall
    {
        @Override
        protected void finished(StepContext context) throws Exception
        {
        }
    }
}