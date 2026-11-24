package com.tallyho.scorekeeper

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject

/** Score is kept in half-points as an Int so there's no floating-point drift. */
data class Team(
    val id: Long,
    val name: String,
    val defaultName: String,
    val halves: Int = 0,
) {
    val displayName: String get() = name.ifBlank { defaultName }
}

/**
 * Holds all app state and writes it to the app's private SharedPreferences on every
 * change, so a game survives rotation or the app being closed. Needs no permissions.
 */
class GameStore(context: Context) {
    private val prefs = context.getSharedPreferences("tallyho", Context.MODE_PRIVATE)

    val teams = mutableStateListOf<Team>()
    var phase by mutableStateOf(Phase.SETUP)
        private set
    private var nextNumber = 1
    private var nextId = 1L

    init {
        load()
        if (teams.isEmpty()) {
            addTeam()
            addTeam()
        }
    }

    fun addTeam() {
        teams.add(Team(id = nextId++, name = "", defaultName = "Team ${nextNumber++}"))
        save()
    }

    fun removeTeam(id: Long) {
        teams.removeAll { it.id == id }
        save()
    }

    fun rename(id: Long, name: String) = update(id) { it.copy(name = name) }

    fun changeScore(id: Long, deltaHalves: Int) =
        update(id) { it.copy(halves = it.halves + deltaHalves) }

    fun startGame() {
        for (i in teams.indices) teams[i] = teams[i].copy(halves = 0)
        phase = Phase.GAME
        save()
    }

    fun resetScores() {
        for (i in teams.indices) teams[i] = teams[i].copy(halves = 0)
        save()
    }

    /** Ends the game and shows the results; scores stay until the next game starts. */
    fun endGame() {
        phase = Phase.RESULTS
        save()
    }

    fun backToSetup() {
        phase = Phase.SETUP
        save()
    }

    /** Teams sorted by score, highest first, with tied teams sharing a place (1, 1, 3…). */
    fun standings(): List<Pair<Int, Team>> {
        val sorted = teams.sortedByDescending { it.halves }
        return sorted.map { team ->
            val place = sorted.indexOfFirst { it.halves == team.halves } + 1
            place to team
        }
    }

    private inline fun update(id: Long, change: (Team) -> Team) {
        val i = teams.indexOfFirst { it.id == id }
        if (i >= 0) {
            teams[i] = change(teams[i])
            save()
        }
    }

    private fun save() {
        val arr = JSONArray()
        teams.forEach {
            arr.put(
                JSONObject()
                    .put("id", it.id)
                    .put("name", it.name)
                    .put("defaultName", it.defaultName)
                    .put("halves", it.halves)
            )
        }
        prefs.edit()
            .putString("teams", arr.toString())
            .putString("phase", phase.name)
            .putInt("nextNumber", nextNumber)
            .putLong("nextId", nextId)
            .apply()
    }

    private fun load() {
        nextNumber = prefs.getInt("nextNumber", 1)
        nextId = prefs.getLong("nextId", 1L)
        // "inGame" is the pre-1.0.3 key; keep honouring it so an in-progress game survives the update.
        phase = prefs.getString("phase", null)?.let { runCatching { Phase.valueOf(it) }.getOrNull() }
            ?: if (prefs.getBoolean("inGame", false)) Phase.GAME else Phase.SETUP
        val raw = prefs.getString("teams", null) ?: return
        runCatching {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                teams.add(
                    Team(
                        id = o.getLong("id"),
                        name = o.optString("name"),
                        defaultName = o.optString("defaultName", "Team ${i + 1}"),
                        halves = o.optInt("halves"),
                    )
                )
            }
        }
        if (teams.size < 2) phase = Phase.SETUP
    }
}

enum class Phase { SETUP, GAME, RESULTS }

/** 15 halves -> "7½", -1 -> "−½", 0 -> "0". */
fun formatHalves(halves: Int): String {
    val abs = kotlin.math.abs(halves)
    val whole = abs / 2
    val hasHalf = abs % 2 == 1
    val sign = if (halves < 0) "−" else ""
    val wholePart = if (whole == 0 && hasHalf) "" else whole.toString()
    return sign + wholePart + if (hasHalf) "½" else ""
}
