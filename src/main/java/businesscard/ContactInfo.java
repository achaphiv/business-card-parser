package businesscard;

public final class ContactInfo {
  private final Name name;
  private final PhoneNumber phoneNumber;
  private final EmailAddress emailAddress;

  public ContactInfo(Name name, PhoneNumber phoneNumber, EmailAddress emailAddress) {
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.emailAddress = emailAddress;
  }

  public Name name() {
    return name;
  }

  public PhoneNumber phoneNumber() {
    return phoneNumber;
  }

  public EmailAddress emailAddress() {
    return emailAddress;
  }
}
