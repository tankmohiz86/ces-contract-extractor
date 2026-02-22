package com.ces.contract.dto;

import com.ces.contract.entity.ContractRecord;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class ContractRecordDto {
    private Long id;
    private UUID uuid;
    private String contractNumber;
    private String workOrderNumber;
    private String airlineOperator;
    private String aircraftType;
    private String engineModel;
    private String engineSerialNumber;
    private String shopVisitDate;
    private String customerName;
    private String stationLocation;
    private String totalCost;
    private String currency;
    private String workScope;
    private String returnToServiceDate;
    private String authorizedSignatory;
    private String originalFilename;
    private String extractionStatus;
    private OffsetDateTime createdAt;

    public static ContractRecordDto from(ContractRecord r) {
        return ContractRecordDto.builder()
                .id(r.getId())
                .uuid(r.getUuid())
                .contractNumber(r.getContractNumber())
                .workOrderNumber(r.getWorkOrderNumber())
                .airlineOperator(r.getAirlineOperator())
                .aircraftType(r.getAircraftType())
                .engineModel(r.getEngineModel())
                .engineSerialNumber(r.getEngineSerialNumber())
                .shopVisitDate(r.getShopVisitDate())
                .customerName(r.getCustomerName())
                .stationLocation(r.getStationLocation())
                .totalCost(r.getTotalCost())
                .currency(r.getCurrency())
                .workScope(r.getWorkScope())
                .returnToServiceDate(r.getReturnToServiceDate())
                .authorizedSignatory(r.getAuthorizedSignatory())
                .originalFilename(r.getOriginalFilename())
                .extractionStatus(r.getExtractionStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
