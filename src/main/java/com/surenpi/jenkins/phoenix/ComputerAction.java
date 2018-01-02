package com.surenpi.jenkins.phoenix;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Api;
import hudson.model.Label;
import hudson.slaves.SlaveComputer;
import jenkins.model.Jenkins;
import jenkins.model.TransientActionFactory;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;

@Extension
@ExportedBean
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
        return "phoenix";
    }

    @CheckForNull
    @Override
    public String getUrlName()
    {
        return "phoenix";
    }

    public void doTest()
    {
        System.out.println("suren test");
        System.out.println(computer);
//        System.out.println(computer.getLauncher());

//        return computer.getBuilds() + "";
    }

    @Exported
    public Set<Label> getLabels()
    {
        Set<Label> labels = Jenkins.getInstance().getLabels();
        return labels;
//        Iterator<Label> it = labels.iterator();
//        Set<LabelOrigin> labelOrigins = new HashSet<>();
//        while(it.hasNext())
//        {
//            Label label = it.next();
//
//            labelOrigins.add(new LabelOrigin(label));
//        }
//
//        return labelOrigins;
    }

    public void doDelete() throws IOException, InterruptedException
    {
    }

    public Api getApi()
    {
        return new Api(this);
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
        System.out.println(computer + "===");
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