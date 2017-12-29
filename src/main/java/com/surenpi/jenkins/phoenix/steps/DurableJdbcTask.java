package com.surenpi.jenkins.phoenix.steps;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.durabletask.Controller;
import org.jenkinsci.plugins.durabletask.DurableTask;

import javax.annotation.CheckForNull;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author suren
 */
public class DurableJdbcTask extends DurableTask implements Serializable
{
    private final String url;
    private final String user;
    private final String password;
    private final String sql;

    public DurableJdbcTask(String url, String user, String password, String sql)
    {
        this.url = url;
        this.user = user;
        this.password = password;
        this.sql = sql;
    }

    @Override
    public Controller launch(EnvVars env, FilePath workspace, Launcher launcher, TaskListener listener) throws IOException, InterruptedException
    {
        PrintStream logger = listener.getLogger();

        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try(Connection conn = DriverManager.getConnection(url, user, password))
        {
            Statement stage = conn.createStatement();

            stage.execute(sql);
        }
        catch (Exception ex)
        {
            logger.println(ex.getLocalizedMessage());
        }

        return new JdbcController();
    }

    @Override
    public void captureOutput() throws UnsupportedOperationException
    {
        System.out.println("sdfds");
    }

    class JdbcController extends Controller
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
}