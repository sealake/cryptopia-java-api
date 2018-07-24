package net.sealake.cryptopia.api.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * GetTradeDetail获取到的交易详情信息
 */
@Data
@NoArgsConstructor
public class TradeDetail {

  private Long tradeId;

  private Long tradePairId;

  private String market;

  private String type;

  private BigDecimal rate;

  private BigDecimal amount;

  private BigDecimal total;

  private BigDecimal fee;

  private DateTime time;

  public static List<TradeDetail> parse(String responseString) {
    final ApiResponse<List<TradeDetail>> apiResponse = new ApiResponse<>();

    final JsonElement jElement = new JsonParser().parse(responseString);
    final JsonObject rootObject = jElement.getAsJsonObject();
    apiResponse.setJson(responseString);
    apiResponse.setSuccess(rootObject.get("Success").getAsBoolean());
    apiResponse.setMessage(rootObject.get("Error").toString());
    apiResponse.validate();

    // 解析列表中的对象
    final List<TradeDetail> results = new ArrayList<>();
    final JsonArray jsonArray = rootObject.get("Data").getAsJsonArray();
    for (final JsonElement element : jsonArray) {
      final JsonObject object = element.getAsJsonObject();
      final TradeDetail item = new TradeDetail();
      item.setTradeId(object.get("TradeId").getAsLong());
      item.setTradePairId(object.get("TradePairId").getAsLong());
      item.setMarket(object.get("Market").toString());
      item.setType(object.get("Type").toString());
      item.setRate(object.get("Rate").getAsBigDecimal());
      item.setAmount(object.get("Amount").getAsBigDecimal());
      item.setTotal(object.get("Total").getAsBigDecimal());
      item.setFee(object.get("Fee").getAsBigDecimal());
      item.setTime(new DateTime(object.get("TimeStamp").getAsString()));

      results.add(item);
    }

    return results;
  }
}