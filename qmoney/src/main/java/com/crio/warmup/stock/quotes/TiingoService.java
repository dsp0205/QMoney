package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {


  private RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) 
      throws JsonProcessingException, StockQuoteServiceException{
        List<Candle> stocksStartToEndDate = new ArrayList<>();

        if(from.compareTo(to) >= 0) {
          throw new RuntimeException();
        }
    
        String url = buildUri(symbol, to, to);
    
        try {
          String stocks = restTemplate.getForObject(url, String.class);
          
          ObjectMapper objectMapper = new ObjectMapper();
          objectMapper.registerModule(new JavaTimeModule());
    
          TiingoCandle[] stocksStartToEndDateArray = objectMapper.readValue(stocks, TiingoCandle[].class);
          stocksStartToEndDate = Arrays.asList(stocksStartToEndDateArray);
        }
        catch (NullPointerException e) {
          throw new StockQuoteServiceException("Error occured when requesting response from Tiingo API", e.getCause());
        }
        return stocksStartToEndDate;
  }

  

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?" + "startDate="
        + startDate.toString() + "&endDate=" + endDate.toString() + "&token="
        + "cf9ad7d9ecc916b282e9acf7fe6153e07b757268";
    return uriTemplate;
  }
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Now we will be separating communication with Tiingo from PortfolioManager.
  //  Generate the functions as per the declarations in the interface and then
  //  Move the code from PortfolioManagerImpl#getSTockQuotes inside newly created method.
  //  Run the tests using command below -
  //  ./gradlew test --tests TiingoServiceTest and make sure it passes.



  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Write a method to create appropriate url to call tiingo service.




  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //  Update the method signature to match the signature change in the interface.
  //  Start throwing new StockQuoteServiceException when you get some invalid response from
  //  Tiingo, or if Tiingo returns empty results for whatever reason,
  //  or you encounter a runtime exception during Json parsing.
  //  Make sure that the exception propagates all the way from
  //  PortfolioManager#calculateAnnualisedReturns,
  //  so that the external user's of our API are able to explicitly handle this exception upfront.

  //CHECKSTYLE:OFF


}
