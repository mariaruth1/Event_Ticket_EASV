package ticketSystemEASV.be;

import java.util.UUID;

public class Ticket {
    private Event event;
    private Customer customer;
    private UUID id;
    private String ticketType;
    private byte[] ticketQR;

    public Ticket(Event event, Customer customer, String ticketType, byte[] ticketQR) {
        this.event = event;
        this.customer = customer;
        this.ticketType = ticketType;
        this.ticketQR = ticketQR;
    }

    public Ticket(UUID id, Event event, Customer customer, String ticketType, byte[] ticketQR) {
        this(event, customer, ticketType, ticketQR);
        this.id = id;
    }

    public Ticket(Event event, Customer customer) {
        this.event = event;
        this.customer = customer;
        this.ticketType = "Classic";
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

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public byte[] getTicketQR() {
        return ticketQR;
    }

    public void setTicketQR(byte[] ticketQR) {
        this.ticketQR = ticketQR;
    }

    public UUID getId() {
        return id;
    }
}
