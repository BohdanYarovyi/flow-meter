package com.yarovyi.flowmeter.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted")
    private boolean deleted;


    /**
     * Constructor for creating independence {@code BaseEntity}.
     * @apiNote be careful, the constructor copies the object without preserving two-way relationships,
     * as JPA entities do
     * @param other other {@code BaseEntity} object
     */
    public BaseEntity(BaseEntity other) {
        this.id = other.id;
        this.createdAt = other.createdAt == null ? null : LocalDateTime.from(other.createdAt);
        this.updatedAt = other.updatedAt == null ? null : LocalDateTime.from(other.updatedAt);
        this.deleted = other.deleted;
    }

    @PrePersist
    protected void onCreate() {this.createdAt = LocalDateTime.now();}

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deleted = true;
    }

    public void restore() {
        this.deleted = false;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BaseEntity that = (BaseEntity) object;
        // якщо немає id у двох об'єктів, то вони вважаються різними в системі
        if (this.id == null) return false;

        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
               "id=" + id +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               ", deleted=" + deleted +
               '}';
    }

}
