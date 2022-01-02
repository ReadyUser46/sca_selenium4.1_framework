package setup;


import browsers.Browsers;
import browsers.customs.ProfileChrome;
import browsers.customs.ProfileLocalChrome;
import browsers.standard.StandarChrome;
import browsers.standard.StandarEdge;
import browsers.standard.StandarFirefox;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SetupWebdriver {

    private static final String grid_url = "http://localhost:4444";
    private static final int pageTimeout = 300; // The Timeout in seconds when a load page expectation is called
    private static final int implicitTimeout = 20; // The Timeout in seconds when an implicit expectation is called
    private static final int scriptTimeout = 600; // The script Timeout in seconds when a load script expectation is called
    protected String testCaseName;
    protected WebDriver driver;
    private String browserName = null;
    private String targetUrl = null;
    private Logger logger;

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Logger customLog() {
        return logger;
    }

    @BeforeMethod(alwaysRun = true)
    public void setup() {

        HashMap<String, Browsers> browsers = new HashMap<>();
        browsers.put("chrome", new StandarChrome());
        browsers.put("firefox", new StandarFirefox());
        browsers.put("edge", new StandarEdge());
        browsers.put("chrome-profile", new ProfileChrome());
        browsers.put("chrome-local-profile", new ProfileLocalChrome());


        Browsers browser = browsers.get(browserName);


        if (System.getProperty("remote") != null && System.getProperty("remote").equalsIgnoreCase("true")) {
            try {
                driver = browser.getRemoteDriver(grid_url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            driver = browser.getLocalDriver();
        }

        /*manage timeouts*/
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitTimeout));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageTimeout));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(scriptTimeout));

        /*Logger*/
        setLogger(Logger.getLogger("setup"));
        customLog().info(String.format("\n[INIT] Test case = '%s' will be executed\n" +
                        "[INIT] Browser = '%s' | Target url = %s\n" +
                        "[INIT] Webdriver initialized",
                testCaseName, browserName, targetUrl));


        /*go to target url*/
        driver.get(targetUrl);

    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        try {
            //todo takeRemoteScreenshot(customWebDriver(), customWebDriver().getCurrentUrl());
            driver.quit();
            customLog().log(Level.INFO, String.format("Webdriver Released for: '%s'", testCaseName));
        } catch (Exception e) {
            customLog().log(Level.SEVERE, String.format("Error closing Webdriver for: '%s'", testCaseName));
        }
    }


}
