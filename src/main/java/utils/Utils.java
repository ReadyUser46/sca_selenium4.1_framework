package utils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import io.qameta.allure.Step;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;

import java.io.*;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("JavaDoc")
public class Utils {

    /**
     * @author Sergio Caballero
     */

    protected static final Logger LOGGER = Logger.getLogger(Utils.class.getName());
    protected final WebDriver driver;
    protected final String testCaseName;

    public Utils(WebDriver driver, String testCaseName) {
        this.driver = driver;
        this.testCaseName = testCaseName;
    }

    /**
     * Execute a bash command. We can handle complex bash commands including
     * multiple executions (; | && ||), quotes, expansions ($), escapes (\), e.g.:
     * "cd /abc/def; mv ghi 'older ghi '$(whoami)"
     *
     * @param command
     * @return true if bash got started, but your command may have failed.
     */
    public static boolean executeBashCommand(String command) {
        boolean success = false;
        System.out.println("Executing BASH command:\n   " + command);
        Runtime r = Runtime.getRuntime();
        // Use bash -c so we can handle things like multi commands separated by ; and
        // things like quotes, $, |, and \. My tests show that command comes as
        // one argument to bash, so we do not need to quote it to make it one thing.
        // Also, exec may object if it does not have an executable file as the first thing,
        // so having bash here makes it happy provided bash is installed and in path.
        String[] commands = {"bash", "-c", command};
        try {
            Process p = r.exec(commands);

            p.waitFor();
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";

            while ((line = b.readLine()) != null) {
                System.out.println(line);
            }

            b.close();
            success = true;
        } catch (Exception e) {
            System.err.println("Failed to execute bash with command: " + command);
            e.printStackTrace();
        }
        return success;
    }

    //HARDCODE SLEEPS
    public void espera(int s) {
        try {
            Thread.sleep(1000 * s);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception occur");
        }
    }

    public void espera() {
        espera(1);
    }

    // WINDOWS
    public void switchToWindow(String name) {
        driver.switchTo().window(name);
    }

    public void switchToNextWindow() {
        final int countWindows = driver.getWindowHandles().size();
        String actualWindowName = driver.getWindowHandle();
        for (String winHandle : driver.getWindowHandles()) {
            if (!actualWindowName.equals(winHandle)) {
                switchToWindow(winHandle);
            }
        }
    }

    public void acceptAlert(long timeOutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, String.format("alert not present for: '%s' --> continue", testCaseName));
        }
    }

    //SET AND GET VALUES FROM EXTERNAL FILE
    public String readProperty(String key) {
        String propertiesPath = "src/main/resources/config.properties";

        String value = null;
        try {
            PropertiesConfiguration config = new PropertiesConfiguration(propertiesPath);
            value = config.getProperty(key).toString();
        } catch (ConfigurationException e) {
            LOGGER.log(Level.SEVERE, "Exception occur while reading external property", e);
        }
        return value;

    }


    public void setAuxValue(String key, String value, String propertyFileName) {

        String propertiesFilePath = System.getProperty("configDirectory") + propertyFileName;

        if (!new File(propertiesFilePath).exists()) {
            createPropertyFile_new(propertyFileName);
        }

        try {
            PropertiesConfiguration config = new PropertiesConfiguration(propertiesFilePath);
            config.setProperty(key, value);
            config.save();
        } catch (ConfigurationException e) {
            LOGGER.log(Level.SEVERE, "Exception occur", e);
        }
    }

    private void createPropertyFile_new(String propertyFileName) {


        // Create file for a given path+name and set size
        File file = new File(System.getProperty("configDirectory") + propertyFileName);
        try {
            if (file.createNewFile()) LOGGER.info("The file was created in: " + file);
            else LOGGER.info("[ERROR] Error creating directory");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception occur", e);
        }

        String todayDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        setAuxValue("CreationDate", todayDate, propertyFileName);
    }

    //NEW STABILITY METHODS --> SERGIO CABALLERO
    public void waitForVisible(By element) {
        waitForVisible(element, 60);
    }

    public void waitForClickable(By element) {
        waitForClickable(element, 60);
    }

    public boolean isElementVisible(By element) {
        return isElementVisible(element, 60);
    }

    public boolean isElementVisible(By element, int timeoutInSeconds) {
        if (isElementLocated(element, timeoutInSeconds)) { //First, we check the element is located
            return driver.findElement(element).isDisplayed();
        } else return false;
    }

    public boolean isElementVisibleAngular(By element) {
        return isElementVisibleAngular(element, 60);
    }

    public boolean isElementVisibleAngular(By element, int timeoutInSeconds) {
        for (int i = 0; i < timeoutInSeconds; i++) {
            if (!isElementVisible(element, 1)) {
                espera();
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean isElementLocated(By element, int timeoutInSeconds) { //locates an element
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeoutInSeconds));
        boolean exists = !driver.findElements(element).isEmpty();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        return exists;
    }

    public boolean isElementLocated(By element) {
        return isElementLocated(element, 60);
    }

    public void executeJS(String funcion, WebElement e) {
        ((JavascriptExecutor) driver).executeScript(funcion, e);
    }

    public void executeJS(String funcion) {
        ((JavascriptExecutor) driver).executeScript(funcion);
    }

    public void waitForInvisible(By element) {
        waitForInvisible(element, 60);
    }

    public void waitForClickable(By element, int timeOutInSeconds) {
        //Clickable = located on DOM + visible + clickable
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            espera(); //needed for angular
        } catch (TimeoutException e) {
            LOGGER.log(Level.SEVERE, String.format("Element not clickable for: '%s'", testCaseName));
            Assert.fail("Element might not be located && visible && clickable");
        }
    }

    public void waitForVisible(By element, int timeOutInSeconds) {
        //Visible = located on DOM + visible
        WebDriverWait image = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
        try {
            image.until(ExpectedConditions.visibilityOfElementLocated(element));
            espera(); //needed for angular
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("Element not visible for: '%s'", testCaseName));
            Assert.fail("Element is not visible");
        }
    }

    public void waitForJSandJqueryFinish() {
        waitForJSandJqueryFinish(60);
    }

    public void waitForInvisible(By element, int timeOutInSeconds) {
        WebDriverWait image = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
        image.until(ExpectedConditions.invisibilityOfElementLocated(element));
        espera(); //needed for angular
    }

    public void waitForNumberOfWindows(int numberOfWindows) {
        waitForNumberOfWindows(numberOfWindows, 60);
    }

    public void waitForNumberOfWindows(final int numberOfWindows, int timeOutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutSeconds));
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (driver.getWindowHandles().size() == numberOfWindows);
            }
        };
        wait.until(expectation);
        espera(); //needed for angular
    }

    public void waitForJSandJqueryFinish(int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        // wait for jQuery to load
        ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return ((Long) ((JavascriptExecutor) driver).executeScript("return jQuery.active") == 0);
                } catch (Exception e) {
                    return true;
                }
            }
        };
        // wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        };
        boolean waitFlag = wait.until(jQueryLoad) && wait.until(jsLoad);
        espera(); //needed for angular
    }

    public void sendKeysAndTrigger(WebElement element, String keys) {
        sendKeysAndTrigger(element, keys, true);
    }

    public void performBackgroundClick(boolean wait) {
        // this method clicks on the page background, simulating a human click, to invoke all the events (onChangeText, onLostFocus, onBlur...)
        By backgroundLocator = By.xpath("//body");
        waitForClickable(backgroundLocator);
        driver.findElement(backgroundLocator).click();
        if (wait) {
            waitForJSandJqueryFinish();
        }
    }

    public void performBackgroundClick() {
        performBackgroundClick(true);
    }

    //COMMON ELEMENT ACTIONS - SERGIO CABALLERO
    public void sendKeysAndTrigger(WebElement element, String keys, boolean wait) {
        element.clear();
        element.sendKeys(keys);
        performBackgroundClick(wait);
    }

    public void waitForLocated(By element) {
        //Located = located on DOM
        WebDriverWait image = new WebDriverWait(driver, Duration.ofSeconds(60));
        try {
            image.until(ExpectedConditions.presenceOfElementLocated(element));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("Element not located for: '%s'", testCaseName));
            Assert.fail("Element is not located");
        }
    }

    public void executeSshCommand(String username, String password,
                                  String host, int port, String command) throws Exception {

        Session session = null;
        ChannelExec channel = null;

        try {
            session = new JSch().getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            System.out.println("[INFO][AUTOMATION] Executing command shell: " + command);
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.connect();

            while (channel.isConnected()) {
                Thread.sleep(100);
            }

            String responseString = new String(responseStream.toByteArray());
            System.out.println("[INFO][AUTOMATION] Command executed: " + command);

            System.out.println(responseString);
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    public boolean verifyTextIsPresentInPDF(String textToVerify, String filePath) throws URISyntaxException {
        try {

            String pdfOutput = null;
            File pdfFile = new File(filePath);
            PDDocument document = PDDocument.load(pdfFile);
            pdfOutput = new PDFTextStripper().getText(document);
            return pdfOutput.contains(textToVerify);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getTextFromPdf(String filePath) {
        try {

            String pdfOutput = null;
            File pdfFile = new File(filePath);
            PDDocument document = PDDocument.load(pdfFile);
            pdfOutput = new PDFTextStripper().getText(document);
            return pdfOutput;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error when getting text from pdf";
    }

    private void executeCommandShell(String command) {

        String s;
        Process p;
        try {
            p = new ProcessBuilder(command).start();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            System.out.println("[INFO][AUTOMATION] Executing command shell: " + command);
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            p.waitFor();
            System.out.println("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {
            System.out.println("[INFO][AUTOMATION] Error while executing command shell: " + command);
            e.printStackTrace();
        }
    }

    public void scrollToElement(By by) {
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public void sendKeysJS(WebElement element, String value) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[1].value = arguments[0]; ", value, element);
    }

    public String[] getPdfDetails() {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

        //open downloads page
        jsExecutor.executeScript("window.open()");
        espera();
        switchToNextWindow();
        driver.get("chrome://downloads");

        //getDownloadDetails
        String fileName = (String) jsExecutor.executeScript("return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('div#content #file-link').text");
        String sourceURL = (String) jsExecutor.executeScript("return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('div#content #file-link').href");

        return new String[]{fileName, sourceURL};
    }

    //ASSERTS
    @Step("{1}}")
    public void assertTrue(Boolean bol, String checkMessage) throws URISyntaxException, IOException, AssertionError {
        try {
            Assert.assertTrue(bol, checkMessage);
        } catch (AssertionError e) {
            throw new AssertionError(e.getMessage());
        }
    }

    @Step("{3}}")
    public void assertEquals(String condition1, String condition2, String assertMessage) throws URISyntaxException, IOException, AssertionError {
        try {
            Assert.assertEquals(condition1, condition2, null);
            // return true;
        } catch (AssertionError | Exception e) {
            throw new AssertionError(e.getMessage());
        }
    }

    public void skipTest(String reason) {
        throw new SkipException(reason);
    }


}
