package rush.kitchen;

public record RunMetrics(int tablesServed, double averageTicketTime, double averageWaitAtPass, int maxWaitAtPass) {
    @Override
    public String toString() {
        return String.format("Tables served: %d, average ticket time: %.2f ticks, "
                        + "average wait at the pass: %.2f ticks, max wait at the pass: %d ticks",
                tablesServed, averageTicketTime, averageWaitAtPass, maxWaitAtPass);
    }
}
