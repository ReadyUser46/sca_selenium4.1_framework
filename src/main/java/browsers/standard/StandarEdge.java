package browsers.standard;

import browsers.Browsers;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class StandarEdge extends Browsers {
    private static final EdgeOptions edgeOptions = new EdgeOptions();

    public StandarEdge() {
        super(edgeOptions);

    }

    @Override
    public WebDriver getLocalDriver() {
        System.setProperty("webdriver.edge.driver", "C:\\Selenium\\drivers\\msedgedriver.exe");
        return new EdgeDriver();
    }

    @Override
    public WebDriver getRemoteDriver(String remoteWebDriverURl) throws MalformedURLException {
        return new RemoteWebDriver(new URL(remoteWebDriverURl), edgeOptions);
    }
}
