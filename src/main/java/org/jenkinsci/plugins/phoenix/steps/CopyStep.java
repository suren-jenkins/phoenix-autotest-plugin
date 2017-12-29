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
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

/**
 * Copy a file from master to slaver.
 * @author suren
 */
public class CopyStep extends Builder implements SimpleBuildStep
{
    private String srcFile;
    private String dstFile;
    private boolean keepMeta = false;
    private boolean recursive = false;

    @DataBoundConstructor
    public CopyStep(){}

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace,
                        @Nonnull Launcher launcher, @Nonnull TaskListener listener)
            throws InterruptedException, IOException
    {
        Jenkins jenkins = Jenkins.getInstance();
        if(run instanceof WorkflowRun)
        {
            WorkflowRun wf = (WorkflowRun) run;
            WorkflowJob job = wf.getParent();
            File jobBuildDir = jenkins.getBuildDirFor(job);

            int number = wf.getNumber();

            File sourceFile = new File(jobBuildDir, String.format("%d/%s", number, srcFile));

            FilePath sourcePath = new FilePath(sourceFile);
            FilePath targetPath = new FilePath(workspace, dstFile);

            if(keepMeta)
            {
                sourcePath.copyToWithPermission(targetPath);
            }
            else if(recursive)
            {
                sourcePath.copyRecursiveTo(targetPath);
            }
            else
            {
                sourcePath.copyTo(targetPath);
            }
        }
    }

    @Extension
    @Symbol("copy")
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder>
    {
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType)
        {
            return true;
        }
    }

    public String getSrcFile()
    {
        return srcFile;
    }

    @DataBoundSetter
    public void setSrcFile(String srcFile)
    {
        this.srcFile = srcFile;
    }

    public String getDstFile()
    {
        return dstFile;
    }

    @DataBoundSetter
    public void setDstFile(String dstFile)
    {
        this.dstFile = dstFile;
    }

    public boolean isKeepMeta()
    {
        return keepMeta;
    }

    @DataBoundSetter
    public void setKeepMeta(boolean keepMeta)
    {
        this.keepMeta = keepMeta;
    }

    public boolean isRecursive()
    {
        return recursive;
    }

    @DataBoundSetter
    public void setRecursive(boolean recursive)
    {
        this.recursive = recursive;
    }
}