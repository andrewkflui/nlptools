/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oucomp.nlptools.spellcheck;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;

public class LanguageTool {

  private Language lang = new BritishEnglish();
  private JLanguageTool langTool;

  public LanguageTool() {
    this(new BritishEnglish());
  }

  public LanguageTool(Language lang) {
    this.lang = lang;
    langTool = new JLanguageTool(this.lang);
  }

  public List<RuleMatch> spellcheck(String text) throws IOException {
    List<RuleMatch> matches = langTool.check(text);
    return matches;
  }

  public void printRuleMatch(List<RuleMatch> matches) {
    for (RuleMatch match : matches) {
      System.out.println("Potential error at characters "
              + match.getFromPos() + "-" + match.getToPos() + ": "
              + match.getMessage());
      System.out.println("Suggested correction(s): "
              + match.getSuggestedReplacements());
    }
  }

  public void runDemo() throws IOException {
    String line = "A sentence with a error in the Hitchhiker's Guide tot he Galaxy";
    System.out.println("Checking '" + line + "'");
    printRuleMatch(spellcheck(line));
  }

  public static void main(String args[]) throws Exception {
    System.out.println("Language Tool Spell Checker Demo");
    System.out.println("Loading ...");
    System.out.print("Enter text: ");
    LanguageTool checker = new LanguageTool();

    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNextLine()) {

      String line = scanner.nextLine();
      List<RuleMatch> matches = checker.spellcheck(line);
      checker.printRuleMatch(matches);
      System.out.print("Enter text: ");
    }

  }
}
