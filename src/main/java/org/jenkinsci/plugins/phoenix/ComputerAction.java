package org.jenkinsci.plugins.phoenix;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Computer;
import hudson.slaves.SlaveComputer;
import jenkins.model.TransientActionFactory;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Extension
public class ComputerAction extends TransientActionFactory<SuRenComputer> implements Action
{
    public SuRenComputer computer;

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
        return "surenc";
    }

    public String doTest()
    {
        System.out.println("suren test");

        return computer.getBuilds().size() + "";
    }

    public void doDelete() throws IOException, InterruptedException
    {
    }

    @Override
    public Class<SuRenComputer> type()
    {
        return SuRenComputer.class;
    }

    @Nonnull
    @Override
    public Collection<? extends Action> createFor(@Nonnull SuRenComputer computer)
    {
        try
        {
            ComputerAction instance = getClass().newInstance();
            instance.computer = this.computer;

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