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
public class ComputerAction extends TransientActionFactory<SlaveComputer> implements Action
{
    public SlaveComputer computer;

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
        return "surenc";
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
        System.out.println(computer);
//        System.out.println(computer.getLauncher());

//        return computer.getBuilds() + "";
        return "";
    }

    public void doDelete() throws IOException, InterruptedException
    {
    }

    @Override
    public Class<SlaveComputer> type()
    {
        return SlaveComputer.class;
    }

    @Nonnull
    @Override
    public Collection<? extends Action> createFor(@Nonnull SlaveComputer computer)
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