package com.okuread.db.util

enum class OkuLanguage(val label: String, val languageType: LanguageType = LanguageType.LATIN_CYRILLIC) {
    ALL("All"),
    ALBANIAN("Albanian"),
    ARABIC("Arabic", LanguageType.ARABIC),
    AZERBAIJANI("Azerbaijani"),
    BASQUE("Basque"),
    BELARUSIAN("Belarusian"),
    BOSNIAN("Bosnian"),
    BULGARIAN("Bulgarian"),
    CATALAN("Catalan"),
    CANTONESE("Cantonese", LanguageType.CHINESE),
    CHINESE("Chinese", LanguageType.CHINESE),
    CROATIAN("Croatian"),
    CZECH("Czech"),
    DANISH("Danish"),
    DUTCH("Dutch"),
    ENGLISH("English"),
    ESPERANTO("Esperanto"),
    ESTONIAN("Estonian"),
    FINNISH("Finnish"),
    FRENCH("French"),
    GALICIAN("Galician"),
    GERMAN("German"),
    HUNGARIAN("Hungarian"),
    ICELANDIC("Icelandic"),
    IRISH("Irish"),
    ITALIAN("Italian"),

    //    JAPANESE("Japanese", LanguageType.JAPANESE),
    KOREAN("Korean", LanguageType.KOREAN),
    KURDISH_KURMANJI("Kurmanji"),
    KURDISH_SORANI("Sorani", LanguageType.ARABIC),
    LATIN("Latin"),
    LATVIAN("Latvian"),
    LITHUANIAN("Lithuanian"),
    LUXEMBOURGISH("Luxembourgish"),
    MALTESE("Maltese"),
    MOLDOVAN("Moldovan"),
    MONTENEGRIN("Montenegrin"),
    NORWEGIAN("Norwegian"),
    PERSIAN("Persian", LanguageType.ARABIC),
    POLISH("Polish"),
    PORTUGUESE("Portuguese"),
    ROMANI("Romani"),
    ROMANIAN("Romanian"),
    RUSSIAN("Russian"),
    SCOTTISH_GAELIC("Scottish Gaelic"),
    SERBIAN("Serbian"),
    SLOVAK("Slovak"),
    SLOVENE("Slovene"),
    SPANISH("Spanish"),
    SWEDISH("Swedish"),
    TAJIK("Tajik"),
    TATAR("Tatar"),
    TURKISH("Turkish"),
    TURKMEN("Turkmen"),
    UKRAINIAN("Ukrainian"),
    URDU("Urdu", LanguageType.ARABIC),
    UZBEK("Uzbek"),
    WELSH("Welsh");

    override fun toString(): String = label

    companion object {
        fun valueByLabel(label: String) = entries.first { it.label == label }
    }
}

enum class LanguageType {
    LATIN_CYRILLIC, ARABIC, CHINESE, KOREAN, JAPANESE
}