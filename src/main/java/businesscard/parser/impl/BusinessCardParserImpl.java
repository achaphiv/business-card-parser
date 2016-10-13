package businesscard.parser.impl;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import businesscard.ContactInfo;
import businesscard.EmailAddress;
import businesscard.Name;
import businesscard.PhoneNumber;
import businesscard.parser.BusinessCardParser;

public final class BusinessCardParserImpl implements BusinessCardParser {
  private final Pattern email = Pattern.compile("[.\\w]++@[.\\w]++");

  @Override
  public ContactInfo getContactInfo(String document) {
    return new ContactInfo(
        new Name(""),
        new PhoneNumber(""),
        new EmailAddress(onlyMatch(email, document).orElse(""))
    );
  }

  private static Optional<String> onlyMatch(Pattern regex, String text) {
    Matcher m = regex.matcher(text);
    if (m.find()) {
      String match = m.group();
      if (!m.find()) {
        return Optional.of(match);
      }
    }
    return Optional.empty();
  }
}
