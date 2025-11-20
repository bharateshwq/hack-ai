import os
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from presidio_analyzer import AnalyzerEngine, RecognizerRegistry, Pattern
from presidio_analyzer.pattern_recognizer import PatternRecognizer
from presidio_anonymizer import AnonymizerEngine
from presidio_analyzer.predefined_recognizers import (
    AbaRoutingRecognizer,
    CreditCardRecognizer,
    CryptoRecognizer,
    DateRecognizer,
    EmailRecognizer,
    IbanRecognizer,
    IpRecognizer,
    NhsRecognizer,
    MedicalLicenseRecognizer,
    PhoneRecognizer,
    SgFinRecognizer,
    UrlRecognizer,
    UsBankRecognizer,
    UsItinRecognizer,
    UsLicenseRecognizer,
    UsPassportRecognizer,
    UsSsnRecognizer,
    EsNifRecognizer,
    AuAbnRecognizer,
    AuAcnRecognizer,
    AuTfnRecognizer,
    AuMedicareRecognizer,
    ItDriverLicenseRecognizer,
    ItFiscalCodeRecognizer,
    ItVatCodeRecognizer,
    ItIdentityCardRecognizer,
    ItPassportRecognizer,
    InPanRecognizer,
    PlPeselRecognizer,
    InAadhaarRecognizer,
    InVehicleRegistrationRecognizer,
    SgUenRecognizer,
    InVoterRecognizer,
    InPassportRecognizer,
    FiPersonalIdentityCodeRecognizer,
    EsNieRecognizer,
    UkNinoRecognizer,
    KrRrnRecognizer,
)


# Set up FastAPI
app = FastAPI()

# Initialize Presidio analyzer and anonymizer
anonymizer_engine = AnonymizerEngine()
analyzer_engine = AnalyzerEngine()

# Define Pydantic models for request and response
class RegexPattern(BaseModel):
    regex: str  # Regex pattern
    score: float = 0.8  # Confidence score (0-1)



class CustomEntity(BaseModel):
    title: str  # Entity name (e.g., "TITLE")
    deny_list: list[str] | None = None  # List of tokens to treat as PII
    deny_list_regex: RegexPattern | None    = None  # Regex pattern with score to match PII

class AnalyzeRequest(BaseModel):
    text: str
    language: str = "en"  # Default language is English
    predefined_entities: list[str] = []  # Predefined recognizers to use
    custom_entities: list[CustomEntity] = []  # Custom entity patterns

class AnalyzeResponse(BaseModel):
    original_text: str
    anonymized_text: str


ENTITY_RECOGNIZER_MAP = {

    # ------------------------- GLOBAL -------------------------
    "CREDIT_CARD": CreditCardRecognizer,
    "CRYPTO": CryptoRecognizer,                 # BTC only
    "DATE_TIME": DateRecognizer,
    "EMAIL_ADDRESS": EmailRecognizer,
    "IBAN_CODE": IbanRecognizer,
    "IP_ADDRESS": IpRecognizer,
    "LOCATION": None,                           # No built-in recognizer
    "PERSON": None,                             # No built-in recognizer (use NER)
    "NRP": None,                                # No built-in recognizer
    "PHONE_NUMBER": PhoneRecognizer,
    "MEDICAL_LICENSE": MedicalLicenseRecognizer,
    "URL": UrlRecognizer,


    # ------------------------- USA ----------------------------
    "US_BANK_NUMBER": UsBankRecognizer,
    "US_DRIVER_LICENSE": UsLicenseRecognizer,
    "US_ITIN": UsItinRecognizer,
    "US_PASSPORT": UsPassportRecognizer,
    "US_SSN": UsSsnRecognizer,


    # ------------------------- UK -----------------------------
    "UK_NHS": NhsRecognizer,
    "UK_NINO": UkNinoRecognizer,


    # ------------------------- SPAIN --------------------------
    "ES_NIF": EsNifRecognizer,
    "ES_NIE": EsNieRecognizer,


    # ------------------------- ITALY --------------------------
    "IT_FISCAL_CODE": ItFiscalCodeRecognizer,
    "IT_DRIVER_LICENSE": ItDriverLicenseRecognizer,
    "IT_VAT_CODE": ItVatCodeRecognizer,
    "IT_PASSPORT": ItPassportRecognizer,
    "IT_IDENTITY_CARD": ItIdentityCardRecognizer,


    # ------------------------- POLAND -------------------------
    "PL_PESEL": PlPeselRecognizer,


    # ------------------------- SINGAPORE ----------------------
    "SG_NRIC_FIN": SgFinRecognizer,
    "SG_UEN": SgUenRecognizer,


    # ------------------------- AUSTRALIA ----------------------
    "AU_ABN": AuAbnRecognizer,
    "AU_ACN": AuAcnRecognizer,
    "AU_TFN": AuTfnRecognizer,
    "AU_MEDICARE": AuMedicareRecognizer,


    # ------------------------- INDIA --------------------------
    "IN_PAN": InPanRecognizer,
    "IN_AADHAAR": InAadhaarRecognizer,
    "IN_VEHICLE_REGISTRATION": InVehicleRegistrationRecognizer,
    "IN_VOTER": InVoterRecognizer,
    "IN_PASSPORT": InPassportRecognizer,
    "IN_GSTIN": None,                           # No built-in recognizer


    # ------------------------- FINLAND ------------------------
    "FI_PERSONAL_IDENTITY_CODE": FiPersonalIdentityCodeRecognizer,


    # ------------------------- KOREA --------------------------
    "KR_RRN": KrRrnRecognizer,


    # ------------------------- THAILAND -----------------------
    "TH_TNIN": None,                            # No built-in recognizer
}


@app.post("/analyze", response_model=AnalyzeResponse)
async def analyze(request: AnalyzeRequest):
    """Analyzes and redacts sensitive information from the text."""
    try:
        # Add predefined recognizers if specified
        if request.predefined_entities:
            for entity in request.predefined_entities:
                if entity in ENTITY_RECOGNIZER_MAP:
                    recognizer_class = ENTITY_RECOGNIZER_MAP[entity]
                    if recognizer_class is not None:
                        recognizer = recognizer_class()
                        analyzer_engine.registry.add_recognizer(recognizer)
                else:
                    raise HTTPException(status_code=400, detail=f"Entity {entity} not found")

        # Add custom entity recognizers
        if request.custom_entities:
            for custom_entity in request.custom_entities:
                # Create recognizer from deny_list
                if custom_entity.deny_list:
                    custom_recognizer = PatternRecognizer(
                        supported_entity=custom_entity.title,
                        deny_list=custom_entity.deny_list
                    )
                    analyzer_engine.registry.add_recognizer(custom_recognizer)

                # Create recognizer from regex
                if custom_entity.deny_list_regex:
                    regex_pattern = Pattern(
                        name=f"{custom_entity.title}_pattern",
                        regex=custom_entity.deny_list_regex.regex,
                        score=custom_entity.deny_list_regex.score
                    )
                    regex_recognizer = PatternRecognizer(
                        supported_entity=custom_entity.title,
                        patterns=[regex_pattern]
                    )
                    analyzer_engine.registry.add_recognizer(regex_recognizer)

        # Analyze the text using Presidio Analyzer
        results = analyzer_engine.analyze(text=request.text, language=request.language,return_decision_process=True)



        anonymized_result = anonymizer_engine.anonymize(
            text=request.text,
            analyzer_results=results,
        )

        anonymized_text = anonymized_result.text

        return AnalyzeResponse(
            original_text=request.text,
            anonymized_text=anonymized_text
        )

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/supported-entities")
async def get_supported_entities():
    """Get all supported entity types grouped by category."""
    try:
        # Define categories and their entities
        categories = {
            "Global": [
                "CREDIT_CARD", "CRYPTO", "DATE_TIME", "EMAIL_ADDRESS",
                "IBAN_CODE", "IP_ADDRESS", "LOCATION", "PERSON", "NRP",
                "PHONE_NUMBER", "MEDICAL_LICENSE", "URL"
            ],
            "USA": [
                "US_BANK_NUMBER", "US_DRIVER_LICENSE", "US_ITIN",
                "US_PASSPORT", "US_SSN"
            ],
            "UK": [
                "UK_NHS", "UK_NINO"
            ],
            "Spain": [
                "ES_NIF", "ES_NIE"
            ],
            "Italy": [
                "IT_FISCAL_CODE", "IT_DRIVER_LICENSE", "IT_VAT_CODE",
                "IT_PASSPORT", "IT_IDENTITY_CARD"
            ],
            "Poland": [
                "PL_PESEL"
            ],
            "Singapore": [
                "SG_NRIC_FIN", "SG_UEN"
            ],
            "Australia": [
                "AU_ABN", "AU_ACN", "AU_TFN", "AU_MEDICARE"
            ],
            "India": [
                "IN_PAN", "IN_AADHAAR", "IN_VEHICLE_REGISTRATION",
                "IN_VOTER", "IN_PASSPORT", "IN_GSTIN"
            ],
            "Finland": [
                "FI_PERSONAL_IDENTITY_CODE"
            ],
            "Korea": [
                "KR_RRN"
            ],
            "Thailand": [
                "TH_TNIN"
            ]
        }

        # Build response with only entities that exist in ENTITY_RECOGNIZER_MAP
        recognizer_entities = []
        for category, entities in categories.items():
            valid_entities = [entity for entity in entities if entity in ENTITY_RECOGNIZER_MAP]
            if valid_entities:
                recognizer_entities.append({
                    "title": category,
                    "valid_entities": sorted(valid_entities)
                })

        return {"recognizer_entities": recognizer_entities}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/health")
async def health():
    """Health check endpoint."""
    return {"status": "Presidio Analyzer service is up"}
