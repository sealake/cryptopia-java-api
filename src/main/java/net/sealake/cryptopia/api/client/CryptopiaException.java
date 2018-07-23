package net.sealake.cryptopia.api.client;

public class CryptopiaException extends RuntimeException {

  public CryptopiaException(String message) {
    super(message);
  }

  public CryptopiaException(String message, Throwable cause) {
    super(message, cause);
  }
}