package org.jenkinsci.plugins.phoenix;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Label;
import jenkins.model.Jenkins;
import jenkins.model.TransientActionFactory;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

@Extension
public class TestAction extends TransientActionFactory<WorkflowJob> implements Action
{
    public WorkflowJob workflowJob;

    @CheckForNull
    @Override
    public String getIconFileName()
    {
        return null;
    }

    @CheckForNull
    @Override
    public String getDisplayName()
    {
        return null;
    }

    @CheckForNull
    @Override
    public String getUrlName()
    {
        return "suren";
    }

    public String doTest()
    {
        System.out.println("suren test");

        System.out.println(workflowJob.getBuildDir());

        System.out.println(Jenkins.getInstance().getLabels());

        Iterator<Label> it = Jenkins.getInstance().getLabels().iterator();
        while(it.hasNext()) {
            System.out.println(it.next().getName());
        }

        return "suren test";
    }

    public void doDelete() throws IOException, InterruptedException
    {
        workflowJob.delete();
    }

    public Set<Label> doHello()
    {
        return Jenkins.getInstance().getLabels();
    }

    @Override
    public Class<WorkflowJob> type()
    {
        return WorkflowJob.class;
    }

    @Nonnull
    @Override
    public Collection<? extends Action> createFor(@Nonnull WorkflowJob workflowJob)
    {
        try
        {
            TestAction instance = getClass().newInstance();
            instance.workflowJob = workflowJob;

            return Collections.singleton(instance);
        } catch (InstantiationException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}