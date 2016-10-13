package businesscard.parser.impl;

import businesscard.ContactInfo;
import businesscard.EmailAddress;
import businesscard.Name;
import businesscard.PhoneNumber;
import businesscard.parser.BusinessCardParser;

public final class BusinessCardParserImpl implements BusinessCardParser {
  @Override
  public ContactInfo getContactInfo(String document) {
    return new ContactInfo(
        new Name(""),
        new PhoneNumber(""),
        new EmailAddress("")
    );
  }
}
