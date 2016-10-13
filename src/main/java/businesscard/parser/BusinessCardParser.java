package businesscard.parser;

import businesscard.ContactInfo;

public interface BusinessCardParser {
  /**
   * Determine most likely {@link ContactInfo} from business card text.
   * <p>
   * E.G.
   *
   * <pre>
   * {@code
   * ASYMMETRIK LTD
   * Mike Smith
   * Senior Software Engineer
   * (410)555-1234
   * msmith@asymmetrik.com
   * }
   * </pre>
   * 
   * ==>
   * 
   * <pre>
   * {@code
   * Name: Mike Smith
   * Phone: 4105551234
   * Email: msmith@asymmetrik.com
   * }
   * </pre>
   */
  ContactInfo getContactInfo(String document);
}
