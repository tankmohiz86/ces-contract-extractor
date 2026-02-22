package com.ces.contract.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "contract_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @Column(name = "contract_number")
    private String contractNumber;

    @Column(name = "work_order_number")
    private String workOrderNumber;

    @Column(name = "airline_operator")
    private String airlineOperator;

    @Column(name = "aircraft_type")
    private String aircraftType;

    @Column(name = "engine_model")
    private String engineModel;

    @Column(name = "engine_serial_number")
    private String engineSerialNumber;

    @Column(name = "shop_visit_date")
    private String shopVisitDate;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "station_location")
    private String stationLocation;

    @Column(name = "total_cost")
    private String totalCost;

    @Column(name = "currency")
    private String currency;

    @Column(name = "work_scope", columnDefinition = "TEXT")
    private String workScope;

    @Column(name = "return_to_service_date")
    private String returnToServiceDate;

    @Column(name = "authorized_signatory")
    private String authorizedSignatory;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "extraction_status")
    private String extractionStatus;

    @Column(name = "raw_llm_response", columnDefinition = "TEXT")
    private String rawLlmResponse;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
