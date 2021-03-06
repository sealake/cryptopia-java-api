package net.sealake.cryptopia.api.client;

import com.google.gson.JsonObject;

import net.sealake.cryptopia.api.constants.ApiConstants;
import net.sealake.cryptopia.api.models.Balance;
import net.sealake.cryptopia.api.models.Market;
import net.sealake.cryptopia.api.models.Trade;
import net.sealake.cryptopia.api.models.TradeDetail;
import net.sealake.cryptopia.api.models.request.TradeRequest;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CryptopiaClientImpl implements CryptopiaClient {

  private String apiKey;

  private String secretKey;

  private OkHttpClient httpClient;

  public CryptopiaClientImpl(String apiKey, String secretKey) {
    this.apiKey = apiKey;
    this.secretKey = secretKey;
    this.httpClient = new OkHttpClient();
  }

  /**
   * 连通性测试，因为cryptopia不提供该api，因此我们执行一次btc的余额查询。
   */
  @Override
  public void ping() {

    JsonObject params = new JsonObject();
    params.addProperty("Currency", "BTC");

    try {
      Response response = request("GetBalance", params.toString());
      assert response.body().string().length() >= 0;
    } catch (Exception ex) {
      throw new CryptopiaException("[ping](getbalance of btc in fact) failed", ex);
    }
  }

  @Override
  public Market getMarket(final String symbol) {

    try {
      final String action = String.format("GetMarket/%s", symbol);
      Response response = simpleRequest(action);
      return Market.parse(response.body().string());
    } catch (IOException ex) {
      throw new CryptopiaException("failed read response while getMarket.", ex);
    }

  }

  @Override
  public List<Balance> getAllBalances() {
    String currency = null;
    try {
      JsonObject params = new JsonObject();
      params.addProperty("Currency", currency);

      Response response = request("GetBalance", params.toString());
      return Balance.parse(response.body().string());
    } catch (IOException ex) {
      throw new CryptopiaException("failed read response while GetBalance.", ex);
    }
  }

  @Override
  public Balance getBalance(String currency) {

    try {
      JsonObject params = new JsonObject();
      params.addProperty("Currency", currency);

      Response response = request("GetBalance", params.toString());

      List<Balance> balances = Balance.parse(response.body().string());
      if (CollectionUtils.isNotEmpty(balances)) {
        return balances.get(0);
      }
    } catch (IOException ex) {
      throw new CryptopiaException("failed read response while GetBalance.", ex);
    }

    return null;
  }

  @Override
  public Trade submitTrade(TradeRequest request) {

    final String action = "SubmitTrade";

    final JsonObject params = new JsonObject();
    if (request.getMarket() != null) {
      params.addProperty("Market", request.getMarket());
    }
    if ((request.getMarket() == null || request.getMarket().length() == 0)
        && request.getTradePairId() != null) {
      params.addProperty("TradePairId", request.getTradePairId());
    }
    params.addProperty("TradeType", request.getType().getLabel());
    params.addProperty("Rate", request.getRate());
    params.addProperty("Amount", request.getAmount());

    try {
      Response response = request(action, params.toString());
      return Trade.parse(response.body().string());
    } catch (IOException ex) {
      throw new CryptopiaException("[submitTrade] failed.", ex);
    }
  }

  @Override
  public List<TradeDetail> getTradeHistory(String symbol, String count) {
    final String action = "GetTradeHistory";
    final JsonObject params = new JsonObject();
    params.addProperty("Market", symbol);
    params.addProperty("Count", count);

    try {
      Response response = request(action, params.toString());

      return TradeDetail.parse(response.body().string());
    } catch (IOException ex) {
      throw new CryptopiaException("[GetTradeHistory] failed", ex);
    }
  }


  /**
   * for private api
   * @return http response instance
   */
  private Response request(String action, String paramStr) throws IOException {

    final String uri = String.format("%s%s", ApiConstants.API_BASE_URL, action);

    Request request = new Request.Builder()
        .headers(buildHeaders(uri, paramStr))
        .url(uri)
        .post(RequestBody.create(MediaType.parse(ApiConstants.CONTENT_TYPE_APPLICATION_JSON), paramStr))
        .build();

    return httpClient.newCall(request).execute();
  }

  /**
   * for public api
   * @return http response instance
   */
  private Response simpleRequest(final String action) throws IOException {

    final String uri = String.format("%s%s", ApiConstants.API_BASE_URL, action);
    Request request = new Request.Builder()
        // .addHeader(ApiConstants.HEADER_CONTENT_TYPE, ApiConstants.CONTENT_TYPE_APPLICATION_JSON)
        .url(uri)
        .build();

    return httpClient.newCall(request).execute();
  }

  private Headers buildHeaders(final String uri, String paramStr) {

    Headers headers = new Headers.Builder()
        // .add(ApiConstants.HEADER_CONTENT_TYPE, ApiConstants.CONTENT_TYPE_APPLICATION_JSON)
        .add(ApiConstants.HEADER_AUTHORIZATION, getAuthString(uri, paramStr))
        .build();

    return headers;
  }

  private String getAuthString(String uri, String postParam) {
    try {
      final String nonce = String.valueOf(System.currentTimeMillis());

      final StringBuilder requestSignature = new StringBuilder();
      requestSignature.append(this.apiKey)
          .append("POST")
          .append(URLEncoder.encode(uri, StandardCharsets.UTF_8.toString()).toLowerCase())
          .append(nonce)
          .append(getMd5B64String(postParam));

      final StringBuilder auth = new StringBuilder();
      auth.append(ApiConstants.AUTHENTICATION_SCHEMA)
          .append(this.apiKey)
          .append(":")
          .append(getHmacSha256B64String(requestSignature.toString()))
          .append(":")
          .append(nonce);

      return auth.toString();

    } catch (UnsupportedEncodingException e) {
      throw new CryptopiaException("Unsupport encoding exception.", e);
    } catch (NoSuchAlgorithmException e) {
      throw new CryptopiaException("no such algorithm exception.", e);
    } catch (InvalidKeyException e) {
      throw new CryptopiaException("invalid key exception.", e);
    }
  }

  private String getMd5B64String(String postParameter)
      throws NoSuchAlgorithmException, UnsupportedEncodingException {

    MessageDigest md5Digest = MessageDigest.getInstance(ApiConstants.HASH_ALGORITHM_MD5);
    byte[] digestBytes = md5Digest.digest(postParameter.getBytes(ApiConstants.UTF_8));
    return Base64.getEncoder().encodeToString(digestBytes);
  }

  private String getHmacSha256B64String(String msg)
      throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {

    Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
    SecretKeySpec secretSpec = new SecretKeySpec(
        Base64.getDecoder().decode(this.secretKey), ApiConstants.SIGN_ALGORITHM_HMAC_SHA256);

    hmacSHA256.init(secretSpec);
    return Base64.getEncoder().encodeToString(hmacSHA256.doFinal(msg.getBytes(ApiConstants.UTF_8)));
  }
}
