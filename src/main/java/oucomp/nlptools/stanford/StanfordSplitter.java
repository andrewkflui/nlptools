package oucomp.nlptools.stanford;



import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class StanfordSplitter {

  public static List<String> splitSentence(String doc) {
    List<String> sentenceList = new LinkedList<String>();
    Reader reader = new StringReader(doc);
    DocumentPreprocessor dp = new DocumentPreprocessor(reader);
    Iterator<List<HasWord>> it = dp.iterator();
    while (it.hasNext()) {
      StringBuilder sentenceSb = new StringBuilder();
      List<HasWord> sentence = it.next();
      for (HasWord token : sentence) {
        if (sentenceSb.length() > 1) {
          sentenceSb.append(" ");
        }
        sentenceSb.append(token);
      }
      sentenceList.add(sentenceSb.toString());
    }
    return sentenceList;
  }

  public static List<String> splitWord(String doc) {
    return splitWord(doc, true);
  }

  public static List<String> splitWord(String doc, boolean includePunctuation) {
    List<String> wordList = new LinkedList<String>();
    Reader reader = new StringReader(doc);
    DocumentPreprocessor dp = new DocumentPreprocessor(reader);
    Iterator<List<HasWord>> it = dp.iterator();
    while (it.hasNext()) {
      List<HasWord> sentence = it.next();
      for (HasWord token : sentence) {
        //System.out.println("Token: " + token + " " + includePunctuation);
        if (!includePunctuation && !Character.isLetterOrDigit(token.word().charAt(0))) {
          //System.out.println("true");
        } else {
          wordList.add(token.word());
        }
      }
    }
    return wordList;
  }

  public static void main(String args[]) throws Exception {
    String text = "My first sentence, and appositive. My second sentence.";
    List<String> sentenceList = splitWord(text, false);
    for (String s : sentenceList) {
      System.out.println(s);
    }
  }
}
