package org.jenkinsci.plugins.phoenix.steps;

import com.google.common.collect.ImmutableSet;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.kohsuke.stapler.QueryParameter;

import java.nio.charset.Charset;
import java.util.Set;

public abstract class DurableTaskStepDescriptor extends StepDescriptor
{
    public static final String defaultEncoding = "UTF-8";

    public FormValidation doCheckEncoding(@QueryParameter boolean returnStdout, @QueryParameter String encoding) {
        try {
            Charset.forName(encoding);
        } catch (Exception x) {
            return FormValidation.error(x, "Unrecognized encoding");
        }
        if (!returnStdout && !encoding.equals(JdbcStep.DescriptorImpl.defaultEncoding)) {
            return FormValidation.warning("encoding is ignored unless returnStdout is checked.");
        }
        return FormValidation.ok();
    }

    public FormValidation doCheckReturnStatus(@QueryParameter boolean returnStdout, @QueryParameter boolean returnStatus) {
        if (returnStdout && returnStatus) {
            return FormValidation.error("You may not select both returnStdout and returnStatus.");
        }
        return FormValidation.ok();
    }

    @Override public final Set<? extends Class<?>> getRequiredContext() {
        return ImmutableSet.of(FilePath.class, EnvVars.class, Launcher.class, TaskListener.class);
    }
}