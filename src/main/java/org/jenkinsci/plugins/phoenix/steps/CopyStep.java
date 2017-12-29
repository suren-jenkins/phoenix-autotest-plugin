package org.jenkinsci.plugins.phoenix.steps;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import hudson.tasks.Builder;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;

public class CopyStep extends Builder implements SimpleBuildStep
{
    @DataBoundConstructor
    public CopyStep(){}

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace,
                        @Nonnull Launcher launcher, @Nonnull TaskListener listener)
            throws InterruptedException, IOException
    {
        Jenkins jenkins = Jenkins.getInstance();

        if (run instanceof AbstractBuild) {
            AbstractBuild build = (AbstractBuild) run;
            AbstractProject pro = ((AbstractBuild) run).getProject();

            FilePath ws = build.getWorkspace();

            FilePath ps = build.getProject().getScm().getModuleRoot(ws, build);

            System.out.println(ws.toURI());
            System.out.println(ps.toURI());
        }
    }

    @Extension
    @Symbol("copySuRen")
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder>
    {
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType)
        {
            return true;
        }
    }
}