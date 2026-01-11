# 📚 NLP-powered Reading App — Client/Server Platform for Vocabulary Discovery

This project is a full client–server system designed to help users read English books and learn new vocabulary directly inside the text.
Instead of switching apps or memorizing isolated flashcards, readers interact with highlighted words, definitions, and story context in real time.

## ✨ Core Features

Upload and process books in FB2 format

Extract and normalize text (tokenization, POS tagging, lemmatization)

Identify important words using TF-IDF across the entire book corpus

Detect unknown words based on a user dictionary

Perform Named Entity Recognition (people, places, countries, organizations)

Highlight words and display definitions in the Android client

Find other books where the same entities appear

Provide search and navigation over processed documents

## 🏗 System Architecture Overview

The platform consists of three major components working together:

### 1️⃣ Backend Service — Kotlin + Spring Boot

Responsible for:

uploading and parsing FB2 documents

text preprocessing and lemmatization

Stanford CoreNLP integration (NER, tagging, parsing)

computing TF-IDF per document and globally across the corpus

filtering user-known vocabulary

storing structured representations in PostgreSQL

exposing REST API endpoints for the Android client

### 2️⃣ Database — PostgreSQL

Stores:

formatted text for rendering

lemma frequencies and TF-IDF values

tsvector representations for full-text search

detected entities in JSONB

book metadata and processed notes

Full-text indexing enables fast search across large books.

### 3️⃣ Android Client — Kotlin

Implements:

Clean Architecture (Data / Domain / Presentation)

MVVM

Kotlin Coroutines for async operations

Retrofit + OkHttp for networking

Inline vocabulary hints fetched from the backend

Custom PageView for text pagination & rendering

Highlighting of unknown and important words

## 🧪 Technology Stack

### Backend

- Kotlin

- Spring Boot

- Hibernate / JPA

- PostgreSQL

- Stanford CoreNLP (remote microservice)

- Docker + Docker Compose

- PgAdmin for DB inspection

### Android

- Kotlin

- MVVM + Clean Architecture

- Coroutines + Flow

- Retrofit / OkHttp / Gson

- Dagger 2 for dependency injection

## 🔌 REST API Overview

(Representative examples based on implemented controllers)

POST /books/load – upload and index a new FB2 book

POST /dictionary/load – upload known-word list

GET /books/list – list processed books

GET /books/{title}/format_text – formatted book text

GET /books/{title}/strange_words – unknown vocabulary

GET /books/{title}/important_strange_words – key unknown words (TF-IDF + NER)

GET /books/{title}/count_tfidf – raw TF-IDF stats

GET /books/{title} – full metadata + access to all components

## ▶️ Running the System

Install Docker and Docker Compose

Build the backend service

Run all services:

docker compose up -d


## 💬 Contribute / Explore

This repository is a great starting point if you’re interested in:

mobile apps powered by NLP

content enrichment and reading tools

combining server-side analytics with client UI

building learning products on real text data

Feel free to open issues, fork the repo, or reach out for collaboration 🚀

## Links 

Client Repo: https://github.com/StormStudioAndroid2/diploma-client
