package nosdaq.ast.value;

import java.util.Objects;

public final class ISODate extends Value {
    private final String ISODate;

    public ISODate(final String isoDate) {
        this.ISODate = Objects.requireNonNull(isoDate);
    }

    public String getISODate() {
        return ISODate;
    }

    @Override
    public <T> T accept(ValueVisitor<T> visitor) {
        try {
            return visitor.visit(this);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ISODate isoDate = (ISODate) o;
        return Objects.equals(ISODate, isoDate.ISODate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ISODate);
    }

    @Override
    public String toString() {
        return String.format("ISODate(%s)", '"' + ISODate + '"');
    }
}
