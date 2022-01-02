package browsers.customs;

import browsers.Browsers;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class ProfileLocalChrome extends Browsers {
    private static final ChromeOptions chromeOptions = new ChromeOptions();

    public ProfileLocalChrome() {
        super(chromeOptions);
        chromeOptions.addArguments("user-data-dir=C:\\Users\\blackout\\AppData\\Local\\Google\\Chrome\\User Data");
        chromeOptions.addArguments("--profile-directory=Profile 1");

    }


    @Override
    public WebDriver getRemoteDriver(String remoteWebDriverURl) throws MalformedURLException {
        return new RemoteWebDriver(new URL(remoteWebDriverURl), chromeOptions);
    }

    @Override
    public WebDriver getLocalDriver() {
        System.setProperty("webdriver.chrome.driver", "C:\\Selenium\\drivers\\chromedriver.exe");
        return new ChromeDriver(chromeOptions);
    }
}
