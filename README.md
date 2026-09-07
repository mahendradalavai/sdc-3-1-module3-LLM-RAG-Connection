# RAG vs LLM: Retrieval-Augmented Generation Benchmark for Android

An Android application built with **Jetpack Compose** and **Material Design 3** that demonstrates and benchmarks **Retrieval-Augmented Generation (RAG)** against **Direct LLM** generation using the Google Gemini API.

---

## 🌟 Overview

Large Language Models (LLMs) are powerful at general reasoning but often suffer from hallucinations, outdated knowledge, or a lack of access to private domain documents. **RAG (Retrieval-Augmented Generation)** addresses this by retrieving relevant passages from a local knowledge base and augmenting the prompt with ground-truth context before generating a response.

This project provides an interactive side-by-side playground that lets you:
1. **Index Documents Locally**: Store and manage custom knowledge documents in an offline SQLite/Room database with automatic text passage chunking.
2. **Retrieve Context (BM25 Engine)**: Execute keyword and term-frequency retrieval to find top-matching passages relevant to any user query.
3. **Compare in Real Time**: Send queries simultaneously to:
   - **RAG-Grounded Model**: Augmented with retrieved context chunks, instructed to cite sources and reject ungrounded assumptions.
   - **Direct LLM**: Relying purely on parametric memory without local document grounding.
4. **Benchmark & Inspect**: Compare latency, output divergence, citation sources, and similarity scores.

---

## ✨ Key Features

### 🔍 Dual-Generation Comparison
- **Side-by-Side & Tabbed Views**: Seamlessly switch between side-by-side comparison cards and tabbed deep dives.
- **Parametric vs. Grounded Answers**: Observe how RAG corrects hallucinations, supplies exact internal version numbers, and stays within domain boundaries.
- **Latency Benchmarks**: Real-time response timing for both pipelines.

### 📚 In-App Knowledge Base
- **Pre-Loaded Sample Specs**: Includes sample enterprise documents (e.g., *Project Helios Architecture*, *Aurora API Security Policies*, *Titan Fleet Deployment Guidelines*).
- **Custom Document Ingestion**: Add your own documents, specifications, or notes directly inside the app.
- **Dynamic Indexing**: Toggle documents on/off to see how the model behaves when information is missing from the index.
- **Chunk Extraction**: Automatically segments documents into overlapping text passages for retrieval.

### 🔎 Retrieved Chunk Inspector
- **Score Breakdown**: Inspect the exact chunks retrieved for each query along with similarity/relevance scores.
- **Source Attribution**: See which document and passage provided the factual basis for the answer.

### 📜 Query History & Persistence
- **Room Database**: All documents, chunk indices, and benchmark sessions are persisted locally using Android Room.
- **History Viewer**: Review previous questions, answer comparisons, latency metrics, and retrieved references.

### 🎨 Geometric Balance Design System
- Clean, high-contrast Material 3 interface inspired by the *Geometric Balance* design theme.
- Rounded surface cards (20–24dp), clear visual hierarchy, accessible touch targets (48dp+), and adaptive layout support.

---

## 🏗️ Architecture & Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Architecture**: MVVM (Model-View-ViewModel) with StateFlow
- **Local Persistence**: Android Room Database (`DocumentEntity`, `ChunkEntity`, `ComparisonHistoryEntity`)
- **Retrieval Engine**: Local BM25-inspired passage scoring and chunk ranking
- **AI Model**: Google Gemini API (`gemini-2.5-flash`) via direct REST API integration
- **Offline Simulation Fallback**: Built-in realistic grounded simulation when running without an API key, allowing complete offline demonstration

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug or newer
- JDK 17+
- Android device or emulator running Android 8.0 (API 26) or higher

### Configuration & API Key

The app works out of the box with an offline simulation mode. To connect directly to the live Gemini API:

1. Obtain a Google Gemini API key from [Google AI Studio](https://aistudio.google.com/).
2. In the app, tap the **Key** icon in the top app bar to enter your API key.
3. Alternatively, supply the key via the environment variable `GEMINI_API_KEY` in `.env` or the Secrets panel.

### Build and Run

```bash
# Clone the repository
git clone https://github.com/your-username/rag-vs-llm.git
cd rag-vs-llm

# Build debug APK using Gradle
gradle assembleDebug

# Run unit and local tests
gradle :app:testDebugUnitTest
```

---

## 📁 Project Structure

```
app/src/main/java/com/example/
├── data/
│   ├── api/
│   │   ├── GeminiApiService.kt        # Gemini REST client & prompt construction
│   │   └── GeminiModels.kt            # Request & response data models
│   ├── local/
│   │   ├── AppDatabase.kt             # Room database setup
│   │   ├── ComparisonHistoryEntity.kt # Query history entity
│   │   ├── DocumentDao.kt             # Room DAO for docs, chunks, history
│   │   └── DocumentEntity.kt          # Document and Chunk entity definitions
│   ├── repository/
│   │   └── RagRepository.kt           # RAG pipeline orchestration & BM25 engine
│   └── DefaultKnowledgeBase.kt        # Default seed documents & sample specs
├── ui/
│   ├── components/
│   │   ├── ApiKeyDialog.kt            # API key configuration dialog
│   │   ├── ComparisonView.kt          # Side-by-side RAG vs Direct comparison UI
│   │   ├── HistoryScreen.kt           # Benchmark history screen
│   │   ├── KnowledgeBaseScreen.kt     # Document management & chunking screen
│   │   ├── PromptInputSection.kt      # Query input & quick suggestion chips
│   │   └── RetrievedChunksView.kt     # Retrieved passage inspector cards
│   ├── theme/
│   │   ├── Color.kt                   # Geometric Balance palette
│   │   ├── Theme.kt                   # Material3 theme configuration
│   │   └── Type.kt                    # Typography definitions
│   ├── MainScreen.kt                  # App scaffold and bottom navigation
│   └── MainViewModel.kt               # Central UI state holder & pipeline triggers
└── MainActivity.kt                    # Single Activity entry point
```

---

## 🧪 Testing

The project includes JVM unit tests with Robolectric:

```bash
# Run unit tests
gradle :app:testDebugUnitTest
```

---

## 📄 License

This project is licensed under the Apache 2.0 License - see the LICENSE file for details.
