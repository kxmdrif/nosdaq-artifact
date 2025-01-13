package nosdaq.grammar;

import java.lang.reflect.Array;
import java.util.*;

public final class Grammar {
    private final List<Production> productions;
    private final NonTerminal startSymbol;
    private final Map<NonTerminal, List<Production>> lhsToProductionMap;

    public Grammar(List<Production> productions, NonTerminal startSymbol) {
        this.productions = productions; // check
        this.startSymbol = startSymbol; // check
        this.lhsToProductionMap = genLhsToProductionMap(productions);
    }

    private Map<NonTerminal, List<Production>> genLhsToProductionMap(List<Production> productions) {
        Map<NonTerminal, List<Production>> productionMap = new HashMap<>();
        for (Production production : productions) {
            if (productionMap.get(production.lhs()) == null) {
                productionMap.put(production.lhs(), Arrays.asList(production));
            } else {
                List<Production> temp = new ArrayList(productionMap.get(production.lhs()));
                temp.add(production);
                productionMap.put(production.lhs(), temp);
            }
        }

        return productionMap;
    }

    public List<Production> getProductions() {
        return productions;
    }

    public NonTerminal getStartSymbol() {
        return startSymbol;
    }

    public Map<NonTerminal, List<Production>> getLhsToProductionMap() {
        return lhsToProductionMap;
    }

    public List<Production> getProductionsWithLhs(NonTerminal lhs) {
        return lhsToProductionMap.get(lhs);
    }
}
