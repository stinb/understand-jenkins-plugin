package io.jenkins.plugins.understand;

import io.jenkins.plugins.understand.UndGlobalConfiguration;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsSessionRule;

public class UndGlobalConfigurationTest {

    @Rule
    public JenkinsSessionRule sessions = new JenkinsSessionRule();

    /**
     * Tries to exercise enough code paths to catch common mistakes:
     * <ul>
     * <li>missing {@code load}
     * <li>missing {@code save}
     * <li>misnamed or absent getter/setter
     * <li>misnamed {@code textbox}
     * </ul>
     */
    @Test
    public void uiAndStorage() throws Throwable {
        sessions.then(r -> {
            assertNull("not set initially", UndGlobalConfiguration.get().getPath());
            HtmlForm config = r.createWebClient().goTo("configure").getFormByName("config");
            HtmlTextInput textbox = config.getInputByName("_.path");
            textbox.setText("hello");
            r.submit(config);
            assertEquals("global config page let us edit it", "hello", UndGlobalConfiguration.get().getPath());
        });
        sessions.then(r -> {
            assertEquals("still there after restart of Jenkins", "hello", UndGlobalConfiguration.get().getPath());
        });
    }

}
