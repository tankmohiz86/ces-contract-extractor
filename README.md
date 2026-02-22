# CES CMR Contract Extractor

Extract structured data from GE Aerospace CMR contract PDFs using AWS Bedrock (Titan Text Lite v1).

**Stack:** AngularJS 1.8 · Spring Boot 3 · Java 17 · AWS SDK v2.41.31 · PostgreSQL 15 · Docker

---

## ⚡ Run Locally in 3 Steps

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running
- AWS credentials with `bedrock:InvokeModel` permission on `amazon.titan-text-lite-v1`

### Step 1 — Set your AWS credentials

```bash
# Linux / macOS
export AWS_ACCESS_KEY_ID=your_key_here
export AWS_SECRET_ACCESS_KEY=your_secret_here
export AWS_REGION=us-east-1

# Windows PowerShell
$env:AWS_ACCESS_KEY_ID="your_key_here"
$env:AWS_SECRET_ACCESS_KEY="your_secret_here"
$env:AWS_REGION="us-east-1"
```

### Step 2 — Start all services

```bash
docker-compose up --build
```

First build takes ~3-4 minutes (Maven downloads dependencies).
Subsequent starts take ~30 seconds.

### Step 3 — Open the app

```
http://localhost:4200
```

---

## 🧪 Test with Sample PDF

A ready-made sample CMR contract PDF is included:

```
sample-pdfs/ces-cmr-2024-0042.pdf
```

Upload it via the UI to test the full extraction pipeline.

To regenerate it:
```bash
cd sample-pdfs
python3 generate_sample_pdf.py
```

---

## 📡 API Endpoints

| Method | URL                          | Description                        |
|--------|------------------------------|------------------------------------|
| POST   | `/api/contracts/upload`      | Upload PDF → extract → save to DB  |
| GET    | `/api/contracts`             | List all extracted contracts        |
| DELETE | `/api/contracts/{id}`        | Delete a contract record            |
| GET    | `/api/contracts/health`      | Health check                        |

---

## 🗂 Project Structure

```
ces-contract-extractor/
├── backend/                    Spring Boot 3 / Java 17
│   ├── src/main/java/com/ces/
│   │   ├── ContractExtractorApplication.java
│   │   ├── controller/ContractController.java
│   │   ├── service/
│   │   │   ├── BedrockService.java       # AWS SDK v2.41.31
│   │   │   ├── PdfExtractionService.java # PDFBox 3.x
│   │   │   └── ContractService.java      # Orchestration
│   │   ├── entity/ContractRecord.java
│   │   ├── repository/ContractRepository.java
│   │   ├── dto/ContractRecordDto.java
│   │   └── config/AppConfig.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/V1__create_contract_records.sql
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                   AngularJS 1.8 / Nginx
│   ├── app/
│   │   ├── app.module.js
│   │   ├── components/app.controller.js
│   │   ├── services/contract.service.js
│   │   └── directives/dropzone.directive.js
│   ├── index.html
│   ├── styles.css
│   ├── nginx.conf
│   └── Dockerfile
│
├── docker-compose.yml
└── sample-pdfs/
    ├── ces-cmr-2024-0042.pdf
    └── generate_sample_pdf.py
```

---

## ☁️ Deploy to Render (when ready)

1. Push this repo to GitHub
2. On [render.com](https://render.com):
   - **New → PostgreSQL** → copy Internal Database URL
   - **New → Web Service** → `backend/` folder → set env vars:
     - `DATABASE_URL` = the Postgres URL from step above
     - `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_REGION`
   - **New → Web Service** → `frontend/` folder → default settings
3. Every push to `main` auto-redeploys both services

---

## 🔐 Required IAM Permission

Create a dedicated IAM user with this single policy:

```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Action": "bedrock:InvokeModel",
    "Resource": "arn:aws:bedrock:*::foundation-model/amazon.titan-text-lite-v1"
  }]
}
```

---

## 💡 Notes

- **Bedrock region:** `amazon.titan-text-lite-v1` is available in `us-east-1` and `us-west-2`
- **Cost:** ~$0.001 per PDF extraction
- **PDF size limit:** 20 MB (configurable in `application.yml`)
- **Local Postgres** is persisted in Docker volume `ces-pgdata` — data survives restarts
