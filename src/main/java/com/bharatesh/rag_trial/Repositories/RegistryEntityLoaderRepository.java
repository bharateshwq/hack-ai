package com.bharatesh.rag_trial.Repositories;

import com.bharatesh.rag_trial.Models.RegistryEntity;
import com.bharatesh.rag_trial.dto.RegistryEntityDTO.MaskingType;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class RegistryEntityLoaderRepository {

    private final RegistryEntityRepository repository;
    

    // Map equivalent to your Python ENTITY_RECOGNIZER_MAP (key only)
    private static final List<String> ENTITY_KEYS = List.of(
            // GLOBAL
            "CREDIT_CARD", "CRYPTO", "DATE_TIME", "EMAIL_ADDRESS", "IBAN_CODE", "IP_ADDRESS",
            "LOCATION", "PERSON", "NRP", "PHONE_NUMBER", "MEDICAL_LICENSE", "URL",

            // USA
            "US_BANK_NUMBER", "US_DRIVER_LICENSE", "US_ITIN", "US_PASSPORT", "US_SSN",

            // UK
            "UK_NHS", "UK_NINO",

            // SPAIN
            "ES_NIF", "ES_NIE",

            // ITALY
            "IT_FISCAL_CODE", "IT_DRIVER_LICENSE", "IT_VAT_CODE", "IT_PASSPORT", "IT_IDENTITY_CARD",

            // POLAND
            "PL_PESEL",

            // SINGAPORE
            "SG_NRIC_FIN", "SG_UEN",

            // AUSTRALIA
            "AU_ABN", "AU_ACN", "AU_TFN", "AU_MEDICARE",

            // INDIA
            "IN_PAN", "IN_AADHAAR", "IN_VEHICLE_REGISTRATION", "IN_VOTER", "IN_PASSPORT", "IN_GSTIN",

            // FINLAND
            "FI_PERSONAL_IDENTITY_CODE",

            // KOREA
            "KR_RRN",

            // THAILAND
            "TH_TNIN"
    );

    public RegistryEntityLoaderRepository(RegistryEntityRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void loadEntities() {
        if (repository.count() > 0) {
            log.info("Registry entities already loaded. Skipping...");
            return;
        }

        List<RegistryEntity> entities = new ArrayList<>();

        for (int i = 0; i < ENTITY_KEYS.size(); i++) {
            String key = ENTITY_KEYS.get(i);

            RegistryEntity e = new RegistryEntity();
            e.setName(key);

            // First 12 items -> defaultEnabled = true
            e.setDefaultEnabled(i < 12);

            // Set masking type to REDACT
            e.setMaskingType(MaskingType.REDACT);

            // Set label based on key
            e.setLabel(getLabelForKey(key));

            entities.add(e);
        }

        repository.saveAll(entities);
        log.info("Loaded {} registry entities.", entities.size());
    }

    private String getLabelForKey(String key) {
        return switch (key) {
            case "CREDIT_CARD", "CRYPTO", "DATE_TIME", "EMAIL_ADDRESS", "IBAN_CODE", "IP_ADDRESS",
                 "LOCATION", "PERSON", "PHONE_NUMBER", "MEDICAL_LICENSE", "URL" ,"NRP"-> "GLOBAL";
            case "US_BANK_NUMBER", "US_DRIVER_LICENSE", "US_ITIN", "US_PASSPORT", "US_SSN" -> "US";
            case "UK_NHS", "UK_NINO" -> "UK";
            case "ES_NIF", "ES_NIE" -> "ES";
            case "IT_FISCAL_CODE", "IT_DRIVER_LICENSE", "IT_VAT_CODE", "IT_PASSPORT", "IT_IDENTITY_CARD" -> "IT";
            case "PL_PESEL" -> "PL";
            case "SG_NRIC_FIN", "SG_UEN" -> "SG";
            case "AU_ABN", "AU_ACN", "AU_TFN", "AU_MEDICARE" -> "AU";
            case "IN_PAN", "IN_AADHAAR", "IN_VEHICLE_REGISTRATION", "IN_VOTER", "IN_PASSPORT", "IN_GSTIN" -> "IN";
            case "FI_PERSONAL_IDENTITY_CODE" -> "FI";
            case "KR_RRN" -> "KR";
            case "TH_TNIN" -> "TH";
            default -> "GLOBAL";
        };
    }
}
