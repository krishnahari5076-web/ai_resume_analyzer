# AI Resume Analyzer

An AI-powered Resume Analyzer built using Spring Boot, HTML, CSS, and JavaScript. The application allows users to upload a PDF resume, extracts the text using Apache PDFBox, sends the extracted content to a Large Language Model (LLM) through the Groq API, and provides a detailed ATS-style analysis.

## Features

- Upload Resume (PDF)
- PDF Validation
- Resume Text Extraction using Apache PDFBox
- AI-based Resume Analysis
- ATS Score Generation
- Professional Resume Summary
- Strengths Identification
- Weakness Detection
- Missing Skills Analysis
- Resume Improvement Suggestions
- Important Keyword Extraction
- Responsive User Interface
- REST API Integration

## Tech Stack

### Frontend
- HTML5
- CSS3
- JavaScript

### Backend
- Java 17
- Spring Boot
- Maven
- Apache PDFBox
- Groq API (LLM Integration)

## Project Architecture

```
Frontend (HTML/CSS/JS)
        │
        ▼
Spring Boot REST API
        │
        ▼
Apache PDFBox
        │
        ▼
Extract Resume Text
        │
        ▼
Groq AI
        │
        ▼
Resume Analysis
        │
        ▼
Frontend
```

## Folder Structure

```
AI-Resume-Analyzer
│
├── src
│   ├── main
│   │   ├── java
│   │   ├── resources
│   │   └── static
│   └── test
│
├── pom.xml
├── README.md
└── mvnw
```

## How It Works

1. Upload a PDF Resume.
2. Backend validates the uploaded file.
3. Apache PDFBox extracts text from the PDF.
4. Extracted text is sent to the Groq AI API.
5. AI analyzes the resume.
6. Backend returns:
   - ATS Score
   - Summary
   - Strengths
   - Weaknesses
   - Missing Skills
   - Suggestions
   - Keywords
7. Results are displayed on the frontend.

## Installation

### Clone Repository

```bash
git clone https://github.com/krishnahari5076-web/ai_resume_analyzer.git
```

### Open Project

Open the project in IntelliJ IDEA.

### Configure Environment Variable

Create an environment variable:

```
GROQ_API_KEY=YOUR_GROQ_API_KEY
```

### Run the Application

```bash
mvn spring-boot:run
```

The application will start at:

```
http://localhost:8080
```

## API Endpoint

```
POST /api/resume/analyze
```

Content Type:

```
multipart/form-data
```

Parameter:

```
file : PDF Resume
```

## Future Enhancements

- User Authentication
- Resume History
- Multiple Resume Comparison
- Downloadable Analysis Report
- Job Recommendation System
- Cover Letter Generator
- Resume Template Suggestions
