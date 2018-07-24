## maven依赖

已上传到maven中央仓库，可以如下方式引入：

```
    <dependency>
      <groupId>net.sealaek</groupId>
      <artifactId>cryptopia-java-api</artifactId>
      <version>1.0.0.RELEASE</version>
    </dependency>
```

## 测试代码

```java
package net.sealake.demo.coin;

import com.alibaba.fastjson.JSON;

import net.sealake.cryptopia.api.client.CryptopiaClient;
import net.sealake.cryptopia.api.client.CryptopiaClientImpl;
import net.sealake.cryptopia.api.models.Balance;
import net.sealake.cryptopia.api.models.Market;
import net.sealake.cryptopia.api.models.Trade;
import net.sealake.cryptopia.api.models.TradeDetail;
import net.sealake.cryptopia.api.models.enums.TradeType;
import net.sealake.cryptopia.api.models.request.TradeRequest;

import java.math.BigDecimal;
import java.util.List;

public class CryptopiaDemo {
  public static final String ak = "xxxxxxxxxxxxxxxxxxxxxx";
  public static final String sk = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

  public static CryptopiaClient client = new CryptopiaClientImpl(ak, sk);

  public static void test_ping() {
    client.ping();
  }

  public static void test_getMarket() {
    Market priceMarket = client.getMarket("XZC_BTC");
    System.out.println(JSON.toJSONString(priceMarket));
  }

  public static void test_getBalance() {
    List<Balance> balances = client.getAllBalances();
    for (Balance balance: balances) {
      if (balance.getTotal().compareTo(BigDecimal.ZERO) > 0) {
        System.out.println(JSON.toJSONString(balance));
      }
    }

    Balance balance = client.getBalance("XZC");
    System.out.println(JSON.toJSONString(balance));
  }

  public static void test_submitTrade() {
    Market market = client.getMarket("XZC_BTC");
    BigDecimal price = market.getLastPrice();

    TradeRequest tradeRequest = new TradeRequest();
    tradeRequest.setMarket("XZC/BTC");
    tradeRequest.setAmount(new BigDecimal("0.3"));
    tradeRequest.setRate(price);
    tradeRequest.setType(TradeType.SELL);

    Trade trade = client.submitTrade(tradeRequest);
    System.out.println(JSON.toJSONString(trade));

  }

  public static void test_tradeHistory() {

    List<TradeDetail> tradeDetails = client.getTradeHistory("XZC/BTC", "10");
    System.out.println(JSON.toJSONString(tradeDetails));

    String orderId = "1183075376";
  }

  public static void main(String... args) {
    test_ping();

    test_getMarket();

    test_getBalance();

    test_submitTrade();

    test_tradeHistory();
  }
}
```
