package org.jenkinsci.plugins.phoenix.steps;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import hudson.util.DaemonThreadFactory;
import hudson.util.LogTaskListener;
import hudson.util.NamingThreadFactory;
import jenkins.util.Timer;
import org.jenkinsci.plugins.durabletask.Controller;
import org.jenkinsci.plugins.durabletask.DurableTask;
import org.jenkinsci.plugins.workflow.FilePathUtils;
import org.jenkinsci.plugins.workflow.steps.AbstractStepExecutionImpl;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.support.concurrent.Timeout;
import org.jenkinsci.plugins.workflow.support.concurrent.WithThreadName;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Execution extends AbstractStepExecutionImpl implements Runnable
{
    private static final Logger LOGGER = Logger.getLogger(Execution.class.getName());

    private static final long MIN_RECURRENCE_PERIOD = 250; // ¼s
    private static final long MAX_RECURRENCE_PERIOD = 15000; // 15s
    private static final float RECURRENCE_PERIOD_BACKOFF = 1.2f;

    private static final ScheduledThreadPoolExecutor THREAD_POOL = new ScheduledThreadPoolExecutor(25, new NamingThreadFactory(new DaemonThreadFactory(), JdbcStep.class.getName()));
    static {
        THREAD_POOL.setKeepAliveTime(1, TimeUnit.MINUTES);
        THREAD_POOL.allowCoreThreadTimeOut(true);
    }

    private transient final DurableStep step;
    private transient FilePath ws;
    private transient long recurrencePeriod;
    private transient volatile ScheduledFuture<?> task, stopTask;
    private transient boolean printedCannotContactMessage;
    private Controller controller;
    private String node;
    private String remote;
    private boolean returnStdout; // serialized default is false
    private String encoding; // serialized default is irrelevant
    private boolean returnStatus; // serialized default is false

    Execution(StepContext context, DurableStep step) {
        super(context);
        this.step = step;
    }

    @Override
    public boolean start() throws Exception {
        returnStdout = false;//step.returnStdout;
        encoding = "utf-8";//step.encoding;
        returnStatus = false;//step.returnStatus;
        StepContext context = getContext();
        ws = context.get(FilePath.class);
        node = FilePathUtils.getNodeName(ws);
        DurableTask durableTask = step.task();
        if (returnStdout) {
            durableTask.captureOutput();
        }
        controller = durableTask.launch(context.get(EnvVars.class), ws,
                context.get(Launcher.class), context.get(TaskListener.class));
        this.remote = ws.getRemote();
        setupTimer();
        return false;
    }

    private @CheckForNull
    FilePath getWorkspace() throws AbortException
    {
        if (ws == null) {
            ws = FilePathUtils.find(node, remote);
            if (ws == null) {
                LOGGER.log(Level.FINE, "Jenkins is not running, no such node {0}, or it is offline", node);
                return null;
            }
        }

        try
        {
            if(!ws.exists())
            {
                ws.mkdirs();
            }

            System.out.println(ws.getBaseName() + "==" + ws.getName() + "===" + ws.getRemote() + "==" + ws.isDirectory());
            LOGGER.log(Level.FINE, ws.getBaseName() + "==" + ws.getName() + "===" + ws.getRemote() + "==" + ws.isDirectory());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        boolean directory = true;
        try (Timeout timeout = Timeout.limit(10, TimeUnit.SECONDS)) {
            directory = ws.isDirectory();
        } catch (Exception x) {
            // RequestAbortedException, ChannelClosedException, EOFException, wrappers thereof; InterruptedException if it just takes too long.
            LOGGER.log(Level.FINE, node + " is evidently offline now", x);
            ws = null;
            if (!printedCannotContactMessage) {
                logger().println("Cannot contact " + node + ": " + x);
                printedCannotContactMessage = true;
            }
            return null;
        }
        if (!directory) {
            throw new AbortException("missing workspace " + remote + " on " + node);
        }
        return ws;
    }

    @Nonnull
    private PrintStream logger() {
        TaskListener l;
        StepContext context = getContext();
        try {
            l = context.get(TaskListener.class);
            if (l != null) {
                LOGGER.log(Level.FINEST, "JENKINS-34021: DurableTaskStep.Execution.listener present in {0}", context);
            } else {
                LOGGER.log(Level.WARNING, "JENKINS-34021: TaskListener not available upon request in {0}", context);
                l = new LogTaskListener(LOGGER, Level.FINE);
            }
        } catch (Exception x) {
            LOGGER.log(Level.FINE, "JENKINS-34021: could not get TaskListener in " + context, x);
            l = new LogTaskListener(LOGGER, Level.FINE);
            recurrencePeriod = 0;
        }
        return l.getLogger();
    }

    private @Nonnull Launcher launcher() throws IOException, InterruptedException {
        StepContext context = getContext();
        Launcher l = context.get(Launcher.class);
        if (l == null) {
            throw new IOException("JENKINS-37486: Launcher not present in " + context);
        }
        return l;
    }

    @Override
    public void stop(final Throwable cause) throws Exception {
        FilePath workspace = getWorkspace();
        if (workspace != null) {
            logger().println("Sending interrupt signal to process");
            LOGGER.log(Level.FINE, "stopping process", cause);
            stopTask = Timer.get().schedule(new Runnable() {
                @Override public void run() {
                    stopTask = null;
                    if (recurrencePeriod > 0) {
                        recurrencePeriod = 0;
                        logger().println("After 10s process did not stop");
                        getContext().onFailure(cause);
                    }
                }
            }, 10, TimeUnit.SECONDS);
            controller.stop(workspace, launcher());
        } else {
            logger().println("Could not connect to " + node + " to send interrupt signal to process");
            recurrencePeriod = 0;
            super.stop(cause);
        }
    }

    @Override
    public String getStatus() {
        StringBuilder b = new StringBuilder();
        try (Timeout timeout = Timeout.limit(2, TimeUnit.SECONDS)) { // CpsThreadDump applies a 3s timeout anyway
            FilePath workspace = getWorkspace();
            if (workspace != null) {
                b.append(controller.getDiagnostics(workspace, launcher()));
            } else {
                b.append("waiting to reconnect to ").append(remote).append(" on ").append(node);
            }
        } catch (IOException | InterruptedException x) {
            b.append("failed to look up workspace ").append(remote).append(" on ").append(node).append(": ").append(x);
        }
        b.append("; recurrence period: ").append(recurrencePeriod).append("ms");
        ScheduledFuture<?> t = task;
        if (t != null) {
            b.append("; check task scheduled; cancelled? ").append(t.isCancelled()).append(" done? ").append(t.isDone());
        }
        t = stopTask;
        if (t != null) {
            b.append("; stop task scheduled; cancelled? ").append(t.isCancelled()).append(" done? ").append(t.isDone());
        }
        return b.toString();
    }

    /** Checks for progress or completion of the external task. */
    @Override
    public void run() {
        task = null;
        try (WithThreadName naming = new WithThreadName(": checking " + remote + " on " + node)) {
            check();
        } catch (Exception x) { // TODO use ErrorLoggingScheduledThreadPoolExecutor from core if it becomes public
            LOGGER.log(Level.WARNING, null, x);
        } finally {
            if (recurrencePeriod > 0) {
                task = THREAD_POOL.schedule(this, recurrencePeriod, TimeUnit.MILLISECONDS);
            }
        }
    }

    @SuppressFBWarnings(value="REC_CATCH_EXCEPTION", justification="silly rule")
    private void check() {
        if (recurrencePeriod == 0) { // from stop
            return;
        }
        final FilePath workspace;
        try {
            workspace = getWorkspace();
        } catch (AbortException x) {
            recurrencePeriod = 0;
            getContext().onFailure(x);
            return;
        }
        if (workspace == null) {
            return; // slave not yet ready, wait for another day
        }
        try (Timeout timeout = Timeout.limit(10, TimeUnit.SECONDS)) {
            if (controller.writeLog(workspace, logger())) {
                getContext().saveState();
                recurrencePeriod = MIN_RECURRENCE_PERIOD; // got output, maybe we will get more soon
            } else {
                recurrencePeriod = Math.min((long) (recurrencePeriod * RECURRENCE_PERIOD_BACKOFF), MAX_RECURRENCE_PERIOD);
            }
            Integer exitCode = controller.exitStatus(workspace, launcher());
            if (exitCode == null) {
                LOGGER.log(Level.FINE, "still running in {0} on {1}", new Object[] {remote, node});
            } else {
                if (controller.writeLog(workspace, logger())) {
                    LOGGER.log(Level.FINE, "last-minute output in {0} on {1}", new Object[] {remote, node});
                }
                if (returnStatus || exitCode == 0) {
                    getContext().onSuccess(returnStatus ? exitCode : returnStdout ? new String(controller.getOutput(workspace, launcher()), encoding) : null);
                } else {
                    if (returnStdout) {
                        logger().write(controller.getOutput(workspace, launcher())); // diagnostic
                    }
                    getContext().onFailure(new AbortException("script returned exit code " + exitCode));
                }
                recurrencePeriod = 0;
                controller.cleanup(workspace);
            }
        } catch (Exception x) {
            LOGGER.log(Level.FINE, "could not check " + workspace, x);
            ws = null;
            if (!printedCannotContactMessage) {
                logger().println("Cannot contact " + node + ": " + x);
                printedCannotContactMessage = true;
            }
        }
    }

    @Override
    public void onResume() {
        setupTimer();
    }

    private void setupTimer() {
        recurrencePeriod = MIN_RECURRENCE_PERIOD;
        task = THREAD_POOL.schedule(this, recurrencePeriod, TimeUnit.MILLISECONDS);
    }

    private static final long serialVersionUID = 1L;
}