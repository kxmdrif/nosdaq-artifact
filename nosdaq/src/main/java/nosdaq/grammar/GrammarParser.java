package nosdaq.grammar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class GrammarParser {
    public static Grammar parse(String grammarText) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(grammarText));
        String text;
        List<Production> productions = new ArrayList<>();
        while ((text = reader.readLine()) != null) {
            productions.add(parseProduction(text));
        }
        reader.close();

        return new Grammar(productions, getStartSymbol(productions));
    }

    public static Production parseProduction(String text) {
        String[] tokens = text.split(" ::= ");
        NonTerminal lhs = new NonTerminal(tokens[0]);

        String[] rhsTokens = tokens[1].split(" ");
        List<Symbol> rhs = new ArrayList<>();
        for (String token : rhsTokens) {
            if (token.length() == 0) {
                rhs.add(new Terminal(""));
            } else {
                if (Character.isUpperCase(token.charAt(0))) {
                    rhs.add(new NonTerminal(token));
                } else {
                    rhs.add(new Terminal(token));
                }
            }
        }

        return new Production(lhs, rhs);
    }

    private static NonTerminal getStartSymbol(List<Production> productions) {
        return productions.get(0).lhs();
    }
}
