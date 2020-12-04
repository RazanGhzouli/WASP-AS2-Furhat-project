package furhatos.app.kidsfriend.nlu

import furhatos.nlu.EnumEntity
import furhatos.nlu.Intent
import furhatos.util.Language

/*class RequestOptions: Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("What can you do?",
                "What are the options?",
                "What are the alternatives?",
                "What do you have?")
    }
}*/

class Mode : EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf("sing", "quiz")
    }
}
// me adding things
class Song : EnumEntity()  {
    override fun getEnum(lang: Language): List<String> {
        return listOf("twinkle twinkle little star: twinkle twinkle little star, twinkle, twinkle twinkle,little star","wheels on the bus: wheels on the bus, wheels, on the bus, bus")
    }
}

class Category : EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf("animal", "fruit")
    }
}

class Singing(var song : Song? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("Sing", "Singing", "@song", "I want to sing", "I would like to sing", "I want to sing @song", "I would like to sing @song","sing @song")
    }
}

class Quiz(var something : Category? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("quiz", "I want a quiz", "I want to guess @something")
    }
}

/*class Stop : EnumEntity (stemming = true, speechRecPhrases = true)  {
    override fun getExamples(lang: Language): List<String> {
        return listOf("stop", "I do not want to play", "stop singing")
    }
}*/

