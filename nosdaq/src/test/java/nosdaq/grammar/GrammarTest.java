package nosdaq.grammar;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GrammarTest {
    @Test
    void grammarParserTest() throws IOException {
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar.txt");
        List<Production> productions = grammar.getProductions();
        System.out.println("Number of Productions: " + productions.size());
        System.out.println("Start Symbol: " + grammar.getStartSymbol().getName());
        for (Production production : productions) {
            System.out.print(production.lhs().getName() + " ::= ");
            for (Symbol symbol : production.rhs()) {
                System.out.print(symbol.getName() + " ");
            }
            System.out.println();
        }
    }

    @Test
    void grammarParserTest2() throws IOException {
        Grammar grammar = GrammarParser.parse("./src/main/java/nosdaq/grammar/Grammar.txt");
        Map<NonTerminal, List<Production>> productionMap = grammar.getLhsToProductionMap();
        System.out.println("Size of production map: " + productionMap.size());

        for (Map.Entry<NonTerminal, List<Production>> entry : productionMap.entrySet()) {
            System.out.println("Key: " + entry.getKey().getName());
            for (Production production : entry.getValue()) {
                for (Symbol symbol : production.rhs()) {
                    System.out.print(symbol.getName() + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}
