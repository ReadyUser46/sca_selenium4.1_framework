package browsers.customs;

import browsers.Browsers;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class ProfileChrome extends Browsers {
    private static final ChromeOptions chromeOptions = new ChromeOptions();

    public ProfileChrome() {
        super(chromeOptions);
        //chromeOptions.addExtensions(new File("src/main/resources/mtmsk_10_0_2_0.crx"));
        //chromeOptions.addArguments("user-data-dir=C:\\Users\\blackout\\AppData\\Local\\Google\\Chrome\\User Data");
        chromeOptions.addArguments("user-data-dir=/tmp/chrome_profiles");
        chromeOptions.addArguments("--profile-directory=Profile 2");

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
