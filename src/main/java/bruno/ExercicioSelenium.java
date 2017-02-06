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
import main.IntervaloDeBusca;

public class ExercicioSelenium implements Exercicio {

	public static void main(String[] args) {
		ExercicioSelenium ex = new ExercicioSelenium();
		
		String termo = "Metallica";
		int pagina = 2;
		IntervaloDeBusca intervalo = IntervaloDeBusca.NO_ULTIMO_ANO;
		
		BuscaConfig config = new BuscaConfig();
		config.setPagina(pagina);
		config.setTermo(termo);
		config.setIntervalo(intervalo);
		
		List<String> result = ex.getUrls(config);
		for(String url: result)
			System.out.println(url);

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
		WebDriver webdriver = null;
		List<String> result = new ArrayList<String>();
		
		try {
			webdriver = startWebDriver();
		
			webdriver.get("https://www.google.com.br/");
			WebDriverWait wait = new WebDriverWait(webdriver, 10);
			
			WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("lst-ib")));
			element.sendKeys(config.getTermo());
			wait.until(ExpectedConditions.elementToBeClickable(By.id("_fZl"))).click();
			
			switch (config.getIntervalo()) {
			case EM_QUALQUER_DATA:	
				break;
			
			case NA_ULTIMA_HORA:
				wait.until(ExpectedConditions.elementToBeClickable(By.id("hdtb-tls"))).click();
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("resultStats")));
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='mn-hd-txt' and contains(text(),'Em qualquer data')]"))).click();
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@class='q qs' and contains(text(),'Na última hora')]"))).click();
				break;
			
			case NAS_ULTIMAS_24_HORAS:
				wait.until(ExpectedConditions.elementToBeClickable(By.id("hdtb-tls"))).click();
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("resultStats")));
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='mn-hd-txt' and contains(text(),'Em qualquer data')]"))).click();
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@class='q qs' and contains(text(),'Nas últimas 24 horas')]"))).click();
				break;
			
			case NA_ULTIMA_SEMANA:
				wait.until(ExpectedConditions.elementToBeClickable(By.id("hdtb-tls"))).click();
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("resultStats")));
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='mn-hd-txt' and contains(text(),'Em qualquer data')]"))).click();
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@class='q qs' and contains(text(),'Na última semana')]"))).click();
				break;
			
			case NO_ULTIMO_MES:
				wait.until(ExpectedConditions.elementToBeClickable(By.id("hdtb-tls"))).click();
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("resultStats")));
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='mn-hd-txt' and contains(text(),'Em qualquer data')]"))).click();
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@class='q qs' and contains(text(),'No último mês')]"))).click();
				break;
			
			case NO_ULTIMO_ANO:
				wait.until(ExpectedConditions.elementToBeClickable(By.id("hdtb-tls"))).click();
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("resultStats")));
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='mn-hd-txt' and contains(text(),'Em qualquer data')]"))).click();
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@class='q qs' and contains(text(),'No último ano')]"))).click();
				break;
			
			case INTERVALO_PERSONALIZADO:
				wait.until(ExpectedConditions.elementToBeClickable(By.id("hdtb-tls"))).click();
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("resultStats")));
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='mn-hd-txt' and contains(text(),'Em qualquer data')]"))).click();
				wait.until(ExpectedConditions.elementToBeClickable(By.id("cdrlnk"))).click();
				wait.until(ExpectedConditions.elementToBeClickable(By.id("cdr_min"))).sendKeys("01/01/2017");
				wait.until(ExpectedConditions.elementToBeClickable(By.id("cdr_max"))).sendKeys("02/02/2017");
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//form[@id='cdr_frm']/input[@type='submit']"))).click();
				break;
				
			default:
				break;
			}
			
			if (config.getPagina() < 11) {
				if (config.getPagina() != 1)
					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("flyr")));
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td/a[@aria-label='Page "+config.getPagina()+"']"))).click();
			} else {
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td/a[@aria-label='Page 10']"))).click();
				
				for (int i = 0; i < config.getPagina()-10; i++) {
					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("flyr")));
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@id='pnnext']"))).click();
				}
			}
			
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[@class='cur' and contains(text(),'"+config.getPagina()+"')]")));
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
	
	public String getWikiResume(String termo) {
		
		WebDriver webdriver = null;
		String result = null;
		
		try {
			webdriver = startWebDriver();
		
			webdriver.get("https://www.google.com.br/");
			WebDriverWait wait = new WebDriverWait(webdriver, 10);
			
			WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("lst-ib")));
			element.sendKeys(termo);
			
			WebElement element1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@jsl='$t t-JgTEvN6zUII;$x 0;']/span")));
			
			result = element1.getText();

		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			closeWebDriver(webdriver);
		}
		return result;
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
