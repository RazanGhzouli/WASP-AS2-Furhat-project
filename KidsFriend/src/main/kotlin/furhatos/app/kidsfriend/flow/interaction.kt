package furhatos.app.kidsfriend.flow

import furhatos.gestures.Gestures
import furhatos.app.kidsfriend.stringSimilarity
import furhatos.app.kidsfriend.lyricpath
import furhatos.app.kidsfriend.nlu.*
import furhatos.flow.kotlin.*
import furhatos.nlu.common.Maybe
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes
import java.io.File
import java.io.InputStream

val Start : State = state(Interaction) {

    onEntry {
        random(
                {furhat.say("Hello there!")},
                {furhat.say("Hi guys!")},
                {furhat.say("Hey welcome!")}
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

    onResponse<Quiz> {
        val story = it.intent.something
        if (story != null) {
            goto(SelectQuiz(story))
        }
        else {
            propagate()
        }
    }

    onResponse<RequestOptions> {
        furhat.say("Great! I can ${Mode().optionsToText()}")
        furhat.ask("What do you want?")
    }

    onResponse<Yes>{
        furhat.say("Great! I can ${Mode().optionsToText()}")
        furhat.ask("What do you want?")
    }

}

val SelectMode = state(Options) {
    onEntry {
        random(
                {furhat.ask("What do you want to play?")},
                {furhat.ask("Do you want to play?")},
                {furhat.ask("Would you like to play with me?")}
        )
    }

    onResponse<No> {
        furhat.say("Okay, that's sad. Have a splendid day!")
        goto(Idle)
    }
}

fun SelectSong(song: Song) : State = state(Options) {
    onEntry {
        furhat.say("${song}, what a lovely song!")
        furhat.ask("Do you know it?")
    }
    onResponse<No> {
        furhat.say("Okay, I will sing the whole song for you!")
        goto(SingWholeSong(song))
    }

    onResponse<Maybe> {
        furhat.say("Okay, Let's take turn singing it!")
        goto(SingAlternately(song))
    }

    onResponse<Yes> {
        furhat.say("Okay, I will cover when you forget the lyric!")
        goto(SingAlong(song))
    }
}

fun SingWholeSong(song: Song) : State = state(Options){
    val songString = song.toString().toLowerCase()
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

fun SingAlternately(song: Song, lineCounter: Int = 0) : State = state(Options){
    val songString = song.toString().toLowerCase()
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
    }

    onResponse {
        var responseText: String = it.text.toLowerCase()
        var lyricLine = lyricList[lineCounter + 1].toLowerCase()
        var similarityScore = stringSimilarity(responseText, lyricLine)
        if (similarityScore > 0.5) { //set a low threshold for a kid ;)
            if (lyricList.size-lineCounter > 2 ) goto(SingAlternately(song,lineCounter+2))
            else { //on the last line
                furhat.say("Bravo! What a collaboration!")
                goto(SelectMode)
            }
        }
        else {
            val similarityPercent = similarityScore * 100.0
            furhat.say("Probably my voice recognition is not perfect, it is ${similarityPercent.toInt()} similar.")
            furhat.ask("Could you repeat it?")
        }

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
            if (score > 0.95) {
                furhat.gesture(Gestures.BigSmile,async = true)
                furhat.say("Awesome! You sing it perfectly!")
                furhat.gesture(Gestures.Wink(1.0),async = false)
            }
            else if (score > 0.7) {
                furhat.gesture(Gestures.BigSmile,async = true)
                furhat.say("Great! You remember ${finalScore.toInt()} percent!" )
                furhat.gesture(Gestures.Wink(1.0),async = false)
            }
            else if (score > 0.4) {
                furhat.gesture(Gestures.Thoughtful(0.9, 2.0), async = true)
                furhat.say("Not bad! You cover ${finalScore.toInt()} percent of the song!")
                goto(OfferSingAlternately(song))
                }
            else {
                furhat.gesture(Gestures.Shake(0.9, 1.0, 3), async = true)
                furhat.say ("Singing only ${finalScore.toInt()} percent of the song?")
                goto(OfferSingWholeSong(song))
            }
            goto(SelectMode)
        }
        else {
            furhat.gesture(Gestures.Roll(0.9, 1.5), async = true)
            furhat.listen(timeout = 1500)
        }
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
        else if (similarityScore > 0.9){ //probably the recognizer is not perfect or a bit of mistake, continue singing!
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

}

fun OfferSingWholeSong(song: Song) : State = state(Options){
    onEntry {
        furhat.ask("I can sing the whole ${song} for you! May I?")
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

fun OfferSingAlternately(song: Song) : State = state(Options){
    onEntry {
        furhat.ask("We can sing ${song} together! Would you?")
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

fun SelectQuiz(something: Category) : State = state(Options) {
    onEntry {
        furhat.say("${something}, what a good choice!")
        furhat.say("Let's start!")
    }

}

