# Groq API setup

1. Create an API key in the Groq Console.
2. In IntelliJ, add this environment variable to the backend Run Configuration:

   `GROQ_API_KEY=your_groq_api_key`

3. Restart the backend and open the frontend as before.

The application keeps the Groq key on the server. Do not put it in the frontend files or commit it to source control.

The configured model is `llama-3.3-70b-versatile`. Change `groq.api.model` in `src/main/resources/application.properties` only if you need to use another Groq model available to your key.
