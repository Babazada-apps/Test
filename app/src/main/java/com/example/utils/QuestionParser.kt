package com.example.utils

import com.example.data.local.QuestionEntity
import org.json.JSONArray
import org.json.JSONObject

/** İstifadəçi mətnini/JSON-unu suallara çevirir. */
object QuestionParser {

    fun parse(text: String, defaultSection: Int): List<QuestionEntity> {
        val trimmed = text.trim()
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            // JSON kimi görünən mətn səhv formatdadırsa, cümlələrdən "zibil" sual yaratmırıq.
            return parseJson(trimmed, defaultSection)
        }
        return generateFromSentences(trimmed, defaultSection)
    }

    private fun parseJson(text: String, defaultSection: Int): List<QuestionEntity> {
        return try {
            val objects = mutableListOf<JSONObject>()
            if (text.startsWith("{")) {
                objects.add(JSONObject(text))
            } else {
                val array = JSONArray(text)
                for (i in 0 until array.length()) objects.add(array.getJSONObject(i))
            }
            objects.mapNotNull { parseJsonObject(it, defaultSection) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseJsonObject(obj: JSONObject, defaultSection: Int): QuestionEntity? {
        val sual = obj.optString("sual", "").trim()
        val variants = obj.optJSONArray("variantlar")
        val a = (variants?.optString(0) ?: obj.optString("variantA", "")).trim()
        val b = (variants?.optString(1) ?: obj.optString("variantB", "")).trim()
        val c = (variants?.optString(2) ?: obj.optString("variantC", "")).trim()
        val d = (variants?.optString(3) ?: obj.optString("variantD", "")).trim()
        val duzgun = obj.optInt("duzgun", obj.optInt("duzgunCavabIndex", -1))

        if (sual.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty() || duzgun !in 0..3) {
            return null
        }

        val bolmeRaw = obj.optString("bolme", "$defaultSection")
        val bolmeInt = (bolmeRaw.split(".").firstOrNull()?.toIntOrNull() ?: defaultSection).coerceIn(1, 4)
        val tip = obj.optString("tip", "")
        val sualTipi = when (tip) {
            "single_choice" -> "4 Variantlı"
            "true_false" -> "Doğru / Səhv"
            "fill_blank" -> "Boşluğu Doldur"
            else -> obj.optString("sualTipi", "4 Variantlı")
        }

        return QuestionEntity(
            sual = sual,
            variantA = a,
            variantB = b,
            variantC = c,
            variantD = d,
            duzgunCavabIndex = duzgun,
            izah = obj.optString("izah", ""),
            bolme = bolmeInt,
            paraqraf = obj.optString("paraqraf", "§$bolmeRaw"),
            cetinlik = obj.optString("cetinlik", "Orta"),
            sualTipi = sualTipi
        )
    }

    private fun generateFromSentences(text: String, section: Int): List<QuestionEntity> {
        val sentences = text.split(".", "!", "\n", ";")
            .map { it.trim() }
            .filter { it.length >= 15 }

        return sentences.take(10).mapIndexed { idx, sentence ->
            val base = QuestionEntity(
                sual = "Mətnə əsasən aşağıdakı hökm doğrudurmu?\n\"$sentence\"",
                variantA = "Bəli, doğrudur",
                variantB = "Xeyr, yanlışdır",
                variantC = "Təlimatda bu barədə məlumat yoxdur",
                variantD = "Mətndən müəyyən etmək mümkün deyil",
                duzgunCavabIndex = 0,
                izah = "Təlimat mətnində qeyd olunmuşdur: $sentence",
                bolme = section,
                paraqraf = "§$section.${idx + 1}",
                cetinlik = "Orta",
                sualTipi = "Doğru / Səhv"
            )
            QuizLogic.shuffleOptions(base)
        }
    }
}
