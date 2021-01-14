package com.paulfrmbrn.ssmdemo;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("orders")
public class Order {

    @Id
    private final Long id;
    private final String status;
    private final String comment;

    public Order(Long id, String status, String comment) {
        this.id = id;
        this.status = status;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(getId(), order.getId()) && Objects.equals(getStatus(), order.getStatus()) && Objects.equals(getComment(), order.getComment());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStatus(), getComment());
    }
}
