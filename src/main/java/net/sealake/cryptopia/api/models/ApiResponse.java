package net.sealake.cryptopia.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.sealake.cryptopia.api.client.CryptopiaException;

@Data
@NoArgsConstructor
public class ApiResponse<T> {
  private boolean success;
  private String message;
  private T data;
  private String json;

  public void validate() {
    if (success) {
      return;
    }
    throw new CryptopiaException(message);
  }
}
