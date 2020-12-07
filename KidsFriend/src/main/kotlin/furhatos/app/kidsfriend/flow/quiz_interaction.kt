package furhatos.app.kidsfriend.flow
import furhatos.app.kidsfriend.nlu.Category
import furhatos.app.kidsfriend.nlu.ObjectGuess
import furhatos.app.kidsfriend.nlu.QuizType
import furhatos.app.kidsfriend.nlu.StopPlaying
import furhatos.app.kidsfriend.quizpath
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onResponse
import furhatos.flow.kotlin.state
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes
import java.io.File
import java.util.*


fun TakingQuiz(quizType: String): State = state(Options) {

    var inputStream = File("$quizpath/fruit_info.txt").inputStream()

    if (quizType.equals("animal")) {
        inputStream = File("$quizpath/animals_info.txt").inputStream()
    }

    var lineList = mutableListOf<String>()
    inputStream.bufferedReader().forEachLine { lineList.add(it) }
    var givenClueCnt = 0
    var parts = lineList[Random(System.nanoTime()).nextInt(lineList.size)].split(":").toTypedArray()
    var clueNumber = Random(System.nanoTime()).nextInt(parts.size - 1)
    var objectName = parts[0]
    var clueCnt = parts.size - 1
    var askedForLastTime = false



    onEntry {
        inputStream = File("$quizpath/fruit_info.txt").inputStream()

        if (quizType.equals("animal")) {
            inputStream = File("$quizpath/animals_info.txt").inputStream()
        }

        lineList = mutableListOf<String>()
        inputStream.bufferedReader().forEachLine { lineList.add(it) }
        givenClueCnt = 0
        parts = lineList[Random(System.nanoTime()).nextInt(lineList.size)].split(":").toTypedArray()
        clueNumber = Random(System.nanoTime()).nextInt(parts.size - 1)
        objectName = parts[0]
        clueCnt = parts.size - 1
        askedForLastTime = false


        furhat.say("Ok I'll give you some clues and you will guess what $quizType I'm thinking of.")


        if (givenClueCnt == clueCnt) {
            furhat.say("Sorry, but I don't have more clues!")
        } else {
            furhat.say("Here is the clue: " + parts[1 + clueNumber])
            clueNumber = (clueNumber + 1) % clueCnt
            givenClueCnt++
        }
        furhat.ask("Do you have a guess?")
    }

    onReentry {

        if (askedForLastTime) {
            furhat.say("Fine! The correct answer was $objectName")
           // furhat.say("If you wanted to play, I'll always be here for it! See you again!")
           // goto(Idle)
            goto(PlayAgainOption)

        } else {

            furhat.say("I'll give you another clue and you will guess what $quizType I'm thinking of.")

            if (givenClueCnt == clueCnt) {
                furhat.say("Sorry, but I don't have more clues!")
                askedForLastTime = true

            } else {
                furhat.say("Here is the clue: " + parts[1 + clueNumber])
                clueNumber = (clueNumber + 1) % clueCnt
                givenClueCnt++
            }
            furhat.ask("Do you have a guess?")
        }
    }

    onResponse<Yes> {
        furhat.ask("Great, what is it?")
    }

    onResponse<No> {

        if (askedForLastTime) {

            furhat.say("Fine! The correct answer was $objectName")
            //furhat.say("If you wanted to play, I'll always be here for it! See you again!")
            //goto(Idle)
            goto(PlayAgainOption)

        } else {

            if (givenClueCnt == clueCnt) {
                furhat.say("Sorry, but I don't have more clues!")
                askedForLastTime = true
            } else {
                furhat.say("Fine, I'll give you another clue! Here is the clue: " + parts[1 + clueNumber])
                clueNumber = (clueNumber + 1) % clueCnt
                givenClueCnt++

            }
            furhat.ask("Do you have a guess?")
        }
    }

    onResponse<StopPlaying> {
        furhat.say("You give up? That is ok. The correct answer was $objectName")
        goto(PlayAgainOption)
    }


    onResponse<ObjectGuess> {

        var guess = it.intent.guess

        if (guess == null) {



        } else if (guess.text.toLowerCase().equals(objectName)) {

            furhat.say("That's it! The answer was exactly $objectName")
            //furhat.say("If you wanted to play, I'll always be here for it! See you again!")
            //goto(Idle)
            goto(PlayAgainOption)

        } else {

            furhat.say("You're doing great! But your guess wasn't correct. I'll give you another clue!")

            if (givenClueCnt == clueCnt) {
                furhat.say("Sorry, but I don't have more clues!")
                askedForLastTime = true
            } else {
                furhat.say("Here is the clue: " + parts[1 + clueNumber])
                clueNumber = (clueNumber + 1) % clueCnt
                givenClueCnt++
            }
            furhat.ask("Do you have a guess?")
        }
    }
}

val PlayAgainOption: State = state(Interaction) {
    onEntry {
        random(
                {furhat.ask("Do you want to play again now?")},
                {furhat.ask("Do you want another round?") }
        )
    }
    onResponse<Yes> {
        furhat.say("Great, let's play more")
        goto(SelectQuiz)
    }

    onResponse<No> {
        furhat.say("That is ok, you know where to find me, I'm always here if you want to play.")
        goto(Idle)
    }

}

val SelectQuiz: State = state(Options) {

    onEntry {
        furhat.ask("What do you want to guess? You can pick from ${Category().optionsToText()}")
    }


    onResponse<QuizType> {

        var quizSubject = it.intent.quizSubject

        if (quizSubject == null)
            propagate()

        else {
            furhat.say("${quizSubject}, what a good choice!")
            furhat.say("Let's start!")

            if (quizSubject.text.equals("animal") || quizSubject.text.equals("fruit"))
                goto(TakingQuiz(quizSubject.text.toLowerCase()))
            else
                goto(Options)
        }
    }
}

