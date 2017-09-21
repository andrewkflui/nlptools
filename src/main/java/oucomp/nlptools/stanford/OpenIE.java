package oucomp.nlptools.stanford;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class OpenIE {

  private final StanfordCoreNLP pipeline;

  public OpenIE() {
    // creates a StanfordCoreNLP object
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, depparse, natlog, openie");
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
      System.out.printf("      [%s] POS(%s) \n", word, pos);
    }
    return list;
  }

  public Collection<RelationTriple> getRelations(CoreMap sentence) {
    Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
    for (RelationTriple rel : triples) {
      //printRelationTriple(rel);
    }
    return triples;
  }

  public SemanticGraph getSemanticGraph(CoreMap sentence) {
    SemanticGraph graph = sentence.get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class);
    //System.out.println(graph.toString(SemanticGraph.OutputFormat.LIST));
    return graph;
  }

  public void processSemanticGraph(SemanticGraph graph) {
    System.out.println("Number of relatons: " + graph.edgeCount());
    List<SemanticGraphEdge> edgeList = graph.edgeListSorted();
    for (SemanticGraphEdge rel : edgeList) {
      System.out.printf("Relation (%s) gov:%s dep:%s\n", rel.getRelation().getLongName(), rel.getGovernor(), rel.getDependent());
    }
  }

  public void printRelationTripleList(Collection<RelationTriple> relationList) {
    if (relationList != null) {
      for (RelationTriple rel : relationList) {
        printRelationTriple(rel);
      }
    }
  }

  public void printRelationTriple(RelationTriple rel) {
    System.out.printf("[%.2f] (%s, %s) Subject: %s, Object: %s\n", rel.confidence, rel.relationGloss(), rel.relationHead(),
            rel.subjectGloss(), rel.objectGloss());
  }

  public void demo(String text) {
    Annotation annotatedDocument = this.annotate(text);
    List<CoreMap> sentences = this.getSentences(annotatedDocument);

    for (CoreMap sentence : sentences) {
      System.out.println("\n== SENTENCE: " + sentence);
      System.out.println("\n\n==== TOKENS:");
      this.getTokens(sentence);
      System.out.println("\n\n==== SEMANTIC GRAPH:");
      SemanticGraph graph = this.getSemanticGraph(sentence);
      processSemanticGraph(graph);
      System.out.println("\n\n==== OPENIE RELATION:");
      Collection<RelationTriple> relationList = this.getRelations(sentence);
      printRelationTripleList(relationList);
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
    text = "Obama was born in Hawaii. He is our president.";
    demo(text);
  }

  public static void main(String args[]) throws Exception {
    System.out.println("\nStanford OpenIE Extraction Demo");
    OpenIE sen = new OpenIE();

    sen.demoTestCase();
    //sen.demoInteractive();
  }
}
