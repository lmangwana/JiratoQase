# JiratoQase

**JiratoQase** is a Java-based migration utility that extracts test cases from Jira (via CSV export) and prepares them for seamless import into [Qase TestOps](https://qase.io).  
It also supports automated Qase Run creation and result uploads — including attachments — using the Qase API.

---

## Features

- **Jira CSV Parsing** – Reads exported Jira tickets and extracts:
    - Issue keys, summaries, descriptions
    - Comments
    - Attachment references (URLs and filenames)
- **Attachment Mapping** – Resolves Jira-referenced files to local file paths for upload.
- **Qase CSV Export** – Generates Qase-compatible CSV for bulk test case import.
- **Qase API Integration**:
    - Create new test runs in Qase
    - Upload test results with or without attachments
- **Sprint-Based Workflow** – Designed to map Jira sprints directly to Qase suites/runs.

---

##  Project Structure

<pre>
src/test/java/
├── base/        # Base URI setup
├── config/      # API key and base URL
├── endpoints/   # All endpoint request logic
├── tests/       # TestNG test cases
testng.xml       # Test suite config
pom.xml          # Maven dependencies
</pre>


---

## Prerequisites

- **Java 17+**
- **Maven 3.9+**
- Qase API Token (stored in environment variable `QASE_API_TOKEN`)
  ```bash
  export QASE_API_TOKEN="your_qase_api_token"
- Jira CSV export for the sprint you want to migrate

---

## Usage

1. **Create a Qase Test Run**
   ```bash
   mvn exec:java -Dexec.mainClass="utils.QaseRunCreator"
2. **Generate Qase CSV from Jira**
   ```bash
   mvn exec:java -Dexec.mainClass="Main"
3. **Upload Test Results**
   ```bash
   mvn exec:java -Dexec.mainClass="utils.QaseResultUploader"