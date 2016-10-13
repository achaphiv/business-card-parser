package businesscard.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import businesscard.ContactInfo;
import businesscard.EmailAddress;
import businesscard.Name;
import businesscard.PhoneNumber;

@RunWith(Parameterized.class)
public final class BusinessCardParserImplIT {
  @Parameters
  public static Collection<Object[]> testCases() {
    Object[][] all = {
      {
        String.join(
            "\n",
            "ASYMMETRIK LTD",
            "Mike Smith",
            "Senior Software Engineer",
            "(410)555-1234",
            "msmith@asymmetrik.com"
        ),
        new ContactInfo(
            new Name("Mike Smith"),
            new PhoneNumber("(410)555-1234"),
            new EmailAddress("msmith@asymmetrik.com")
        )
      },
      {
        String.join(
            "\n",
            "Foobar Technologies",
            "Analytic Developer",
            "Lisa Haung",
            "1234 Sentry Road",
            "Columbia, MD 12345",
            "Phone: 410-555-1234",
            "Fax: 410-555-4321",
            "lisa.haung@foobartech.com"
        ),
        new ContactInfo(
            new Name("Lisa Haung"),
            new PhoneNumber("410-555-1234"),
            new EmailAddress("lisa.haung@foobartech.com")
        )
      },
      {
        String.join(
            "\n",
            "Arthur Wilson",
            "Software Engineer",
            "Decision & Security Technologies",
            "ABC Technologies",
            "123 North 11th Street",
            "Suite 229",
            "Arlington, VA 22209",
            "Tel: +1 (703) 555-1259",
            "Fax: +1 (703) 555-1200",
            "awilson@abctech.com"
        ),
        new ContactInfo(
            new Name("Arthur Wilson"),
            new PhoneNumber("+1 (703) 555-1259"),
            new EmailAddress("awilson@abctech.com")
        )
      },
    };

    return Arrays.asList(all);
  }

  private final String input;
  private final ContactInfo expected;

  public BusinessCardParserImplIT(String input, ContactInfo expected) {
    this.input = input;
    this.expected = expected;
  }

  @Test
  public void correctName() {
    assertThat(actual().name()).isEqualTo(expected.name());
  }

  @Test
  public void correctPhoneNumber() {
    assertThat(actual().phoneNumber()).isEqualTo(expected.phoneNumber());
  }

  @Test
  public void correctEmailAddress() {
    assertThat(actual().emailAddress()).isEqualTo(expected.emailAddress());
  }

  private ContactInfo actual() {
    return new BusinessCardParserImpl().getContactInfo(input);
  }
}
