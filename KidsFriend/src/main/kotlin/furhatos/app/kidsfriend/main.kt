package furhatos.app.kidsfriend

import furhatos.app.kidsfriend.flow.*
import furhatos.skills.Skill
import furhatos.flow.kotlin.*

class KidsfriendSkill : Skill() {
    override fun start() {
        Flow().run(Idle)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
    //val a = "twinkle twinkle little star"
    //val b = "dsds"
    //val c = "twinkle twinkle little star"
    //println(a.substringAfter(c))
    //println(a.substringAfter(b))

}
