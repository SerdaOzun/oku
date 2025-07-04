package com.okuread

import com.okuread.db.data.OkuText
import com.okuread.db.repositories.OkuTextEntity
import com.okuread.db.repositories.OkuWordEntity
import com.okuread.db.util.OkuLanguage
import com.okuread.services.ReadingService
import com.okuread.textprocessing.processOkuText
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

fun deleteDatabase() {
    transaction {
        OkuTextEntity.deleteAll()
        OkuWordEntity.deleteAll()
    }
}

fun populateDatabaseWithTestData() {
    var loreIpsumText =
        "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."
    repeat(5) {
        loreIpsumText += loreIpsumText
    }

    val loremIpsum = OkuText(
        id = null,
        title = "Lorem ipsum",
        body = loreIpsumText,
        wordList = processOkuText(loreIpsumText, OkuLanguage.LATIN),
        timestampCreated = LocalDateTime.now(),
        timestampFinished = null,
        language = OkuLanguage.LATIN
    )

    val snoopDogText =
        "En couvrant les Jeux olympiques pour la chaîne américaine NBC, le rappeur devenu mascotte à plein temps aura joui d’un inattendu retour de hype. Et fait preuve d’une liberté de mouvement suffisamment efficace pour embarquer tout le monde dans son cirque.\n"
    val snoopDog = OkuText(
        id = null,
        title = "Aux JO de Paris 2024, Snoop Dogg y a mis du chien",
        body = snoopDogText,
        wordList = processOkuText(snoopDogText, OkuLanguage.FRENCH),
        timestampCreated = LocalDateTime.now(),
        timestampFinished = null,
        language = OkuLanguage.FRENCH
    )

    val psychanalyseText =
        "Tous, ou presque, en thérapie ? C’est à coup sûr le pays qui compte le plus de « psys », la « patrie symbolique » de Jacques Lacan. Les liens historiques entre Argentine et Europe ne suffisent pas à expliquer une longue hégémonie… aujourd’hui remise en cause par d’autres formes de traitement de la souffrance psychique, voire par le développement personnel." +
                "Il y en a à tous les coins de rue. María Bondoni, 33 ans et passionnée de littérature, accueille ses patients sur un canapé gris et austère, orné de quelques coussins jaune et rouge, tout près d’une petite place au pied d’une église. Cinq minutes de marche plus loin, dans une rue parallèle, Nora Silvestri, la soixantaine, propose une thérapie d’orientation lacanienne, au premier étage d’un élégant immeuble haussmannien, à l’ombre d’un jacaranda. Quant à Lucila Aranda, qui se décrit comme « féministe » et « péroniste » sur son compte Instagram, elle est spécialiste des crises d’angoisse qu’elle traite dans son cabinet de l’avenue Santa Fe, à trois cents mètres de là.\n" +
                "\n" +
                "Le vaste choix de « psys » qui exercent dans ce petit quartier bourgeois de Buenos Aires, surnommé la « Villa Freud », témoigne de la passion argentine pour le divan. Selon des statistiques de l’Organisation mondiale de la santé (OMS) de 2016, l’Argentine compte le plus grand nombre de psychologues par habitant au monde : 222 pour 100 000 personnes, soit au moins quatre fois plus qu’en France.\n" +
                "\n" +
                "En Argentine, prendre soin de sa santé mentale n’est pas un tabou. « Ici tu es bizarre si tu ne te fais pas analyser », explique M. Ezequiel Berretta. Vendeur à Letra Viva, une librairie incontournable du quartier, l’homme nous tend un livre récemment publié et intitulé ¿ Qué es esa cosa llamada psicoanálisis ? (Cascada de Letras, 2023, « Quelle est cette chose appelée psychanalyse ? »). Écrit par le psychanalyste argentin Hernán José Molina, il vulgarise les concepts de base de la discipline : « inconscient », « blessures narcissiques », « abréaction », « complexe d’Œdipe », « pulsion », « attention flottante », « suggestion », « resignification », « association libre » ou encore « transfert ». Selon M. Berretta, la maîtrise de ce vocabulaire est indispensable pour s’intégrer à la vie « porteña » (de Buenos Aires). « Si vous vous posez deux heures sur le banc de la place Güemes [au cœur du quartier] pour écouter les gens discuter, vous (...)"

    val psychanalyse = OkuText(
        id = null,
        title = "Buenos Aires, capitale de la psychanalyse",
        body = psychanalyseText,
        wordList = processOkuText(psychanalyseText, OkuLanguage.FRENCH),
        timestampCreated = LocalDateTime.now(),
        timestampFinished = null,
        language = OkuLanguage.FRENCH
    )

    val persianText =
        "کسی نیست که از اواسط بهار، دقیقا همان موقع که وزیدن نسیم خنک فروکش می\u200Cکند و ابرهای باران\u200Cزا محو می\u200Cشوند، انتظار گرمای تابستان را نداشته باشد، همه می\u200Cدانند که این فصل با آفتاب سوزانش از راه می\u200Cرسد، اما خیلی\u200Cها با گرمای عجیب تابستان امسال غافلگیر شدند."
    val persian = OkuText(
        id = null,
        title = "شهرهای ایران هم آمپر چسباندند!",
        body = persianText,
        wordList = processOkuText(persianText, OkuLanguage.PERSIAN),
        timestampCreated = LocalDateTime.now(),
        timestampFinished = null,
        language = OkuLanguage.PERSIAN
    )

    val readingService: ReadingService = getKoinInstance()

    transaction {
        readingService.insertText(loremIpsum)
        readingService.insertText(snoopDog)
        readingService.insertText(psychanalyse)
        readingService.insertText(persian)
        repeat(15) {
            readingService.insertText(psychanalyse)
        }
    }
}