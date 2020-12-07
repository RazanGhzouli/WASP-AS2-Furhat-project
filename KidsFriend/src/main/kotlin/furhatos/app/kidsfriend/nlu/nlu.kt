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

class ObjectType : EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf("apple", "banana", "lemon", "pear","skunk", "rabbit","bear","frog","cow","pig","dog","cat","lime","cherry","pineapple","orange","fox")
    }
}

class Singing(var song : Song? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("Sing", "Singing", "@song", "I want to sing", "I would like to sing", "I want to sing @song", "I would like to sing @song","sing @song")
    }
}


//class Quiz : Intent() {
//    override fun getExamples(lang: Language): List<String> {
//        return listOf("quiz", "I want a quiz", "I want to guess", "guessing game", "guess","I want to guess @quizSubject")
//    }
//}

class QuizType(var quizSubject : Category? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I want to guess @quizSubject", "@quizSubject","quiz", "I want a quiz", "I want to guess", "guessing game", "guess", "the @quizSubject game")
    }
}

class ObjectGuess(var guess : ObjectType? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I think it is @guess", "@guess", "I think @guess")
    }
}

class StopPlaying : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("stop", "can we stop", "give me the answer", "I give up", "new game", "I do not want to guess")
    }
}

