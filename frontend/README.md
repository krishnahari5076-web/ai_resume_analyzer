# AI Resume Analyzer Frontend

Responsive static web interface for uploading a PDF resume and displaying an AI-generated ATS report.

## Run locally

1. Start the Spring Boot backend at `http://localhost:8080` with `GROQ_API_KEY` configured.
2. Serve this folder using a static web server (for example VS Code Live Server) and open the shown URL.

The frontend calls `http://localhost:8080/api/resume/analyze` by default. To deploy it separately, set `window.AI_RESUME_API_URL` before `script.js` in `index.html` to the HTTPS URL of the backend.

## Docker

```bash
docker build -t ai-resume-frontend .
docker run --rm -p 8081:80 ai-resume-frontend
```

Open `http://localhost:8081`; the backend should still be available on port 8080.
