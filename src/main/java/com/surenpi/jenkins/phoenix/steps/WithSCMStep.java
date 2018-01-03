package com.surenpi.jenkins.phoenix.steps;

import com.google.common.collect.ImmutableSet;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.scm.SCM;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.Set;

/**
 * This step allow user get config or script files from scm(git,svn).
 * @author suren
 */
public class WithSCMStep extends Step
{
    private SCM scm;
    private String repo;
    private String credentialId;

    @DataBoundConstructor
    public WithSCMStep(SCM scm)
    {
        this.scm = scm;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception
    {
        return new WithSCMExecution(context, this);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor
    {

        @Override
        public Set<? extends Class<?>> getRequiredContext()
        {
            return ImmutableSet.of(TaskListener.class, FilePath.class, Launcher.class, EnvVars.class, Run.class);
        }

        @Override
        public String getFunctionName()
        {
            return "withSCM";
        }
    }

    public SCM getScm()
    {
        return scm;
    }

    @DataBoundSetter
    public void setScm(SCM scm)
    {
        this.scm = scm;
    }

    public String getRepo()
    {
        return repo;
    }

    @DataBoundSetter
    public void setRepo(String repo)
    {
        this.repo = repo;
    }

    public String getCredentialId()
    {
        return credentialId;
    }

    @DataBoundSetter
    public void setCredentialId(String credentialId)
    {
        this.credentialId = credentialId;
    }
}