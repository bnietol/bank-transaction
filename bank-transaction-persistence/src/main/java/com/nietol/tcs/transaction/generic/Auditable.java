package com.nietol.tcs.transaction.generic;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class Auditable<U> {

  @Column(name = "creation_date", nullable = false, updatable = false)
  @CreatedDate
  private Long creationDate;

  @Column(name = "last_modified_date")
  @LastModifiedDate
  private Long lastModifiedDate;

  public Auditable() {}

  public Auditable(Long creationDate, Long lastModifiedDate) {
    this.creationDate = creationDate;
    this.lastModifiedDate = lastModifiedDate;
  }

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  public Long getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(Long lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }
}
