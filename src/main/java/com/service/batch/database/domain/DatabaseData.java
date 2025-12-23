package com.service.batch.database.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "DATABASE_DATA")
public class DatabaseData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DATA_A")
    private String dataA;

    @Column(name = "DATA_B")
    private String dataB;


    public void updateDataA() {
        this.dataA = this.dataA == null ? "" : this.dataA.concat("A");
    }
}
