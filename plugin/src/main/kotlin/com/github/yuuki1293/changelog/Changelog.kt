package com.github.yuuki1293.changelog

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Changelog(
    val rawText: String,
    val versionRegex: Regex = DEFAULT_VERSION_REGEX,
    val versionIndex: Int = DEFAULT_VERSION_INDEX,
    val dateIndex: Int = DEFAULT_DATE_INDEX,
    val pattern: String = DEFAULT_PATTERN,

) {
    companion object {
        /**
         * The default versionRegex
         */
        val DEFAULT_VERSION_REGEX = Regex("""^##\s+\[\s*(.+)\s*]\s*(-\s*(\d{4}-\d{2}-\d{2}))?""")

        /**
         * The default versionIndex
         */
        const val DEFAULT_VERSION_INDEX = 1

        /**
         * The default date index
         */
        const val DEFAULT_DATE_INDEX = 3

        /**
         * The default pattern used by date parser
         */
        const val DEFAULT_PATTERN = "yyyy-MM-dd"

        /**
         * Empty instance.
         */
        val EMPTY = Changelog("")
    }

    private var dataCache: List<Data>? = null

    /**
     * get all lines.
     */
    fun getLines(): List<String> = rawText.split(System.lineSeparator())

    /**
     * get all listed versions
     */
    fun getVersions(): List<String> {
        return getData().map { it.version }
    }

    /**
     * Extract line index, version slags, and date
     */
    fun getData(): List<Data> {
        if (dataCache != null) return dataCache!!

        val lines = getLines()

        val smelted = lines
            .mapIndexedNotNull { i, line ->
                val d = extractDataFromLine(line) ?: return@mapIndexedNotNull null
                Triple(i, d.first, d.second) // index, version, date
            }

        dataCache = smelted.mapIndexed { i, s ->
            val start = s.first
            val version = s.second
            val date = s.third
            val end = smelted.getOrNull(i + 1)?.first ?: (lines.count() - 1)
            val text = lines.slice(start..<end).joinToString(System.lineSeparator())
            val header = lines[0]
            val body = text.drop(header.length)
            Data(start, version, date, text, header, body)
        }

        return dataCache!!
    }

    private fun extractDataFromLine(line: String): Pair<String, LocalDate?>? {
        val match = versionRegex.find(line) ?: return null

        val groups = match.groupValues
        val versionStr = groups.getOrElse(versionIndex) { _ -> "" } // extract version slag

        val dateStr = groups.getOrNull(dateIndex) ?: return Pair(versionStr, null)
        val date = try {
            LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern))
        } catch (e: Exception) {
            null // 日付が無効な場合はnull
        }

        return Pair(versionStr, date)
    }

    fun isEmpty(): Boolean = rawText.isEmpty()

    data class Data(
        val index: Int,
        val version: String,
        val date: LocalDate?,
        val text: String,
        val header: String,
        val body: String
    ) {
        override fun toString(): String = text
    }
}
