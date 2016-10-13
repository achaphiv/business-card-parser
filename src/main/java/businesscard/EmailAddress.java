package businesscard;

import java.util.Objects;

public class EmailAddress {
  private final String value;

  public EmailAddress(String value) {
    this.value = value;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (!(o instanceof EmailAddress)) {
      return false;
    }
    EmailAddress that = (EmailAddress) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public String toString() {
    return value;
  }
}
