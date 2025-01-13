package nosdaq.grammar;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record Production(NonTerminal lhs, List<Symbol> rhs) {
    // private final String name

    public Production(final NonTerminal lhs, final List<Symbol> rhs) {
        this.lhs = Objects.requireNonNull(lhs);
        this.rhs = Collections.unmodifiableList(Objects.requireNonNull(rhs));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return lhs.equals(that.lhs) && rhs.equals(that.rhs);
    }
}
