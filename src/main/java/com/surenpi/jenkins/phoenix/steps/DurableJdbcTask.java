package com.surenpi.jenkins.phoenix.steps;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import hudson.security.ACL;
import jenkins.model.Jenkins;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.jenkinsci.plugins.durabletask.Controller;
import org.jenkinsci.plugins.durabletask.DurableTask;

import javax.annotation.CheckForNull;
import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author suren
 */
public class DurableJdbcTask extends DurableTask implements Serializable
{
    private final JdbcStep jdbcStep;

    public DurableJdbcTask(JdbcStep jdbcStep)
    {
        this.jdbcStep = jdbcStep;
    }

    @Override
    public Controller launch(EnvVars env, FilePath workspace, Launcher launcher, TaskListener listener)
            throws IOException, InterruptedException
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

        List<UsernamePasswordCredentials> allCredentials = CredentialsProvider.lookupCredentials
                (UsernamePasswordCredentials.class, Jenkins.getInstance(), ACL.SYSTEM, new ArrayList<DomainRequirement>());

        Credentials credential = CredentialsMatchers.firstOrNull(
                allCredentials, CredentialsMatchers.withId(jdbcStep.getCredentialsId()));

        if(credential instanceof UsernamePasswordCredentials)
        {
            UsernamePasswordCredentials upCre = (UsernamePasswordCredentials) credential;

            String userName = upCre.getUsername();
            String password = upCre.getPassword().getPlainText();

            Reader reader = null;
            try(Connection conn = DriverManager.getConnection(jdbcStep.getUrl(), userName, password))
            {
                Statement stage = conn.createStatement();

                ScriptRunner runner = new ScriptRunner(conn);

                if(jdbcStep.isText())
                {
                    reader = new InputStreamReader(new ByteArrayInputStream(jdbcStep.getSql().getBytes()));
                }
                else
                {
                    reader = new FileReader(new File(jdbcStep.getSql()));
                }

                runner.runScript(reader);
            }
            catch (Exception ex)
            {
                logger.println(ex.getLocalizedMessage());
            }
            finally
            {
                if(reader != null)
                {
                    reader.close();
                }
            }
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