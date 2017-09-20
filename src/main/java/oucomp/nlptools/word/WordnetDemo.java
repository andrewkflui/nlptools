package oucomp.nlptools.word;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class WordnetDemo {

  private static final String WORDNET_DICT_PATH = "../../lib/WordNet-3.0/dict";
  private IDictionary dict;

  public WordnetDemo() throws IOException {
    URL url = new URL("file", null, WORDNET_DICT_PATH);
    dict = new Dictionary(url);
    dict.open();
  }

  public void processIWord(IWord word) {
    System.out.println("    Lemma = " + word.getLemma());
    System.out.println("    Gloss = " + word.getSynset().getGloss());
    System.out.println("    Synset");
    ISynset sset = word.getSynset();
    List<IWord> wordList = sset.getWords();
    for (IWord iword : wordList) {
      System.out.println("       " + iword.getLemma() + " : " + iword.getSynset().getGloss());
    }
  }

  public void processIDXWord(IIndexWord idxWord) {
    System.out.println("Tag Sense #: " + idxWord.getTagSenseCount());
    int count = 1;
    List<IWordID> wordIDList = idxWord.getWordIDs();
    for (IWordID wordID : wordIDList) {
      System.out.println("  Sense #" + count++);
      //System.out.println("Word ID: " + wordID);
      //System.out.println("Synset ID: " + wordID.getSynsetID());
      IWord word = dict.getWord(wordID);
      processIWord(word);
    }
  }

  public void checkWord(String word) {
    POS[] posarray = POS.values();

    for (POS pos : posarray) {
      System.out.printf("\n=== Check the %s POS of the word '%s'\n", pos.toString(), word);
      IIndexWord idxWord = dict.getIndexWord(word, pos);
      if (idxWord != null) {
        processIDXWord(idxWord);
      }
    }
  }
  
  public void demo() {
    String[] wordarray = {"car", "given", "connect", "test", "bank"};
    for (String word: wordarray) {
      checkWord(word);
    }
  }

  public static void main(String args[]) throws Exception {
    System.out.println("Wordnet Demo");
    System.out.println("Loading ...");
    WordnetDemo wordnet = new WordnetDemo();
    
    wordnet.demo();
    
    System.out.print("\nEnter a word: ");

    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      wordnet.checkWord(line);

      System.out.print("\nEnter a word: ");
    }

  }
}
