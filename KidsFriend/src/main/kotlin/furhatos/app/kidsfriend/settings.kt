package furhatos.app.kidsfriend

val path = System.getProperty("user.dir")
val lyricpath = "$path/src/main/kotlin/furhatos/app/kidsfriend/text_files/lyrics/"

fun stringSimilarity(s1: String, s2:String): Double {
    var shorter = s1
    var longer = s2
    if (s1.length < s2.length) { // longer should always have greater length
        longer = s2
        shorter = s1
    }
    var longerLength = longer.length.toDouble()
    if (longerLength == 0.0) { return 1.0; /* both strings are zero length */ }
    return (longerLength - levenshtein(longer, shorter)) / longerLength

}

fun levenshtein(lhs : CharSequence, rhs : CharSequence) : Int {
    val lhsLength = lhs.length
    val rhsLength = rhs.length

    var cost = Array(lhsLength) { it }
    var newCost = Array(lhsLength) { 0 }

    for (i in 1..rhsLength-1) {
        newCost[0] = i

        for (j in 1..lhsLength-1) {
            val match = if(lhs[j - 1] == rhs[i - 1]) 0 else 1

            val costReplace = cost[j - 1] + match
            val costInsert = cost[j] + 1
            val costDelete = newCost[j - 1] + 1

            newCost[j] = Math.min(Math.min(costInsert, costDelete), costReplace)
        }

        val swap = cost
        cost = newCost
        newCost = swap
    }

    return cost[lhsLength - 1]
}


