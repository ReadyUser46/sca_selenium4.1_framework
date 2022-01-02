package browsers.standard;

import browsers.Browsers;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class StandarFirefox extends Browsers {
    private static final FirefoxOptions firefoxOptions = new FirefoxOptions();

    public StandarFirefox() {
        super(firefoxOptions);

    }

    @Override
    public WebDriver getLocalDriver() {
        System.setProperty("webdriver.gecko.driver", "C:\\Selenium\\drivers\\geckodriver.exe");
        return new FirefoxDriver();
    }

    @Override
    public WebDriver getRemoteDriver(String remoteWebDriverURl) throws MalformedURLException {
        return new RemoteWebDriver(new URL(remoteWebDriverURl), firefoxOptions);
    }
}
