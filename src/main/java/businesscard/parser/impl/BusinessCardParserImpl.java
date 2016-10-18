package businesscard.parser.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import businesscard.ContactInfo;
import businesscard.EmailAddress;
import businesscard.Name;
import businesscard.PhoneNumber;
import businesscard.parser.BusinessCardParser;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

public final class BusinessCardParserImpl implements BusinessCardParser {
  private final Tokenizer tokenizer;
  private final TokenNameFinder nameFinder;
  private final PhoneNumberUtil phoneNumberFinder;
  private final Pattern newLine = Pattern.compile("\r?\n");
  private final Pattern email = Pattern.compile("[.\\w]++@[.\\w]++");

  public BusinessCardParserImpl(
      Tokenizer tokenizer,
      TokenNameFinder nameFinder,
      PhoneNumberUtil phoneNumberFinder
  ) {
    this.tokenizer = tokenizer;
    this.nameFinder = nameFinder;
    this.phoneNumberFinder = phoneNumberFinder;
  }

  @Override
  public ContactInfo getContactInfo(String document) {
    String[] lines = newLine.split(document);
    return new ContactInfo(
        new Name(mostLikelyName(lines).orElse("")),
        new PhoneNumber(mostLikelyPhoneNumber(lines).orElse("")),
        new EmailAddress(onlyMatch(email, document).orElse(""))
    );
  }

  private Optional<String> mostLikelyName(String[] lines) {
    Queue<PossibleName> all = possibleNames(lines);
    return !all.isEmpty() ? Optional.of(all.peek().value) : Optional.empty();
  }

  /**
   * Ordered by most likely to least likely.
   */
  private Queue<PossibleName> possibleNames(String[] lines) {
    Queue<PossibleName> found = new PriorityQueue<>();
    for (String sentence : lines) {
      String[] foundTokens = tokenizer.tokenize(sentence);
      Span[] spans = nameFinder.find(foundTokens);
      for (Span span : spans) {
        PossibleName maybe = new PossibleName(
            span.getProb(),
            Joiner.on(" ").join(Span.spansToStrings(spans, foundTokens))
        );
        found.add(maybe);
      }
    }
    return found;
  }

  private static final class PossibleName implements Comparable<PossibleName> {
    private final double probability;
    final String value;

    PossibleName(double probability, String value) {
      this.probability = probability;
      this.value = value;
    }

    @Override
    public int compareTo(PossibleName o) {
      return ComparisonChain.start()
          // Larger probability first
          .compare(probability, o.probability, Ordering.natural().reverse())
          .compare(value, o.value)
          .result();
    }
  }

  private Optional<String> mostLikelyPhoneNumber(String[] lines) {
    List<PossiblePhoneNumber> all = possiblePhoneNumbers(lines);
    if (all.size() == 1) {
      return Optional.of(all.get(0).value);
    } else if (all.size() >= 2) {
      List<PossiblePhoneNumber> filtered = all.stream().filter(
          match -> match.wholeLine.matches("(Tel|Phone):.*+")
      ).collect(Collectors.toList());
      if (filtered.size() == 1) {
        return Optional.of(filtered.get(0).value);
      }
    }
    return Optional.empty();
  }

  private List<PossiblePhoneNumber> possiblePhoneNumbers(String[] lines) {
    List<PossiblePhoneNumber> all = new ArrayList<>();
    for (String line : lines) {
      for (PhoneNumberMatch match : phoneNumberFinder.findNumbers(line, "US")) {
        all.add(new PossiblePhoneNumber(line, match.rawString()));
      }
    }
    return all;
  }

  private static final class PossiblePhoneNumber {
    final String wholeLine;
    final String value;

    PossiblePhoneNumber(String wholeLine, String value) {
      this.wholeLine = wholeLine;
      this.value = value;
    }
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
