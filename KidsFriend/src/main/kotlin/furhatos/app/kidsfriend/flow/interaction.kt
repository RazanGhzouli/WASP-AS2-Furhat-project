package furhatos.app.kidsfriend.flow

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
        if (lineCounter < audioList.size-1) furhat.say({+Audio(audioList[lineCounter], lyricList[lineCounter])})
        else if (lineCounter == audioList.size-1) {
            furhat.say({+Audio(audioList[lineCounter], lyricList[lineCounter])})
            furhat.say("Great! What a collaboration!")
            goto(SelectMode)
        }
    }

    onResponse {
        var responseText: String = it.text.toLowerCase()
//        furhat.say("I know you said ${responseText} !")
        if (responseText == lyricList[lineCounter + 1].toLowerCase()) {
            if (lyricList.size-lineCounter > 2 ) goto(SingAlternately(song,lineCounter+2))
            else { //on the last line
                furhat.say("Bravo! What a collaboration!")
                goto(SelectMode)
            }
        }
        else {
            furhat.ask("Probably my voice recognition is not perfect, could you repeat it?")
        }

    }

}


fun SelectQuiz(something: Category) : State = state(Options) {
    onEntry {
        furhat.say("${something}, what a good choice!")
        furhat.say("Let's start!")
    }

}