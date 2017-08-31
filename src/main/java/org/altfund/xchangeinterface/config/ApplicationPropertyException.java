package org.altfund.xchangeinterface.config;

public class ApplicationPropertyException extends RuntimeException {

  private final String propertyName;

  public ApplicationPropertyException(String propertyName) {
    this(propertyName, null);
  }

  public ApplicationPropertyException(String propertyName, Throwable cause) {
    super(cause);
    this.propertyName = propertyName;
  }

  @Override
  public String getMessage() {
    return "Application property " + propertyName + " is missing or invalid";
  }

}
