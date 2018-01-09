package com.surenpi.jenkins.phoenix.steps;

import hudson.Extension;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 修改属性配置文件的流水线步骤
 * @author suren
 */
public class ModifyPropertiesStep extends Step
{
    private String file;
    private Object data;

    @DataBoundConstructor
    public ModifyPropertiesStep(String file, Object data)
    {
        if(file == null || "".equals(file.trim()))
        {
            throw new IllegalArgumentException("file parameter must be provided to modifyProperties");
        }
    }

    @Override
    public StepExecution start(StepContext context) throws Exception
    {
        return new Execution(this, context);
    }

    @Extension
    public static final class DescriptorImpl extends StepDescriptor
    {

        @Override
        public Set<? extends Class<?>> getRequiredContext()
        {
            return Collections.singleton(TaskListener.class);
        }

        @Override
        public String getFunctionName()
        {
            return "phoenixModifyProperties";
        }
    }

    public static class Execution extends AbstractStepExecutionImpl
    {
        private final ModifyPropertiesStep modifyPropertiesStep;
        private final StepContext stepContext;

        public Execution(ModifyPropertiesStep modifyPropertiesStep, StepContext stepContext)
        {
            this.modifyPropertiesStep = modifyPropertiesStep;
            this.stepContext = stepContext;
        }

        @Override
        public boolean start() throws Exception
        {
            long free = new File("/").getFreeSpace();
            PrintStream logger = stepContext.get(TaskListener.class).getLogger();
            Object proData = modifyPropertiesStep.data;
            File file = new File(modifyPropertiesStep.file);

            if(proData instanceof Map)
            {
                Properties pro = new Properties();
                if(file.exists())
                {
                    try(InputStream input = new FileInputStream(file))
                    {
                        pro.load(input);
                    }
                    catch (Exception e)
                    {
                        stepContext.onFailure(e);

                        throw e;
                    }
                }

                pro.putAll((Map) proData);

                try(OutputStream output = new FileOutputStream(file))
                {
                    pro.store(output, "");
                }
                catch (Exception e)
                {
                    stepContext.onFailure(e);

                    throw e;
                }

                stepContext.onSuccess("success");
            }

            return true;
        }

        @Override
        public void stop(@Nonnull Throwable cause) throws Exception
        {

        }
    }
}