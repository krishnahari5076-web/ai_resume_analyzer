# AI Resume Analyzer Backend

Spring Boot / Java 26 backend for instant PDF resume analysis with Groq.

## How it works

1. The frontend uploads a PDF to `POST /api/resume/analyze`.
2. The backend validates the file and extracts its text with Apache PDFBox.
3. Groq analyzes the text and returns structured JSON.
4. The backend immediately returns the analysis to the frontend.

The application does not use a database and does not store resumes or analyses.

## Setup

Set the Groq API key once on Windows, then restart IntelliJ:

```powershell
setx GROQ_API_KEY "your_groq_api_key"
```

Start the backend on `http://localhost:8080`.

## API

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/resume/test` | Health check |
| POST | `/api/resume/analyze` | Upload a PDF using the `file` form field and receive the analysis |

The analysis response includes ATS score, summary, strengths, weaknesses, missing skills, suggestions, and keywords.
