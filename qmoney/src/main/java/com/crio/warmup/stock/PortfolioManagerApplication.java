
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {





  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>

  private static Object portfolioTrades;
  // Module 1
  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    ObjectMapper objectMapper = getObjectMapper();
    PortfolioTrade[] trades = objectMapper.readValue(file, PortfolioTrade[].class);
    List<String> symbols = new ArrayList<String>();
    for(PortfolioTrade t : trades) {
      symbols.add(t.getSymbol());
    }

    return symbols;
  }
 // Module 2
  public static List<TotalReturnsDto> mainReadQuotesHelper(String[] args, List<PortfolioTrade> trades) throws IOException, URISyntaxException {
    RestTemplate restTemplate = new RestTemplate(); 
    List<TotalReturnsDto> tests = new ArrayList<TotalReturnsDto>(); 
    String token = "cf9ad7d9ecc916b282e9acf7fe6153e07b757268";
    for (PortfolioTrade t: trades) { 
       //String url= "https://api.tiingo.com/tiingo/daily/"+t.getSymbol() + "/prices?startDate=" + t.getPurchaseDate().toString() + "&endDate=" + args[1] + "&token=d5c5e3f42e549e77a55da30ce35e74132754339a"; 
      LocalDate localDate = LocalDate.parse(args[1]);
      String url = prepareUrl(t, localDate, token);
      TiingoCandle[] results = restTemplate.getForObject(url, TiingoCandle[].class); 
      if (results != null) { 
        tests.add(new TotalReturnsDto(t.getSymbol(), results [results.length - 1].getClose())); 
      } 
     
    } 
    return tests; 
  }
  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    ObjectMapper om = getObjectMapper();
    List<PortfolioTrade> trades = Arrays.asList(om.readValue(resolveFileFromResources(args[0]), PortfolioTrade[].class));
    List<TotalReturnsDto> sortedByValue = mainReadQuotesHelper(args, trades); 
    Collections.sort(sortedByValue, new Comparator<TotalReturnsDto> () {   
      public int compare (TotalReturnsDto t1, TotalReturnsDto t2) { 
        return (int) (t1.getClosingPrice().compareTo(t2.getClosingPrice()));
      }
     
    }); 
    List<String> stocks = new ArrayList<String>(); 
    for (TotalReturnsDto trd: sortedByValue) {

      stocks.add(trd.getSymbol());
    }
    return stocks;
  }

 
 


// TODO:
//  After refactor, make sure that the tests pass by using these two commands
//  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
//  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile



// TODO:
//  Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    String url1 = "https://api.tiingo.com/tiingo/daily/"+trade.getSymbol() + "/prices?startDate=" + trade.getPurchaseDate().toString() + "&endDate=" + endDate.toString() + "&token=" + token;
    return url1;
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
       Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/deeppimpale67-ME_QMONEY_V2/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@5542c4ed";
    String functionNameFromTestFileInStackTrace = "PortfolioManagerApplicationTest.mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "24:1";


    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
    toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
    lineNumberFromTestFileInStackTrace});
  }


// Note:
// Remember to confirm that you are getting same results for annualized returns as in Module 3.



 // public static void main(String[] args) throws Exception {
 //    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
 //    ThreadContext.put("runId", UUID.randomUUID().toString());

 //    printJsonObject(mainReadQuotes(args));



 // }

 
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    File file = resolveFileFromResources(filename);
    ObjectMapper objectMapper = getObjectMapper();
    PortfolioTrade[] portfolioTrades = objectMapper.readValue(file, PortfolioTrade[].class);
    List<PortfolioTrade> trades = new ArrayList<>();
    for(PortfolioTrade trade : portfolioTrades) {
       trades.add(trade);
    }

    return trades;
  }

 // Module 3

 // TODO: CRIO_TASK_MODULE_CALCULATIONS
//  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
//  for the stocks provided in the Json.
//  Use the function you just wrote #calculateAnnualizedReturns.
//  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

// Note:
// 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
// 2. Remember to get the latest quotes from Tiingo API.




// TODO:
//  Ensure all tests are passing using below command
//  ./gradlew test --tests ModuleThreeRefactorTest

  public static String getToken() {
    return "cf9ad7d9ecc916b282e9acf7fe6153e07b757268";
  }
  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    return candles.get(0).getOpen();
    //return 0.0;
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    return candles.get(candles.size() - 1).getClose();
  }


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    String url = prepareUrl(trade, endDate, token);
    Candle[] candlesArr = getRestTemplate().getForObject(url, TiingoCandle[].class);
    List<Candle> listCandles = new ArrayList<>();
    if (candlesArr != null) {
      for (Candle cd : candlesArr) {
        listCandles.add(cd);
      }
    }

    return listCandles;
  }

  private static RestTemplate getRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    return restTemplate;
  }
  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
    throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    LocalDate endDate = LocalDate.parse(args[1]);
    byte[] byteArray = Files.readAllBytes(file.toPath());
    ObjectMapper objectMapper = getObjectMapper();
    PortfolioTrade[] portfolioTrades = objectMapper.readValue(byteArray, PortfolioTrade[].class);

    String token = "cf9ad7d9ecc916b282e9acf7fe6153e07b757268";
    String uri = "https://api.tiingo.com/tiingo/daily/$SYMBOL/"
         + "prices?startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";

    List<AnnualizedReturn> tem = new ArrayList<AnnualizedReturn>();
    for (PortfolioTrade portfolioTrade : portfolioTrades) {
      String url = uri.replace("$APIKEY", token).replace("$SYMBOL", portfolioTrade.getSymbol())
        .replace("$STARTDATE", portfolioTrade.getPurchaseDate().toString())
        .replace("$ENDDATE", endDate.toString());

      try {
        TiingoCandle[] tiingoCandles = new RestTemplate().getForObject(url, TiingoCandle[].class);
        Double buyPrice = tiingoCandles[0].getOpen();
        Double sellPrice = tiingoCandles[tiingoCandles.length - 1].getClose();
        AnnualizedReturn obj = calculateAnnualizedReturns(endDate, 
          portfolioTrade, buyPrice, sellPrice);
        tem.add(obj);
      } catch (NullPointerException e) {
        e.printStackTrace();
      }
    }
    tem.sort(Comparator.comparing(AnnualizedReturn::getAnnualizedReturn));
    Collections.reverse(tem);
    return tem;

  }

// TODO: CRIO_TASK_MODULE_CALCULATIONS
//  Return the populated list of AnnualizedReturn for all stocks.
//  Annualized returns should be calculated in two steps:
//   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
//      1.1 Store the same as totalReturns
//   2. Calculate extrapolated annualized returns by scaling the same in years span.
//      The formula is:
//      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
//      2.1 Store the same as annualized_returns
//  Test the same using below specified command. The build should be successful.
//     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

 
  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
  PortfolioTrade trade, Double buyPrice, Double sellPrice) {
    // calculate absolute total return
    Double absReturn = (sellPrice - buyPrice) / buyPrice;
    
    String symbol = trade.getSymbol();
    LocalDate purchasDate = trade.getPurchaseDate();

    // calculate years
    Double numYears = (double) ChronoUnit.DAYS.between(purchasDate, endDate) / 365;
    // calculate annualized returns using formula
    Double annualizedReturns = Math.pow((1 + absReturn), (1 / numYears)) - 1;

    return new AnnualizedReturn(symbol, annualizedReturns, absReturn);

  }



















  // module 4
  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
    throws Exception {
    String file = args[0];
    LocalDate endDate = LocalDate.parse(args[1]);
    String contents = readFileAsString(file);
    ObjectMapper objectMapper = getObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager(restTemplate);
    PortfolioTrade[] portfolioTrades = objectMapper.readValue(contents, PortfolioTrade[].class);
    return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
  }


  private static String readFileAsString(String file) throws IOException {
    String fileread = new String(Files.readAllBytes(Paths.get(file)), 
      StandardCharsets.UTF_8);
    return fileread;
  }
  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());




    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }
}

