package net.sealake.cryptopia.api.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 价格、行情信息
 */
@Data
@NoArgsConstructor
public class Market {
  private Long tradePairId;

  private String label;

  private BigDecimal askPrice;

  private BigDecimal bidPrice;

  private BigDecimal low;

  private BigDecimal high;

  private BigDecimal volume;

  private BigDecimal lastPrice;  // 当前价格

  private BigDecimal buyVolume;

  private BigDecimal sellVolume;

  private BigDecimal change;

  private BigDecimal open;

  private BigDecimal close;

  private BigDecimal baseVolume;

  private BigDecimal baseBuyVolume;

  private BigDecimal baseSellVolume;

  /**
   * 解析查询指定市场行情信息的结果
   */
  public static Market parse(String responseStr) {
    final ApiResponse<Market> response = new ApiResponse<>();

    final JsonElement jElement = new JsonParser().parse(responseStr);
    final JsonObject rootObject = jElement.getAsJsonObject();

    response.setSuccess(rootObject.get("Success").getAsBoolean());
    response.setMessage(rootObject.get("Error").toString());
    response.validate();

    final JsonObject data = rootObject.get("Data").getAsJsonObject();
    return parseMarketObject(data);
  }

  /**
   * 解析查询所有市场行情信息的结果
   */
  public static List<Market> parseList(String jsonResponse) {
    final ApiResponse<List<Market>> apiResponse = new ApiResponse<>();
    final List<Market> markets = new ArrayList<>();

    final JsonElement jElement = new JsonParser().parse(jsonResponse);
    final JsonObject rootObject = jElement.getAsJsonObject();
    apiResponse.setMessage(rootObject.get("Message").toString());
    apiResponse.setSuccess(rootObject.get("Success").getAsBoolean());
    apiResponse.validate();

    apiResponse.setData(markets);
    apiResponse.setJson(jsonResponse);
    final JsonArray dataArray = rootObject.get("Data").getAsJsonArray();
    for (final JsonElement element : dataArray) {
      final JsonObject object = element.getAsJsonObject();
      final Market result = parseMarketObject(object);
      markets.add(result);
    }
    return markets;
  }

  private static Market parseMarketObject(JsonObject object) {
    final Market result = new Market();
    result.setTradePairId(object.get("TradePairId").getAsLong());
    result.setLabel(object.get("Label").toString());
    result.setAskPrice(object.get("AskPrice").getAsBigDecimal());
    result.setBidPrice(object.get("BidPrice").getAsBigDecimal());
    result.setLow(object.get("Low").getAsBigDecimal());
    result.setHigh(object.get("High").getAsBigDecimal());
    result.setVolume(object.get("Volume").getAsBigDecimal());
    result.setLastPrice(object.get("LastPrice").getAsBigDecimal());
    result.setBuyVolume(object.get("BuyVolume").getAsBigDecimal());
    result.setSellVolume(object.get("SellVolume").getAsBigDecimal());
    result.setChange(object.get("Change").getAsBigDecimal());
    result.setOpen(object.get("Open").getAsBigDecimal());
    result.setClose(object.get("Close").getAsBigDecimal());
    result.setBaseVolume(object.get("BaseVolume").getAsBigDecimal());
    result.setBaseBuyVolume(object.get("BuyBaseVolume").getAsBigDecimal());
    result.setBaseSellVolume(object.get("SellBaseVolume").getAsBigDecimal());
    return result;
  }
}
