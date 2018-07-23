package net.sealake.cryptopia.api.models.enums;

import lombok.Getter;

@Getter
public enum TradeType {

  BUY("Buy"),
  SELL("Sell");

  private final String label;

  private TradeType(String label) {
    this.label = label;
  }

  public static TradeType byLabel(String label) {
    for (TradeType st : values()) {
      if (st.label.equals(label)) {
        return st;
      }
    }
    return null;
  }
}