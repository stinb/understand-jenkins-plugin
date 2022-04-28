package io.jenkins.plugins.understand;

import hudson.Launcher;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import hudson.util.ArgumentListBuilder;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

public class UndBuilder extends Builder implements SimpleBuildStep {

    private final String dbName;
    private String src;
    private String config;
    private boolean newDb;
    private boolean codecheck;
    private boolean metrics;

    @DataBoundConstructor
    public UndBuilder(String dbName) {
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }

    public boolean isNewDb() {
        return newDb;
    }

    @DataBoundSetter
    public void setNewDb(boolean newDb) {
        this.newDb = newDb;
    }
    
    public String getSrc() {
        return src;
    }
    
    @DataBoundSetter
    public void setSrc(String src) {
        this.src = src;
    }
    
    public boolean isCodecheck() {
        return codecheck;
    }

    @DataBoundSetter
    public void setCodecheck(boolean codecheck) {
        this.codecheck = codecheck;
    }
    
    public String getConfig() {
        return config;
    }

    @DataBoundSetter
    public void setConfig(String config) {
        this.config = config;
    }
    
    public boolean isMetrics() {
        return metrics;
    }

    @DataBoundSetter
    public void setMetrics(boolean metrics) {
        this.metrics = metrics;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        String exeName = "und";
        String exePath = UndGlobalConfiguration.get().getPath();
        if (exePath != null && exePath.length() > 0) {
            if (!exePath.endsWith("/") && !exePath.endsWith("\\")) {
                if (launcher.isUnix()) {
                    exePath += "/";
                } else {
                    exePath += "\\";
                }
            }
            exeName = exePath + exeName;
        }
        args.add(exeName, "-db", dbName);
    	if (newDb) {
            args.add("create","-languages", "all", "add",src);
        } 
        args.add("analyze","-sarif","und_analyze.sarif");
        if (codecheck) {
            args.add("codecheck","-sarif","und_cc.sarif",config,".");
        }
        if (metrics) {
            args.add("metrics");
        }
        
        int r;
        if (run instanceof AbstractBuild) {
            r = launcher.launch().cmds(args).envs(run.getEnvironment(listener))
                    .stdout(listener).pwd(workspace).join();
        } else {
            r = launcher.launch().cmds(args).stdout(listener).pwd(workspace).join();
        }
        if (run != null && r != 0) {
            run.setResult(Result.UNSTABLE);
        }
    }

    @Symbol("understand")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckDbName(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.UndBuilder_DescriptorImpl_errors_missingName());
            return FormValidation.ok();
        }
        
        public FormValidation doCheckSrc(@QueryParameter String value, @QueryParameter boolean newDb)
                throws IOException, ServletException {
            if (value.length() == 0 && newDb)
                return FormValidation.error(Messages.UndBuilder_DescriptorImpl_errors_missingSrc());
            return FormValidation.ok();
        }
        
        public FormValidation doCheckConfig(@QueryParameter String value, @QueryParameter boolean codecheck)
                throws IOException, ServletException {
            if (codecheck && value.length() == 0)
                return FormValidation.error(Messages.UndBuilder_DescriptorImpl_errors_missingConfig());
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.UndBuilder_DescriptorImpl_DisplayName();
        }

    }

}
