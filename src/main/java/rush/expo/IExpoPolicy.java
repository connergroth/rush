package rush.expo;

import rush.tickets.Ticket;

import java.util.List;
import java.util.Optional;

public interface IExpoPolicy {
    Optional<Ticket> chooseNextTicket(List<Ticket> rail);
}
