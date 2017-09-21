package oucomp.nlptools.stanford;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Sentiment {

  private final StanfordCoreNLP pipeline;

  public Sentiment() {
    // creates a StanfordCoreNLP object
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, sentiment");
    pipeline = new StanfordCoreNLP(props);
  }

  public Annotation annotate(String text) {
    Annotation document = new Annotation(text);
    pipeline.annotate(document);
    pipeline.prettyPrint(document, System.out);
    return document;
  }

  public List<CoreMap> getSentences(Annotation annotatedDoc) {
    List<CoreMap> sentences = annotatedDoc.get(CoreAnnotations.SentencesAnnotation.class);
    return sentences;
  }

  public List<CoreLabel> getTokens(CoreMap sentence) {
    List<CoreLabel> list = sentence.get(TokensAnnotation.class);
    for (CoreLabel token : list) {
      String word = token.get(TextAnnotation.class);
      String pos = token.get(PartOfSpeechAnnotation.class);
      String ne = token.get(NamedEntityTagAnnotation.class);
      String sentiment = token.get(SentimentCoreAnnotations.SentimentClass.class);
      System.out.printf("      [%s] POS(%s) NE(%s) SENTIMENT (%s)\n", word, pos, ne, sentiment);
    }
    return list;
  }

  public String getSentiment(CoreMap sentence) {
    String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
    System.out.println("      " + sentiment);
    return sentiment;
  }


  public void demo(String text) {
    Annotation annotatedDocument = this.annotate(text);
    List<CoreMap> sentences = this.getSentences(annotatedDocument);

    for (CoreMap sentence : sentences) {
      System.out.println("== SENTENCE: " + sentence);
      System.out.println("\n\n==== TOKENS:");
      this.getTokens(sentence);
      System.out.println("\n\n==== SENTIMENT:");
      String sentiment = this.getSentiment(sentence);
    }

  }

  public void demoInteractive() {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Enter line: ");

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      demo(line);
      System.out.print("\n--------\nEnter line: ");
    }
  }

  public void demoTestCase() {
    String text = "Bills on ports and immigration were not submitted by Senator Brownback, Republican of Kansas";
    demo(text);
  }

  public static void main(String args[]) throws Exception {
    System.out.println("\nStanford Sentiment Demo");
    Sentiment sen = new Sentiment();
    
    sen.demoTestCase();
    sen.demoInteractive();
  }
}
