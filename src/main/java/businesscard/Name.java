package businesscard;

import java.util.Objects;

public final class Name {
  private final String value;

  public Name(String value) {
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
    } else if (!(o instanceof Name)) {
      return false;
    }
    Name that = (Name) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public String toString() {
    return value;
  }
}
