package browsers.standard;

import browsers.Browsers;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class StandarChrome extends Browsers {
    private static final ChromeOptions chromeOptions = new ChromeOptions();

    public StandarChrome() {
        super(chromeOptions);
        chromeOptions.setCapability("browserName", "chrome");
    }

    @Override
    public WebDriver getLocalDriver() {
        System.setProperty("webdriver.chrome.driver", "C:\\Selenium\\drivers\\chromedriver.exe");
        return new ChromeDriver();
    }

    @Override
    public WebDriver getRemoteDriver(String remoteWebDriverURl) throws MalformedURLException {
        return new RemoteWebDriver(new URL(remoteWebDriverURl), chromeOptions);
    }
}
