package com.okuread.util

import com.okuread.db.data.OkuWord

fun OkuWord.isSkippableWord(): Boolean = word.isSkippableWord()
fun String.isSkippableWord(): Boolean = this.isBlank() || this == System.lineSeparator() || this == "\u200C" || this == "\u00AD"
fun String.isLineBreak(): Boolean = this == System.lineSeparator()