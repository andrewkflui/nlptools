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
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Dependency {

  private StanfordCoreNLP pipeline;

  private GrammaticalStructureFactory gsf;

  public Dependency() {
    // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
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
    Tree tree = sentence.get(TreeAnnotation.class);
    System.out.println(tree);
    return list;
  }

  public Tree getParseTree(CoreLabel sentence) {
    Tree tree = sentence.get(TreeAnnotation.class);
    System.out.println(tree);
    return tree;
  }

  public SemanticGraph getDependency(CoreLabel sentence) {
    SemanticGraph dependencies = sentence.get(BasicDependenciesAnnotation.class);
    System.out.println(dependencies);
    return dependencies;
  }

  public Collection<TypedDependency> getTypedDependencyList(Tree tree) {
    GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
    Collection<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
    System.out.println(tdl);
    return tdl;
  }

  public static void main(String args[]) throws Exception {
    System.setIn(new ByteArrayInputStream("Bills on ports and immigration were not submitted by Senator Brownback, Republican of Kansas".getBytes()));

    Dependency dep = new Dependency();
    System.out.println("Stanford Dependencies Demo");
    System.out.println("Loading ...");
    System.out.print("Enter line: ");

    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();

      Annotation sentence = dep.annotate(line);
      System.out.println("\n\n==== TOKENS:");
      dep.getTokens(sentence);
      System.out.println("\n\n==== PARSE TREE:");
      Tree tree = dep.getParseTree(sentence);
      System.out.println("\n\n==== DEPENDENCIES:");
      dep.getDependency(sentence);
      System.out.println("\n\n==== TYPED DEPENDENCIES:");
      dep.getTypedDependencyList(tree);

      System.out.print("Enter line: ");
    }
  }
}
