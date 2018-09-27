package com.vpacheco.vlib.model;

import com.vpacheco.vlib.model.audit.UserDateAudit;
import com.vpacheco.vlib.validator.ValidDeliverDate;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@ValidDeliverDate
public class Requisition extends UserDateAudit {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @NotNull
  private Book book;

  private LocalDateTime pickupDate;

  private LocalDateTime deliveryDate;

  @ManyToOne
  private User authorizedBy;

  @ManyToOne(optional = false)
  private Customer customer;

  @NotNull
  private RequisitionStatus status = RequisitionStatus.RESERVED;

  @PreUpdate
  public void setCopiesOnUpdate() {
    int availableCopies = book.getCopiesAvailable();
    book.setCopiesAvailable(availableCopies--);
    if (status ==  RequisitionStatus.CANCELLED ||
        status == RequisitionStatus.DELIVERED)
      book.setCopiesAvailable(availableCopies++);
    else if (status == RequisitionStatus.PICKED_UP)
      book.setCopiesAvailable(availableCopies--);
  }

  @PrePersist
  public void setCopiesOnCreate() {
    int availableCopies = book.getCopiesAvailable();
    book.setCopiesAvailable(availableCopies--);
  }
}
