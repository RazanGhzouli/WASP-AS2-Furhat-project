package furhatos.app.kidsfriend.flow

import furhatos.flow.kotlin.*
import java.io.File
import java.io.InputStream
import furhatos.app.kidsfriend.lyricpath
import furhatos.gestures.Gestures
import furhatos.app.kidsfriend.stringSimilarity
import furhatos.app.kidsfriend.nlu.*
import furhatos.nlu.common.Maybe
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes

val Start: State = state(Interaction) {

    onEntry {
        random(
                {furhat.say("Hello there! I am Fred" )},
                {furhat.say("Hi my friend! I am Fred" )},
                {furhat.say("Hey You! I am Fred" )}
        )

        goto(SelectMode)

    }
}

val Options = state(Interaction) {
    onResponse<Singing> {
        val song = it.intent.song
        if (song != null) {
                goto(SelectSong(song))
        }
        else {
            furhat.say("I can sing ${Song().optionsToText()} ")
            furhat.ask("What do you want?")
            //propagate()
        }
    }

    onResponse<Yes> {
        random(
                { furhat.ask("Do you want a song or a guessing game?") },
                { furhat.ask("Yey! Do you want a play a guessing game or sing a song?") }
        )
    }

    onResponse<QuizType> {
        val quizSubject = it.intent.quizSubject
        if (quizSubject != null) {
            goto(TakingQuiz(quizSubject.text.toLowerCase()))
        } else {
            random(
                    { furhat.say("A guessing game is the perfect choice.") },
                    { furhat.say("A guessing game it is!") }
            )
            goto(SelectQuiz)
        }
    }
/*    onResponse<RequestOptions> {
        *//*furhat.say("Great! I can ${Mode().optionsToText()}")*//*
        furhat.ask("What do you want ${Mode().optionsToText()}? ")
    }*/
// my changes

    onResponse<Yes>{
/*
        furhat.say("Great! I can ${Mode().optionsToText()}")
*/
        furhat.ask("I can ${Mode().optionsToText()}. Which one do you want? ")
    }




}

val SelectMode = state(Options) {
    onEntry {
        random(
                {furhat.ask("Do you want to sing or play a guessing game?")},
                {furhat.ask("shall we sing or play a guessing game?")},
                {furhat.ask("Would you like to sing or play a guessing game with me?")}
        )
    }
    // my changes
    onReentry {
        random(
                {furhat.ask("Hi again, Do you want to sing or play a quiz?")},
                {furhat.ask("Good to see you again, shall we sing or play a quiz?")}
        )
    }

    onResponse<No> {
        furhat.say("Okay, that's sad. Have a splendid day!")
        goto(Idle)
    }
    onNoResponse {

        furhat.say("It seems you don't want to play! Good bye and join me again if you want to play!")
        goto(Idle)
    }
}

fun SelectSong(song: Song): State = state(Options) {
    onEntry {
        furhat.say("${song}, what a lovely song!")
        furhat.ask("Do you know it? If you don't just say maybe and we can sing together, or say no and I can sing it for you.")
    }
    onResponse<No> {
        furhat.say("Okay, I will sing the whole song for you!")
        goto(SingWholeSong(song))
    }
    // my changes
    onResponse<Maybe> {
        furhat.say("Okay, Let's take turn singing it!")
        goto(SingAlternately(song))
    }

    onResponse<Yes> {
        furhat.say("Okay, I will cover when you forget the lyric!")
        goto(SingAlong(song))
    }
}

fun SingWholeSong(song: Song): State = state(Options) {
    val songString = song.toString().toLowerCase()
//    val inputStream: InputStream = File("tmp.txt").inputStream()
    val inputStream: InputStream = File("$lyricpath/$songString.txt").inputStream()
    val audioList = mutableListOf<String>()
    val lyricList = mutableListOf<String>() // probably nicer way to do with a list of lists
    onEntry {
        inputStream.bufferedReader().forEachLine {

            audioList.add(it.split("; ")[0]);
            lyricList.add(it.split("; ")[1]);
        }

        for (i in 0 until audioList.size) {
            furhat.gesture(Gestures.Roll(0.9, 1.3,2), async = true)
            furhat.say({
                +Audio(audioList[i], lyricList[i])

            })

        }
        goto(SelectMode)


    }


}

fun SingAlternately(song: Song, lineCounter: Int = 0): State = state(Options) {
    val songString = song.toString().toLowerCase()
//    val inputStream: InputStream = File("tmp.txt").inputStream()
    val inputStream: InputStream = File("$lyricpath/$songString.txt").inputStream()
    val audioList = mutableListOf<String>()
    val lyricList = mutableListOf<String>() // probably nicer way to do with a list of lists
    inputStream.bufferedReader().forEachLine {
        audioList.add(it.split("; ")[0]);
        lyricList.add(it.split("; ")[1]);
    }

    onEntry {


        if (lineCounter < audioList.size-1) {
            furhat.gesture(Gestures.Roll(0.9, 1.3,2), async = true)
            furhat.ask({ +Audio(audioList[lineCounter], lyricList[lineCounter]) })
        }
        else if (lineCounter == audioList.size-1) {
            furhat.say({+Audio(audioList[lineCounter], lyricList[lineCounter])})
            furhat.say("Great! What a collaboration!")
            goto(SelectMode)
        }
        else {
            furhat.say("Maybe you need some help with this song!")
            goto(SingWholeSong(song))
        }

    }




    onResponse {
        var responseText: String = it.text.toLowerCase()
        var lyricLine = lyricList[lineCounter + 1].toLowerCase()
        var similarityScore = stringSimilarity(responseText, lyricLine)
        if (similarityScore >= 0.2) { //set a low threshold for a kid ;)
            if (lyricList.size-lineCounter > 2 ) goto(SingAlternately(song,lineCounter+2))
            else { //on the last line
                furhat.say("Bravo! What a good singer!")
                goto(SelectMode)
            }
        }
        else if (similarityScore < 0.2&& similarityScore >= 0.1){
            furhat.say("Maybe you need some help with this song!")
            goto(OfferSinghelp(song))

/*                furhat.say("Probably my hearing is old, I couldn't understand what you said.")
                furhat.ask("Could you repeat it?")*/


            }
        else {
            furhat.say("Maybe we need to change the game!")
            goto(SelectMode)

        }
/*
            val similarityPercent = similarityScore * 100.0
*/
/*
            furhat.say("Probably my voice recognition is not perfect, it is ${similarityPercent.toInt()} similar.")
*/

        }



}


fun SingAlong(song: Song, lineCounter: Int = 0, score: Double = 0.0) : State = state(Options){
    //Limitation: Someone should sing it line per line
    val songString = song.toString().toLowerCase()
    val inputStream: InputStream = File("$lyricpath/$songString.txt").inputStream()
    val audioList = mutableListOf<String>()
    val lyricList = mutableListOf<String>() // probably nicer way to do with a list of lists
    inputStream.bufferedReader().forEachLine {
        audioList.add(it.split("; ")[0]);
        lyricList.add(it.split("; ")[1]);
    }


    onEntry {
        if (lineCounter >= audioList.size) {
            val finalScore = score * 100.0
            if (score >= 0.95) {
                furhat.gesture(Gestures.BigSmile,async = true)
                furhat.say("Awesome! You sing it perfectly!")
                furhat.gesture(Gestures.Wink(1.0),async = false)
            }
            else if (score >= 0.7) {
                furhat.gesture(Gestures.BigSmile,async = true)
                furhat.say("Great! You know most of the song!" )
/*
                furhat.say("Great! You remember ${finalScore.toInt()} percent!" )
*/
                furhat.gesture(Gestures.Wink(1.0),async = false)
            }
            else if (score >= 0.4) {
                furhat.gesture(Gestures.Thoughtful(0.9, 2.0), async = true)
/*
                furhat.say("Not bad! You cover ${finalScore.toInt()} percent of the song!")
*/
                furhat.say("Not bad! But you need some practice!")
                goto(OfferSingAlternately(song))
                }
            else {
                furhat.gesture(Gestures.Shake(0.9, 1.0, 3), async = true)
/*
                furhat.say ("Singing only ${finalScore.toInt()} percent of the song?")
*/
                furhat.say ("It looks like you don't remember the song!")
                goto(OfferSingWholeSong(song))
            }
            goto(SelectMode)
        }
/*        else {
            furhat.gesture(Gestures.Roll(0.9, 1.5), async = true)
            furhat.listen(timeout = 1500)
            furhat.say("Why you are not singing. Do you want to change the game?")
            goto(SelectMode)
        }*/
    }

    onResponse {
        var responseText: String = it.text.toLowerCase()
        var lyricLine = lyricList[lineCounter].toLowerCase()
        var similarityScore = stringSimilarity(responseText, lyricLine)
        var new_score = score + similarityScore/audioList.size.toDouble()
        var leftWords = lyricLine.substringAfter(responseText)
        if (leftWords == "") goto(SingAlong(song,lineCounter+1, new_score)) //perfect match
        else if (leftWords.length < lyricLine.length){ // some early words are correct, need to say the rest
            furhat.say(leftWords)
            goto(SingAlong(song,lineCounter+1, new_score))
        }
        else if (similarityScore >= 0.9){ //probably the recognizer is not perfect or a bit of mistake, continue singing!
            goto(SingAlong(song,lineCounter+1, new_score))
        }
        else{// it hears something, but completely different
            furhat.say("It sounds something else, it should be:")
            furhat.say(lyricLine)
            goto(SingAlong(song,lineCounter+1, new_score))
        }

    }

    onNoResponse {
        //var lyricLine = lyricList[lineCounter].toLowerCase()
        furhat.say(lyricList[lineCounter])
        goto(SingAlong(song,lineCounter+1, score))

    }
    //  changes
/*    onResponse<Stop> {
        furhat.say("Okay, sorry to hear that!")
        goto(SelectMode) }*/

}

fun OfferSingWholeSong(song: Song) : State = state(Options){
    onEntry {
        furhat.ask("I can sing the whole ${song} for you to memorize it! May I?")
    }
    onResponse<Yes> {
        furhat.say("Okay!")
        goto(SingWholeSong(song))
    }

    onResponse<No> {
        furhat.say("No problem. I can do other fun stuffs.")
        goto(SelectMode)
    }

}

fun OfferSinghelp(song: Song) : State = state(Options){
    onEntry {
        furhat.ask("I can sing the whole ${song} for you to memorize it! May I?")
    }
    onResponse<Yes> {
        furhat.say("Okay!")
        goto(SingWholeSong(song))
    }

    onResponse<No> {
        furhat.say("No problem. Let's sing again!")
        goto(SingAlternately(song))
    }

}

fun OfferSingAlternately(song: Song) : State = state(Options){
    onEntry {
        furhat.ask("We can sing ${song} together! shall we?")
    }
    onResponse<Yes> {
        furhat.say("Okay! I will start.")
        goto(goto(SingAlternately(song)))
    }
    onResponse<No> {
        furhat.say("No worries. There are other fun stuffs to do.")
        goto(SelectMode)
    }

}

