package org.jenkinsci.plugins.phoenix.steps;

import org.jenkinsci.plugins.durabletask.DurableTask;
import org.jenkinsci.plugins.workflow.steps.Step;

/**
 * @author suren
 */
public abstract class DurableStep extends Step
{
    abstract DurableTask task();
}