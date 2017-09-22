package oucomp.nlptools.stanford;


import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class Coreference {

  private final StanfordCoreNLP pipeline;

  public Coreference() {
    // creates a StanfordCoreNLP object
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, mention, dcoref");
    pipeline = new StanfordCoreNLP(props);
  }

  public Annotation annotate(String text) {
    Annotation document = new Annotation(text);
    pipeline.annotate(document);
    pipeline.prettyPrint(document, System.out);
    System.out.println("---");

    Map<Integer, CorefChain> corefMap = document.get(CorefCoreAnnotations.CorefChainAnnotation.class);

    return document;
  }

  public List<CoreMap> getSentences(Annotation annotatedDoc) {
    List<CoreMap> sentences = annotatedDoc.get(CoreAnnotations.SentencesAnnotation.class);

    return sentences;
  }

  public Collection<CorefChain> getCorefChainList(Annotation annotatedDoc) {
    Map<Integer, CorefChain> corefMap = annotatedDoc.get(CorefCoreAnnotations.CorefChainAnnotation.class);
    if (corefMap == null) {
      System.out.println("CorefMap is NULL");
      return null;
    }
    Collection<CorefChain> chains = corefMap.values();
    return chains;
  }

  public void printCorefChainList(Collection<CorefChain> chains) {
    if (chains == null) {
      return;
    }
    for (CorefChain c : chains) {
      System.out.println(c);
    }
  }

  public List<CorefMention> getMentionList(CorefChain chain) {
    List<CorefMention> mentionList = chain.getMentionsInTextualOrder();
    return mentionList;
  }

  public void printMentionList(List<CorefMention> mentions) {
    if (mentions == null) {
      return;
    }
    for (CorefMention cm : mentions) {
      System.out.println(cm);
    }
  }

  public List<CoreLabel> getTokens(CoreMap sentence) {
    List<CoreLabel> list = sentence.get(TokensAnnotation.class);
    for (CoreLabel token : list) {
      String word = token.get(TextAnnotation.class);
      String pos = token.get(PartOfSpeechAnnotation.class);
      String ne = token.get(NamedEntityTagAnnotation.class);
      System.out.printf("      [%s] POS(%s) NE(%s) \n", word, pos, ne);
    }
    return list;
  }

  public void demo(String text) {
    Annotation annotatedDocument = this.annotate(text);
    System.out.println("=== CORE COREF CHAIN");
    Collection<CorefChain> chainList = this.getCorefChainList(annotatedDocument);
    printCorefChainList(chainList);
    if (chainList != null) {
      System.out.println("=== MENTIONS OF COREF CHAIN");
      for (CorefChain chain : chainList) {
        List<CorefMention> mentionList = getMentionList(chain);
        printMentionList(mentionList);
      }
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
    String text = "Barack Obama was born in Hawaii.  He is the president. Obama was elected in 2008.";
    demo(text);
  }

  public static void main(String args[]) throws Exception {
    System.out.println("\nStanford Relation Extraction Demo");
    Coreference sen = new Coreference();

    sen.demoTestCase();
    //sen.demoInteractive();
  }
}
