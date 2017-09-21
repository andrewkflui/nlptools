package oucomp.nlptools.stanford;

import edu.stanford.nlp.ie.machinereading.structure.EntityMention;
import edu.stanford.nlp.ie.machinereading.structure.ExtractionObject;
import edu.stanford.nlp.ie.machinereading.structure.MachineReadingAnnotations;
import edu.stanford.nlp.ie.machinereading.structure.RelationMention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.util.CoreMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * **
 *
 * The default Stanford model is trained to produce four relation types Live_In,
 * Located_In, OrgBased_In, Work_For, and None
 *
 * Check out the following for new models
 *
 * https://nlp.stanford.edu/software/relationExtractor.html
 *
 */
public class RelationExtractor {

  private final StanfordCoreNLP pipeline;

  private final GrammaticalStructureFactory gsf;

  public RelationExtractor() {
    // creates a StanfordCoreNLP object
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, relation");
    pipeline = new StanfordCoreNLP(props);
    // for converting parse trees into grammatical structures
    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
    gsf = tlp.grammaticalStructureFactory();
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
      System.out.printf("      [%s] POS(%s) NE(%s) \n", word, pos, ne);
    }
    return list;
  }

  public List<EntityMention> getEntityMentions(CoreMap sentence) {
    List<EntityMention> emList = sentence.get(MachineReadingAnnotations.EntityMentionsAnnotation.class);
    for (EntityMention em : emList) {
      System.out.printf("EntityMention: '%s' [%s]\n", em.getValue(), em.getType());
    }
    return emList;
  }

  public List<RelationMention> getRelation(CoreMap sentence) {
    List<RelationMention> relations = sentence.get(MachineReadingAnnotations.RelationMentionsAnnotation.class);
    return relations;
  }

  public void printRelationList(List<RelationMention> relationList) {
    if (relationList != null) {
      for (RelationMention r : relationList) {
        if (!r.getType().equals(RelationMention.UNRELATED)) {
          //System.out.println("---");
          //System.out.println(r);
          printRelation(r);
        }
      }
    }
  }

  public void printRelation(RelationMention r) {
    String type = r.getType();
    Counter<String> typeProbList = r.getTypeProbabilities();
    String value = r.getValue();
    System.out.printf("\nRelationMention [%s(%f)] %s\n", type, typeProbList.getCount(type), value);

    List<ExtractionObject> extList = r.getArgs();
    if (extList != null) {
      for (ExtractionObject e : extList) {
        System.out.println("EXTOBJ: " + e);
      }
    }

    List<EntityMention> emList = r.getEntityMentionArgs();
    for (EntityMention em : emList) {
      System.out.printf("EntityMention: '%s' [%s]\n", em.getValue(), em.getType());
    }
  }

  public void demo(String text) {
    Annotation annotatedDocument = this.annotate(text);
    List<CoreMap> sentences = this.getSentences(annotatedDocument);

    for (CoreMap sentence : sentences) {
      System.out.println("== SENTENCE: " + sentence);
      System.out.println("\n\n==== TOKENS:");
      this.getTokens(sentence);
      System.out.println("\n\n==== ENTITY MENTIONS:");
      getEntityMentions(sentence);
      System.out.println("\n\n==== RELATION:");
      List<RelationMention> relationList = this.getRelation(sentence);
      printRelationList(relationList);
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
    System.out.println("\nStanford Relation Extraction Demo");
    RelationExtractor sen = new RelationExtractor();

    sen.demoTestCase();
    //sen.demoInteractive();
  }
}
