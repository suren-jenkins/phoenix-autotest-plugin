package com.surenpi.jenkins.phoenix;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.SCMListener;
import hudson.scm.SCM;
import hudson.scm.SCMRevisionState;

import javax.annotation.CheckForNull;
import java.io.File;

@Extension
public class PhoenixSCMListener extends SCMListener
{
    @Override
    public void onCheckout(Run<?, ?> build, SCM scm, FilePath workspace, TaskListener listener,
                           @CheckForNull File changelogFile, @CheckForNull SCMRevisionState pollingBaseline) throws Exception
    {
        System.out.println("oncheckout");
    }
}