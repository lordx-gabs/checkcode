package com.edu.checkcode.model;

import com.edu.checkcode.util.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
public class Ticket {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@ManyToOne
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;

	@ManyToOne
	@JoinColumn(name = "payment_id", nullable = false)
	private Payment payment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "ticket_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private TicketStatus ticketStatus = TicketStatus.NEW;

	@PrePersist
	void onPersist() {
		this.createdAt = Instant.now();
	}

	public static Ticket from(Payment payment) {
		Ticket ticket = new Ticket();
		ticket.setEvent(payment.getEvent());
		ticket.setPayment(payment);
		ticket.setUser(payment.getUser());
		return ticket;
	}
}
