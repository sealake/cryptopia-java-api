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

@Data
@NoArgsConstructor
public class Balance {
  private Long currencyId;

  private String symbol;

  private BigDecimal total;

  private BigDecimal available;

  private BigDecimal unconfirmed;

  private BigDecimal heldForTrades;

  private BigDecimal pendingWithdraw;

  private String address;

  private String baseAddress;

  private String status;

  private String statusMessage;

  /**
   * 从json字符串中解析balance列表
   */
  public static List<Balance> parse(String responseString) {

    final ApiResponse<List<Balance>> response = new ApiResponse<>();

    final List<Balance> balances = new ArrayList<>();
    response.setData(balances);

    final JsonElement jElement = new JsonParser().parse(responseString);
    final JsonObject rootObject = jElement.getAsJsonObject();
    response.setJson(responseString);

    response.setSuccess(rootObject.get("Success").getAsBoolean());
    response.setMessage(rootObject.get("Error").toString());
    response.validate();

    final JsonArray jsonArray = rootObject.get("Data").getAsJsonArray();
    for (final JsonElement element : jsonArray) {
      final JsonObject object = element.getAsJsonObject();

      final Balance balance = new Balance();
      balance.setCurrencyId(object.get("CurrencyId").getAsLong());
      balance.setSymbol(object.get("Symbol").toString());
      balance.setTotal(object.get("Total").getAsBigDecimal());
      balance.setAvailable(object.get("Available").getAsBigDecimal());
      balance.setUnconfirmed(object.get("Unconfirmed").getAsBigDecimal());
      balance.setHeldForTrades(object.get("HeldForTrades").getAsBigDecimal());
      balance.setPendingWithdraw(object.get("PendingWithdraw").getAsBigDecimal());
      balance.setAddress(object.get("Address").toString());
      balance.setBaseAddress(object.get("BaseAddress").toString());
      balance.setStatus(object.get("Status").toString());
      balance.setStatusMessage(object.get("StatusMessage").toString());

      balances.add(balance);
    }

    return balances;
  }
}
