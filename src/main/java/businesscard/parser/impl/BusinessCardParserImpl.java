package businesscard.parser.impl;

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

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
  private final Pattern newLine = Pattern.compile("\r?\n");
  private final Pattern email = Pattern.compile("[.\\w]++@[.\\w]++");

  public BusinessCardParserImpl(Tokenizer tokenizer, TokenNameFinder names) {
    this.tokenizer = tokenizer;
    this.nameFinder = names;
  }

  @Override
  public ContactInfo getContactInfo(String document) {
    return new ContactInfo(
        new Name(mostLikelyName(document).orElse("")),
        new PhoneNumber(""),
        new EmailAddress(onlyMatch(email, document).orElse(""))
    );
  }

  private Optional<String> mostLikelyName(String document) {
    Queue<PossibleName> all = possibleNames(document);
    return !all.isEmpty() ? Optional.of(all.peek().value) : Optional.empty();
  }

  /**
   * Ordered by most likely to least likely.
   */
  private Queue<PossibleName> possibleNames(String document) {
    Queue<PossibleName> found = new PriorityQueue<>();
    for (String sentence : newLine.split(document)) {
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
