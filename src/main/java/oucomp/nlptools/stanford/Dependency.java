package oucomp.nlptools.stanford;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Dependency {

  private final StanfordCoreNLP pipeline;

  private final GrammaticalStructureFactory gsf;

  public Dependency() {
    // creates a StanfordCoreNLP object
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
    pipeline = new StanfordCoreNLP(props);
    // for converting parse trees into grammatical structures
    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
    gsf = tlp.grammaticalStructureFactory();
  }

  public Annotation annotate(String text) {
    Annotation document = new Annotation(text);
    pipeline.annotate(document);
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
      String lemma = token.get(LemmaAnnotation.class);
      System.out.printf("      [%s] POS(%s) NE(%s) LEMMA(%s)\n", word, pos, ne, lemma);
    }
    return list;
  }

  public Tree getParseTree(CoreMap sentence) {
    Tree tree = sentence.get(TreeAnnotation.class);
    System.out.println(tree);
    return tree;
  }

  public SemanticGraph getDependency(CoreMap sentence) {
    SemanticGraph graph = sentence.get(BasicDependenciesAnnotation.class);
    System.out.println(graph);
    return graph;
  }

  public void processSemanticGraph(SemanticGraph graph) {
    System.out.println("Number of relatons: " + graph.edgeCount());
    List<SemanticGraphEdge> edgeList = graph.edgeListSorted();
    for (SemanticGraphEdge rel : edgeList) {
      System.out.printf("Relation (%s) gov:%s dep:%s\n", rel.getRelation().getLongName(), rel.getGovernor(), rel.getDependent());
    }
  }

  public Collection<TypedDependency> getTypedDependencyList(Tree tree) {
    GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
    Collection<TypedDependency> tdlist = gs.typedDependenciesCCprocessed();
    //System.out.println(tdlist);
    printTypedDependencyList(tdlist);
    return tdlist;
  }

  public void printTypedDependencyList(Collection<TypedDependency> tdlist) {
    for (TypedDependency td : tdlist) {
      System.out.printf("Relation (%s) gov:%s dep:%s\n", td.reln().getLongName(), td.gov(), td.dep());
    }
  }

  public void demo(String text) {
    Annotation annotatedDocument = this.annotate(text);
    List<CoreMap> sentences = this.getSentences(annotatedDocument);

    for (CoreMap sentence : sentences) {
      System.out.println("== SENTENCE: " + sentence);
      System.out.println("\n\n==== TOKENS:");
      this.getTokens(sentence);
      System.out.println("\n\n==== PARSE TREE:");
      Tree tree = this.getParseTree(sentence);
      System.out.println("\n\n==== DEPENDENCIES:");
      this.getDependency(sentence);
      System.out.println("\n\n==== TYPED DEPENDENCIES:");
      this.getTypedDependencyList(tree);
    }

  }

  public void demoInteractive() {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter line: ");
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      demo(line);
      System.out.print("Enter line: ");
    }
  }

  public void demoTestCase() {
    String text = "Bills on ports and immigration were not submitted by Senator Brownback, Republican of Kansas";
    text = "I eat sandwich every morning";
    demo(text);
  }

  public static void main(String args[]) throws Exception {
    System.out.println("\nStanford Dependencies Demo");
    Dependency dep = new Dependency();

    dep.demoTestCase();
  }
}
