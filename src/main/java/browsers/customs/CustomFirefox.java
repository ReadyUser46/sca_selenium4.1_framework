package browsers.customs;

import browsers.Browsers;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class CustomFirefox extends Browsers {
    private static final FirefoxOptions firefoxOptions = new FirefoxOptions();
    Proxy proxy = new Proxy();

    public CustomFirefox(String proxyPac, String node) {
        super(firefoxOptions);
        firefoxOptions.setCapability("browserName", "firefox");
        firefoxOptions.setCapability("platform", Platform.ANY);
        proxy.setProxyAutoconfigUrl(proxyPac);
        firefoxOptions.setCapability(CapabilityType.PROXY, proxy);
        if (!node.isEmpty()) {
            firefoxOptions.setCapability("applicationName", node);
        }
    }

    @Override
    public WebDriver getLocalDriver() {
        return null;
    }

    @Override
    public WebDriver getRemoteDriver(String remoteWebDriverURl) throws MalformedURLException {
        return new RemoteWebDriver(new URL(remoteWebDriverURl), firefoxOptions);
    }
}
