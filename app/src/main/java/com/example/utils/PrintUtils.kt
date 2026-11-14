package com.example.utils

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.data.local.QuestionEntity

object PrintUtils {

    private fun getOptionLetter(index: Int): String {
        return when (index) {
            0 -> "A"
            1 -> "B"
            2 -> "C"
            3 -> "D"
            else -> "A"
        }
    }

    fun printQuestions(context: Context, questions: List<QuestionEntity>, documentTitle: String = "Seçilmiş Suallar") {
        if (questions.isEmpty()) {
            Toast.makeText(context, "Çap ediləcək sual yoxdur!", Toast.LENGTH_SHORT).show()
            return
        }

        val webView = WebView(context)
        val htmlBuilder = StringBuilder()

        htmlBuilder.append("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <title>$documentTitle</title>
                <style>
                    @page {
                        margin: 12mm;
                    }
                    body {
                        font-family: Arial, Helvetica, sans-serif;
                        margin: 0;
                        padding: 10px;
                        color: #0f172a;
                        line-height: 1.4;
                        background-color: #ffffff;
                    }
                    .header {
                        text-align: center;
                        padding-bottom: 10px;
                        border-bottom: 2px solid #1b4d3e;
                        margin-bottom: 16px;
                    }
                    .header h1 {
                        font-size: 18px;
                        color: #1b4d3e;
                        margin: 0 0 4px 0;
                        text-transform: uppercase;
                    }
                    .header p {
                        font-size: 12px;
                        color: #64748b;
                        margin: 0;
                    }
                    .question-box {
                        border: 1px solid #cbd5e1;
                        border-radius: 6px;
                        padding: 10px 14px;
                        margin-bottom: 12px;
                        page-break-inside: avoid;
                        background-color: #ffffff;
                    }
                    .meta-badge {
                        display: inline-block;
                        background-color: #f1f5f9;
                        color: #475569;
                        font-size: 10px;
                        font-weight: bold;
                        padding: 2px 6px;
                        border-radius: 4px;
                        margin-bottom: 6px;
                    }
                    .question-number {
                        font-size: 11px;
                        font-weight: bold;
                        color: #0284c7;
                        margin-bottom: 4px;
                    }
                    .question-text {
                        font-size: 13px;
                        font-weight: bold;
                        color: #0f172a;
                        margin-bottom: 8px;
                    }
                    .options-list {
                        display: flex;
                        flex-direction: column;
                        gap: 4px;
                    }
                    .option-item {
                        font-size: 12px;
                        padding: 5px 8px;
                        border-radius: 4px;
                        border: 1px solid #e2e8f0;
                        background-color: #f8fafc;
                        color: #1e293b;
                    }
                    /* Separate Answer Key Page */
                    .answer-key-page {
                        page-break-before: always;
                        break-before: page;
                        padding-top: 10px;
                    }
                    .answer-key-header {
                        text-align: center;
                        border-bottom: 2px solid #1b4d3e;
                        padding-bottom: 8px;
                        margin-bottom: 16px;
                    }
                    .answer-key-header h2 {
                        font-size: 16px;
                        color: #1b4d3e;
                        margin: 0 0 4px 0;
                        text-transform: uppercase;
                    }
                    .answer-grid {
                        display: grid;
                        grid-template-columns: repeat(5, 1fr);
                        gap: 8px;
                    }
                    .answer-item {
                        border: 1px solid #cbd5e1;
                        border-radius: 4px;
                        padding: 6px;
                        text-align: center;
                        font-size: 12px;
                        background-color: #f8fafc;
                    }
                    .answer-num {
                        font-weight: bold;
                        color: #334155;
                    }
                    .answer-letter {
                        font-weight: bold;
                        color: #1b4d3e;
                        margin-left: 4px;
                    }
                    .footer {
                        text-align: center;
                        font-size: 10px;
                        color: #94a3b8;
                        margin-top: 24px;
                        border-top: 1px solid #e2e8f0;
                        padding-top: 8px;
                    }
                    @media print {
                        .question-box {
                            page-break-inside: avoid;
                        }
                        .answer-key-page {
                            page-break-before: always;
                            break-before: page;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>Ümumi Aviasiya Aviasiya Bilikləri üzrə Təlimat</h1>
                    <p><strong>$documentTitle</strong> — Cəmi: ${questions.size} sual</p>
                </div>
        """.trimIndent())

        // Render Questions sequentially without revealing answers
        questions.forEachIndexed { index, q ->
            val variants = listOf(
                "A" to q.variantA,
                "B" to q.variantB,
                "C" to q.variantC,
                "D" to q.variantD
            )

            htmlBuilder.append("""
                <div class="question-box">
                    <div class="meta-badge">Bölmə ${q.bolme} &bull; Paraqraf: ${q.paraqraf}</div>
                    <div class="question-number">SUAL #${index + 1}</div>
                    <div class="question-text">${q.sual}</div>
                    <div class="options-list">
            """.trimIndent())

            variants.forEach { (label, text) ->
                htmlBuilder.append("""
                    <div class="option-item">
                        <strong>$label)</strong> $text
                    </div>
                """.trimIndent())
            }

            htmlBuilder.append("</div></div>")
        }

        // Render Answer Key on a separate final page
        htmlBuilder.append("""
            <div class="answer-key-page">
                <div class="answer-key-header">
                    <h2>DÜZGÜN CAVABLAR (CAVAB AÇARI)</h2>
                    <p>Ümumi Aviasiya Təlimatı — ${questions.size} sualın cavabları</p>
                </div>
                <div class="answer-grid">
        """.trimIndent())

        questions.forEachIndexed { index, q ->
            val correctLetter = getOptionLetter(q.duzgunCavabIndex)
            htmlBuilder.append("""
                <div class="answer-item">
                    <span class="answer-num">${index + 1})</span>
                    <span class="answer-letter">$correctLetter</span>
                </div>
            """.trimIndent())
        }

        htmlBuilder.append("""
                </div>
                <div class="footer">
                    Ümumi Aviasiya Təlimat Test Tətbiqi vasitəsilə hazırlanmışdır.
                </div>
            </div>
            </body>
            </html>
        """.trimIndent())

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                if (printManager != null) {
                    val printAdapter = webView.createPrintDocumentAdapter("Mi17_Secilmis_Suallar")
                    val jobName = "Ümumi Aviasiya Seçilmiş Suallar (${questions.size})"
                    printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
                } else {
                    Toast.makeText(context, "Çap xidməti tapılmadı", Toast.LENGTH_SHORT).show()
                }
            }
        }

        webView.loadDataWithBaseURL(null, htmlBuilder.toString(), "text/html", "UTF-8", null)
    }
}

