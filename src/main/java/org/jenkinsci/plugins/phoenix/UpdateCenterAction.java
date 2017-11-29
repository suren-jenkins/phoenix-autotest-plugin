package org.jenkinsci.plugins.phoenix;

import hudson.Extension;
import hudson.LocalPluginManager;
import hudson.PluginManager;
import hudson.model.Action;
import jenkins.model.TransientActionFactory;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

@Extension
public class UpdateCenterAction extends TransientActionFactory<LocalPluginManager> implements Action
{
    private LocalPluginManager target;

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
        return "surenUpdate";
    }

    public void doTest()
    {
        System.out.println(target + "==");
    }

    @Override
    public Class<LocalPluginManager> type()
    {
        return LocalPluginManager.class;
    }

    @Nonnull
    @Override
    public Collection<? extends Action> createFor(@Nonnull LocalPluginManager target)
    {
        try
        {
            UpdateCenterAction instance = getClass().newInstance();
            instance.target = target;

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