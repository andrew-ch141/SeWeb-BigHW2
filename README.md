<!-- README.md -->
# Semantic Web Homework 2 - Book Recommendation System

Team members:
- Mazilu Stefan
- Chiar Andrei-Cristian

Public GitHub repository: https://github.com/andrew-ch141/SeWeb-BigHW2

## Contributions

Mazilu Stefan: RDF/XML modeling, Apache Jena book operations, book list/detail pages, and RDF graph visualization.

Chiar Andrei-Cristian: OWL ontology, SPARQL queries, Elasticsearch vector indexing, and the RAG chatbot.


## What The Project Contains

- RDF/XML data for Alice, Bob, Dune, The Silent Patient, and Hunger Games.
- Web upload page that visualizes RDF triples as a graph.
- Book add/edit/list/detail pages backed by Apache Jena and RDF/XML.
- OWL ontology for the book recommendation domain.
- Five SPARQL queries in `sparql_owl.txt`.
- Floating chatbot on every page.
- Context-aware chat starters.
- RAG flow that indexes RDF book/user data into Elasticsearch and sends retrieved facts to an OpenAI-compatible LLM.

## Requirements

- Java 17
- Maven
- Docker Desktop for Elasticsearch
- LM Studio or another OpenAI-compatible API

## Run The App

1. Start Elasticsearch:

```powershell
docker compose up -d
```

2. Start LM Studio and load the chat model and embedding model.

3. Run the Spring Boot app:

```powershell
mvn spring-boot:run
```

4. Open the app:

```text
http://localhost:8080
```

5. Click `Rebuild Vector Index` on the home page before testing the chatbot.

## Chatbot And LM Studio

The chatbot uses a RAG flow. Book and user data from the RDF file is transformed into text facts, embedded, and stored in Elasticsearch.

For local LLM testing we used LM Studio with:
- a small instruct chat model, for example `Llama 3.2 1B Instruct`
- `text-embedding-nomic-embed-text-v1.5` for embeddings

LM Studio should run its local OpenAI-compatible server at:

```text
http://localhost:1234/v1
```

Before using the chatbot, start Elasticsearch and click `Rebuild Vector Index` in the application.

## OWL And SPARQL

Use `src/main/resources/data/book-ontology.owl` in Protégé.

We ran the five queries from `sparql_owl.txt`.

We saved the required screenshots in the `screenshots` folder.

