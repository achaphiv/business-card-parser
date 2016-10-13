package businesscard;

import java.util.Objects;

public final class PhoneNumber {
  private final String value;

  public PhoneNumber(String value) {
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
    } else if (!(o instanceof PhoneNumber)) {
      return false;
    }
    PhoneNumber that = (PhoneNumber) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public String toString() {
    return value;
  }
}
