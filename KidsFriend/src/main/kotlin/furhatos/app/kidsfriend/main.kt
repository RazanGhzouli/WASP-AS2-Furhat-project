package furhatos.app.kidsfriend

import furhatos.app.kidsfriend.flow.Idle
import furhatos.skills.Skill
import furhatos.flow.kotlin.*

class KidsfriendSkill : Skill() {
    override fun start() {
        Flow().run(Idle)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}
