package com.example.proyectofinal.data.parser

import com.example.proyectofinal.data.model.Movie
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class M3UParser {
    fun parse(inputStream: InputStream): List<Movie> {
        val movies = mutableListOf<Movie>()
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String? = reader.readLine()
        
        var currentTitle: String? = null
        var currentLogo: String? = null
        var currentCategory: String = "General"
        
        while (line != null) {
            line = line.trim()
            if (line.startsWith("#EXTINF")) {
                // Parse metadata
                val logoMatch = Regex("tvg-logo=\"([^\"]*)\"").find(line)
                currentLogo = logoMatch?.groupValues?.get(1)
                
                val groupMatch = Regex("group-title=\"([^\"]*)\"").find(line)
                currentCategory = groupMatch?.groupValues?.get(1) ?: "General"
                
                val titleMatch = line.substringAfterLast(",")
                currentTitle = titleMatch.trim()
            } else if (line.isNotEmpty() && !line.startsWith("#")) {
                // URL line
                if (currentTitle != null) {
                    movies.add(Movie(
                        title = currentTitle,
                        logo = currentLogo,
                        url = line,
                        category = currentCategory
                    ))
                    currentTitle = null
                    currentLogo = null
                    currentCategory = "General"
                }
            }
            line = reader.readLine()
        }
        return movies
    }
}
