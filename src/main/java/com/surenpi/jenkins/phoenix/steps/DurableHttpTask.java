package com.surenpi.jenkins.phoenix.steps;

import com.surenpi.jenkins.pipeline.step.DurableController;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jenkinsci.plugins.durabletask.Controller;
import org.jenkinsci.plugins.durabletask.DurableTask;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author suren
 */
public class DurableHttpTask extends DurableTask implements Serializable
{
    private final HttpStep httpStep;

    public DurableHttpTask(HttpStep httpStep)
    {
        this.httpStep = httpStep;
    }

    @Override
    public Controller launch(EnvVars env, FilePath workspace, Launcher launcher, TaskListener listener) throws IOException, InterruptedException
    {
        String method = httpStep.getMethod();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpUriRequest request = null;
        URI uri = null;

        try
        {
            uri = new URI(httpStep.getUrl());
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        if("get".equals(method))
        {
            request = new HttpGet(uri);
        }
        else if("post".equals(method))
        {
            request = new HttpPost(uri);
        }

        client.execute(request);

        return new DurableController();
    }
}