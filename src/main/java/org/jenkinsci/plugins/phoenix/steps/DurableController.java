package org.jenkinsci.plugins.phoenix.steps;

import hudson.FilePath;
import hudson.Launcher;
import org.jenkinsci.plugins.durabletask.Controller;

import javax.annotation.CheckForNull;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author suren
 */
public class DurableController extends Controller
{
    @Override
    public boolean writeLog(FilePath workspace, OutputStream sink) throws IOException, InterruptedException
    {
        return true;
    }

    @Override
    public void cleanup(FilePath workspace) throws IOException, InterruptedException
    {

    }

    @CheckForNull
    @Override
    public Integer exitStatus(FilePath workspace, Launcher launcher) throws IOException, InterruptedException
    {
        return 0;
    }
}