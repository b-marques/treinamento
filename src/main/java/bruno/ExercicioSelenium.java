package bruno;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import main.BuscaConfig;
import main.Delay;
import main.Exercicio;

public class ExercicioSelenium implements Exercicio {

	public static void main(String[] args) {
		ExercicioSelenium ex = new ExercicioSelenium();
		
//		long result = ex.getNumeroAproximadoDoResultadoDaBuscaPor("metallica");
//		System.out.println(result);
		
		
		List<String> result = ex.getUrls("metallica", 15);
		for (String link : result)
			System.out.println(link);

	}
	
	public long getNumeroAproximadoDoResultadoDaBuscaPor(String termo) {
		
		WebDriver webdriver = null;
		long result = 0;
		
		try {
			webdriver = startWebDriver();
		
			webdriver.get("https://www.google.com.br/");
			WebDriverWait wait = new WebDriverWait(webdriver, 10);
			
			WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("lst-ib")));
			element.sendKeys(termo);
			
			WebElement element1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("resultStats")));
			String resultString = element1.getText();
			Pattern pattern = Pattern.compile("(\\d+\\.?)+");
			Matcher matcher = pattern.matcher(resultString);
			if (matcher.find()) {
				String justNumber = matcher.group();			
				NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);
				try {
					Number number = nf.parse(justNumber);
					result = number.longValue();
				} catch (ParseException e) {
					e.printStackTrace();
				}				
			} else {
				result = -1;
			}
		
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			closeWebDriver(webdriver);
		}
		return result;	
	}
	
	public List<String> getUrls(String termo) {
		WebDriver webdriver = null;
		List<String> result = new ArrayList<String>();
		
		try {
			webdriver = startWebDriver();
		
			webdriver.get("https://www.google.com.br/");
			WebDriverWait wait = new WebDriverWait(webdriver, 10);
			
			WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("lst-ib")));
			element.sendKeys(termo);
			
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//h3[@class='r']/a")));
			List<WebElement> resultSet = webdriver.findElements(By.xpath("//h3[@class='r']/a"));
			
			for(WebElement link: resultSet)
				result.add(link.getAttribute("href"));

		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			closeWebDriver(webdriver);
		}
		return result;
	}
	
	public List<String> getUrls(String termo, int pag) {
		
		WebDriver webdriver = null;
		List<String> result = new ArrayList<String>();
		
		try {
			webdriver = startWebDriver();
		
			webdriver.get("https://www.google.com.br/");
			WebDriverWait wait = new WebDriverWait(webdriver, 10);
			
			WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("lst-ib")));
			element.sendKeys(termo);
			
			if (pag < 11) {
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td/a[@aria-label='Page "+pag+"']"))).click();
			} else {
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td/a[@aria-label='Page 10']"))).click();
				
				for (int i = 0; i < pag-10; i++) {
					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("flyr")));
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@id='pnnext']"))).click();
				}
			}
			
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[@class='cur' and contains(text(),'"+pag+"')]")));
			List<WebElement> resultSet = webdriver.findElements(By.xpath("//h3[@class='r']/a"));
			
			for(WebElement link: resultSet)
				result.add(link.getAttribute("href"));

		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			closeWebDriver(webdriver);
		}
		return result;
	}
	
	public List<String> getUrls(BuscaConfig config) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getWikiResume(String termo) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private WebDriver startWebDriver(){
		System.setProperty("webdriver.chrome.driver", "chromedriverlinux");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		WebDriver webdriver = new ChromeDriver(options);
		webdriver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
		return webdriver;
	}
	
	private void closeWebDriver(WebDriver webdriver) {
		webdriver.close();
		webdriver.quit();
	}
}
