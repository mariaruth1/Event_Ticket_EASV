package ticketSystemEASV.be;

import java.util.UUID;

public class Ticket {
    private Event event;
    private Customer customer;
    private UUID id;
    private TicketType ticketType;
    private byte[] ticketQR;
    private boolean sent;
    private boolean redeemed;

    public Ticket() {}

    public Ticket(byte[] ticketQR) {
        this.ticketQR = ticketQR;
    }

    public Ticket(Event event, Customer customer, byte[] ticketQR) {
        this.event = event;
        this.customer = customer;
        this.ticketQR = ticketQR;
        this.ticketType = TicketType.TICKET;
    }

    public Ticket(UUID id, Event event, Customer customer, byte[] ticketQR) {
        this(event, customer,ticketQR);
        this.id = id;
    }

    public Ticket(Event event, Customer customer) {
        this.event = event;
        this.customer = customer;
        this.ticketType = TicketType.TICKET;
    }

    public Ticket(UUID id, Customer customer, byte[] ticketQR) {
        this.id = id;
        this.customer = customer;
        this.ticketType = TicketType.TICKET;
        this.ticketQR = ticketQR;
    }


    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public byte[] getTicketQR() {
        return ticketQR;
    }

    public void setTicketQR(byte[] ticketQR) {
        this.ticketQR = ticketQR;
    }

    public void setId(UUID id) {
        this.id = id;
    }
    public UUID getId() {
        return id;
    }
}
