package browsers;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;

public abstract class Browsers {

    private final MutableCapabilities options;
    private DesiredCapabilities capability;


    //---------------------- CONTRUCTORS ---------------------------

    //Working
    public Browsers(MutableCapabilities options) {
        this.options = options;
    }

    //---------------------- METHODS ---------------------------
    protected void setCapability(String key, Object value) {
        capability.setCapability(key, value);
    }

    abstract public WebDriver getRemoteDriver(String remoteWebDriverURl) throws MalformedURLException;

    abstract public WebDriver getLocalDriver();

}
