from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

# Request/Response Models
class FilterRequest(BaseModel):
    text: str

class FilterResponse(BaseModel):
    filteredText: str

# Health check / test endpoint
@app.get("/")
def root():
    return {"message": "hi"}

# Dummy filter endpoint
@app.post("/filter", response_model=FilterResponse)
def filter_text(request: FilterRequest):
    # Simple placeholder logic
    return {"filteredText": "hi from python filter service"}
