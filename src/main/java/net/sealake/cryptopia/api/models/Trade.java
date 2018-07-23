package net.sealake.cryptopia.api.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户发起SubmitTrade请求之后的返回值
 */
@Data
@NoArgsConstructor
public class Trade {
  private Long orderId;
  private List<Long> filledOrders;

  public static Trade parse(String responseStr) {
    final ApiResponse<Trade> response = new ApiResponse<>();

    final JsonElement jElement = new JsonParser().parse(responseStr);
    final JsonObject rootObject = jElement.getAsJsonObject();

    response.setSuccess(rootObject.get("Success").getAsBoolean());
    response.setMessage(rootObject.get("Error").toString());
    response.validate();

    final Trade trade = new Trade();
    final JsonObject data = rootObject.get("Data").getAsJsonObject();
    trade.setOrderId(data.get("OrderId").getAsLong());

    trade.setFilledOrders(new ArrayList<>());
    final JsonArray jsonArray = data.get("FilledOrders").getAsJsonArray();
    for (JsonElement arrayElement : jsonArray) {
      final JsonObject filledOrderObject = arrayElement.getAsJsonObject();
      trade.getFilledOrders().add(filledOrderObject.getAsLong());
    }

    return trade;
  }
}
