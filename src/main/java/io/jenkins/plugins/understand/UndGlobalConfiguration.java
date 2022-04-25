package io.jenkins.plugins.understand;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

/**
 * Example of Jenkins global configuration.
 */
@Extension
public class UndGlobalConfiguration extends GlobalConfiguration {

    /** @return the singleton instance */
    public static UndGlobalConfiguration get() {
        return ExtensionList.lookupSingleton(UndGlobalConfiguration.class);
    }

    private String path;

    public UndGlobalConfiguration() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    /** @return the currently configured path, if any */
    public String getPath() {
        return path;
    }

    /**
     * Together with {@link #getPath}, binds to entry in {@code config.jelly}.
     * @param path the new value of this field
     */
    @DataBoundSetter
    public void setPath(String path) {
        this.path = path;
        save();
    }

    public FormValidation doCheckPath(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.warning("Please specify the path to Understand.");
        }
        return FormValidation.ok();
    }

}
