package com.surenpi.jenkins.phoenix.steps;

import com.google.common.collect.ImmutableSet;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.Set;

/**
 * Time format step
 * @author suren
 */
public class TimeStep extends Step
{
    private String format = "yyyy-MM-dd";
    /** formatted time variable name */
    private String value;

    @DataBoundConstructor
    public TimeStep(){}

    @Override
    public StepExecution start(StepContext context) throws Exception
    {
        return new TimeExecution(context, value, format);
    }

    @Extension
    public static final class DescriptorImpl extends StepDescriptor
    {

        @Override
        public Set<? extends Class<?>> getRequiredContext()
        {
            return ImmutableSet.of(TaskListener.class, FilePath.class, Launcher.class, EnvVars.class, Run.class);
        }

        @Override
        public String getFunctionName()
        {
            return "time";
        }
    }

    public String getFormat()
    {
        return format;
    }

    @DataBoundSetter
    public void setFormat(String format)
    {
        this.format = format;
    }

    public String getValue()
    {
        return value;
    }

    @DataBoundSetter
    public void setValue(String value)
    {
        this.value = value;
    }
}