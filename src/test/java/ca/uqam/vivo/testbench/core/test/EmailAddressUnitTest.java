package ca.uqam.vivo.testbench.core.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import ca.uqam.vivo.testbench.util.SampleGraphUtil;
import ca.uqam.vivo.testbench.util.SeleniumHelper;
/**
 * 
 * @author Michel Heon
 * EmailAdressTest.java
 * 
 * 2020-04-16
 * This test validates the addition, modification and deletion of an email address for a user.
 */

public class EmailAddressUnitTest {
    private static final Log log = LogFactory.getLog(EmailAddressUnitTest.class);
    private WebDriver driver;
    JavascriptExecutor js;
    private String usrURI = "http://localhost:8080/vivo/individual/n6870";
    private String predicatToTestURI = "http://www.w3.org/2006/vcard/ns#email";
    private SeleniumHelper sh;

    @BeforeClass
    public void setUpBeforeClass() {
        try {
            log.info("Setup before Class");
            sh = SeleniumHelper.getInstance();
            sh.seleniumSetupTestCase();
            driver = sh.getDriver();
            js = (JavascriptExecutor) driver;            
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    @AfterClass
    public void tearDownAfterClass() throws Exception {
        log.info("Teardown after Class");
        driver.quit();
        sh.quit();
    }
    @Test(dependsOnMethods={"phase4"})
    private void phase5() throws InterruptedException {
        log.info("Phase 5 login out");
        sh.logout();
        log.info("Phase 5 logout");
    }
    @Test(dependsOnMethods={"phase3"})
    private void phase4() {
        log.info("Phase 4 Email modification validation");
        // 12 | click | css=#primary-email .delete-individual | 
        log.info("deleting email address ");
        driver.findElement(By.cssSelector("#primary-email .delete-individual")).click();
        // 13 | click | id=submit | 
        driver.findElement(By.id("submit")).click();
        String returnEmail = SampleGraphUtil.getValueFromTripleStore(query(), usrURI, predicatToTestURI);
        assertNull(returnEmail);
        log.info("Phase 4 Email modification validation done");

    }
    @Test(dependsOnMethods={"phase2"})
    private void phase3() {
        log.info("Phase 3 Email modification validation");
        String emailToTest = "japer@someemail.org";
        // 8 | click | css=#primary-email .edit-individual | 
        driver.findElement(By.cssSelector("#primary-email .edit-individual")).click();
        // 9 | click | id=emailAddress | 
        driver.findElement(By.id("emailAddress")).click();
        //        driver.findElement(By.id("emailAddress")).sendKeys(Keys.CONTROL,"a",Keys.DELETE);
        // 10 | type | id=emailAddress | peter.japer@someemail.com
        log.info("replacing the actual address by: "+emailToTest);
        driver.findElement(By.id("emailAddress")).sendKeys(Keys.CONTROL,"a",Keys.DELETE,emailToTest);
        // 11 | click | id=submit | 
        driver.findElement(By.id("submit")).click();
        String returnEmail = SampleGraphUtil.getValueFromTripleStore(query(), usrURI, predicatToTestURI);
        assertNotNull(returnEmail);
        assertEquals(emailToTest, returnEmail);
        log.info("Phase 3 Email modification validation done");
    }
    @Test(dependsOnMethods={"phase1"})
    private void phase2() throws InterruptedException {
        log.info("Phase 2 Email validation");
        String emailToTest = "peter.japer@someemail.org";
        /*  Equivalent to
         *         driver.findElement(By.linkText("Peters, Jasper I")).click();
         */
        driver.get(usrURI);
        // 4 | click | xpath= data-range =  http://www.w3.org/2006/vcard/ns#Work (Primary email Adress)
        driver.findElement(By.xpath("//*[@data-range='http://www.w3.org/2006/vcard/ns#Work']")).click();;
        // 5 | click | id=emailAddress | 
        driver.findElement(By.id("emailAddress")).click();
        // 6 | type | id=emailAddress | peter.japer@someemail.org
        log.info("adding "+emailToTest);
        driver.findElement(By.id("emailAddress")).sendKeys(emailToTest);
        // 7 | click | id=submit | 
        driver.findElement(By.id("submit")).click();
        String returnEmail = SampleGraphUtil.getValueFromTripleStore(query(), usrURI, predicatToTestURI);
        assertNotNull(returnEmail);
        assertEquals(emailToTest, returnEmail);
        log.info("Phase 2 Email validation done");
    }
    @Test()
    private void phase1() throws InterruptedException {
        log.info("Phase 1 Login");
        sh.login();
        log.info("Phase 1 Login done");
    }

    private String query() {
        String queryStr = SampleGraphUtil.getPrefixList()+ 
                "construct { <"+ usrURI +"> vcard:email  ?email . } \n"
                + "where {  \n"
                + "<"+ usrURI +"> obo:ARG_2000028/vcard:hasEmail/vcard:email  ?email . \n"
                +"}";
        return queryStr;
    }
}
