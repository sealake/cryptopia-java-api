package net.sealake.cryptopia.api.client;

import net.sealake.cryptopia.api.models.Balance;
import net.sealake.cryptopia.api.models.Market;
import net.sealake.cryptopia.api.models.Trade;
import net.sealake.cryptopia.api.models.TradeDetail;
import net.sealake.cryptopia.api.models.request.TradeRequest;

import java.util.List;

public interface CryptopiaClient {
  /**
   * 测试连通性
   */
  void ping();

  /**
   * 获取market信息，包括价格、行情等
   *
   * @param symbol TradePairId or MarketName， 比如 100， XZC_BTC
   * @return market实例
   */
  Market getMarket(final String symbol);

  /**
   * 查询所有货币资产
   */
  List<Balance> getAllBalances();

  /**
   * 查询指定货币资产信息
   *
   * @param currency 币种
   */
  Balance getBalance(String currency);

  /**
   * 发起交易
   * @param request 交易参数
   * @return Trade实例，包含交易的id等信息
   */
  Trade submitTrade(TradeRequest request);

  /**
   * 获取交易历史
   * @param symbol The market symbol of the history to return, e.g. 'DOT/BTC'
   * @param count The maximum amount of history to return e.g. '10'
   * @return 历史交易详情的列表
   */
  List<TradeDetail> getTradeHistory(String symbol, String count);
}