package com.example.data.local

import android.content.Context
import org.json.JSONArray

/**
 * Daxili sual bazası assets/questions.json faylından yüklənir.
 *
 * ID-lər massivdəki sıraya görə təyin olunur (1-dən başlayaraq), buna görə də yeni sualları
 * YALNIZ faylın sonuna əlavə edin və SEED_VERSION-u artırın. İstifadəçinin əlavə etdiyi suallar
 * CUSTOM_ID_START-dan başlayan ID-lər alır ki, daxili suallarla toqquşmasın.
 */
object InitialData {
    /** assets/questions.json dəyişəndə bu rəqəmi artırın ki, yeniləmə tətbiq olunsun. */
    const val SEED_VERSION = 1
    const val CUSTOM_ID_START = 100000

    fun getInitialQuestions(context: Context): List<QuestionEntity> {
        val text = context.assets.open("questions.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
        val array = JSONArray(text)
        val result = ArrayList<QuestionEntity>(array.length())
        for (i in 0 until array.length()) {
            val o = array.getJSONObject(i)
            val v = o.getJSONArray("variantlar")
            result.add(
                QuestionEntity(
                    id = i + 1,
                    sual = o.getString("sual"),
                    variantA = v.getString(0),
                    variantB = v.getString(1),
                    variantC = v.getString(2),
                    variantD = v.getString(3),
                    duzgunCavabIndex = o.getInt("duzgun"),
                    izah = o.getString("izah"),
                    bolme = o.getInt("bolme"),
                    paraqraf = o.getString("paraqraf"),
                    cetinlik = o.getString("cetinlik"),
                    sualTipi = o.getString("sualTipi")
                )
            )
        }
        return result
    }
}
