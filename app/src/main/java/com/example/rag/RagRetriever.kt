package com.example.rag

import com.example.data.local.DocumentChunkEntity
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

object RagRetriever {

    private val STOP_WORDS = setOf(
        "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are",
        "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both",
        "but", "by", "can", "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does",
        "doesn't", "doing", "don't", "down", "during", "each", "few", "for", "from", "further", "had",
        "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her",
        "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd",
        "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself",
        "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off",
        "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own",
        "same", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so", "some", "such",
        "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there",
        "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those", "through",
        "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're",
        "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which",
        "while", "who", "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you",
        "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves"
    )

    fun retrieve(
        query: String,
        chunks: List<DocumentChunkEntity>,
        topK: Int = 3
    ): RetrievalResult {
        val startTime = System.currentTimeMillis()

        if (chunks.isEmpty() || query.isBlank()) {
            return RetrievalResult(
                chunks = emptyList(),
                retrievalTimeMs = System.currentTimeMillis() - startTime,
                totalChunksSearched = chunks.size
            )
        }

        // Tokenize query
        val rawQueryTokens = tokenize(query)
        val queryTokens = rawQueryTokens.filter { it !in STOP_WORDS }
        val effectiveQueryTokens = if (queryTokens.isEmpty()) rawQueryTokens else queryTokens

        val nDocs = chunks.size
        val avgDocLen = chunks.map { tokenize(it.text).size }.average().takeIf { it > 0 } ?: 1.0

        // Inverted document frequency: count how many chunks contain each term
        val termDocCounts = mutableMapOf<String, Int>()
        val chunkTokenLists = chunks.map { tokenize(it.text) }

        for (token in effectiveQueryTokens) {
            val count = chunkTokenLists.count { it.contains(token) }
            termDocCounts[token] = count
        }

        val k1 = 1.2f
        val b = 0.75f

        val scoredChunks = chunks.mapIndexed { index, chunk ->
            val docTokens = chunkTokenLists[index]
            val docLen = docTokens.size
            var bm25Score = 0.0f
            val matched = mutableListOf<String>()

            // Term frequency in this chunk
            val tfMap = mutableMapOf<String, Int>()
            for (t in docTokens) {
                tfMap[t] = (tfMap[t] ?: 0) + 1
            }

            for (term in effectiveQueryTokens) {
                val tf = tfMap[term] ?: 0
                if (tf > 0) {
                    matched.add(term)
                    val df = termDocCounts[term] ?: 1
                    val idf = ln(((nDocs - df + 0.5f) / (df + 0.5f)) + 1.0f)
                    val numerator = tf * (k1 + 1.0f)
                    val denominator = tf + k1 * (1.0f - b + b * (docLen / avgDocLen.toFloat()))
                    bm25Score += max(0.0f, (idf * (numerator / denominator)).toFloat())
                }
            }

            // Exact phrase or bigram bonus
            val cleanChunkText = chunk.text.lowercase()
            val cleanQuery = query.lowercase().trim()
            if (cleanChunkText.contains(cleanQuery) && cleanQuery.length > 3) {
                bm25Score += 3.0f
            }

            // Check specific numbers or alphanumeric codes (e.g. ERR-804, 1.4, QX-900, 9421)
            val codeRegex = Regex("[A-Za-z0-9\\-]+")
            for (match in codeRegex.findAll(query)) {
                val word = match.value.lowercase()
                if (word.any { it.isDigit() } && cleanChunkText.contains(word)) {
                    bm25Score += 2.5f
                    if (word !in matched) matched.add(word)
                }
            }

            // Coverage ratio bonus
            val coverage = if (effectiveQueryTokens.isNotEmpty()) {
                matched.distinct().size.toFloat() / effectiveQueryTokens.distinct().size.toFloat()
            } else 0f
            val finalScore = bm25Score * (1.0f + 0.5f * coverage)

            Pair(chunk, ScoredData(finalScore, matched.distinct()))
        }

        val maxScore = scoredChunks.maxOfOrNull { it.second.score } ?: 1.0f
        val validResults = scoredChunks
            .filter { it.second.score > 0.05f }
            .sortedByDescending { it.second.score }
            .take(topK)
            .map { (chunk, data) ->
                // Normalize score between 0.4 and 0.99 for display if maxScore > 0
                val normalizedScore = if (maxScore > 0f) {
                    min(0.99f, max(0.40f, data.score / maxScore))
                } else 0.5f

                RetrievedChunk(
                    chunkId = chunk.id,
                    documentTitle = chunk.documentTitle,
                    chunkIndex = chunk.chunkIndex,
                    text = chunk.text,
                    score = normalizedScore,
                    matchedTerms = data.matchedTerms
                )
            }

        val elapsed = System.currentTimeMillis() - startTime

        return RetrievalResult(
            chunks = validResults,
            retrievalTimeMs = elapsed,
            totalChunksSearched = chunks.size
        )
    }

    private fun tokenize(text: String): List<String> {
        return text.lowercase()
            .replace(Regex("[^a-z0-9\\-\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.length > 1 }
    }

    private data class ScoredData(val score: Float, val matchedTerms: List<String>)
}
