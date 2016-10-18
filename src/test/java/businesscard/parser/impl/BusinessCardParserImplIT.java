package businesscard.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import businesscard.ContactInfo;
import businesscard.EmailAddress;
import businesscard.Name;
import businesscard.PhoneNumber;
import businesscard.parser.BusinessCardParser;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

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

  @ClassRule
  public static final TemporaryFolder tmpDir = new TemporaryFolder();

  private static File tokensModel;
  private static File personModel;

  @BeforeClass
  public static void retrieveModels() throws Exception {
    tokensModel = tmpDir.newFile();
    personModel = tmpDir.newFile();
    model("en-token.bin").copyTo(Files.asByteSink(tokensModel));
    model("en-ner-person.bin").copyTo(Files.asByteSink(personModel));
  }

  private static ByteSource model(String name) throws Exception {
    return Resources.asByteSource(
        new URL("http://opennlp.sourceforge.net/models-1.5/" + name)
    );
  }

  private final String input;
  private final ContactInfo expected;

  public BusinessCardParserImplIT(String input, ContactInfo expected) {
    this.input = input;
    this.expected = expected;
  }

  @Test
  public void correctName() throws Exception {
    assertThat(actual().name()).isEqualTo(expected.name());
  }

  @Test
  public void correctPhoneNumber() throws Exception {
    assertThat(actual().phoneNumber()).isEqualTo(expected.phoneNumber());
  }

  @Test
  public void correctEmailAddress() throws Exception {
    assertThat(actual().emailAddress()).isEqualTo(expected.emailAddress());
  }

  private ContactInfo actual() throws Exception {
    BusinessCardParser toTest = new BusinessCardParserImpl(
        new TokenizerME(new TokenizerModel(tokensModel)),
        new NameFinderME(new TokenNameFinderModel(personModel))
    );
    return toTest.getContactInfo(input);
  }
}
