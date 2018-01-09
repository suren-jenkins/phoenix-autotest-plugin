package com.surenpi.jenkins.phoenix;

import hudson.Extension;
import hudson.model.Slave;
import hudson.slaves.SlaveComputer;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class SuRenComputer extends SlaveComputer
{
    public SuRenComputer(Slave slave)
    {
        super(slave);
    }

    @Override
    public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp)
    {
        System.out.println(token + "=suren");
        return super.getDynamic(token, req, rsp);
    }
}