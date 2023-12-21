package day07

import requireSize

// I don't WTF I can't find this in the jdk/kdk
class ListComparator<T : Comparable<T>> : Comparator<List<T>> {
    override fun compare(o1: List<T>?, o2: List<T>?): Int {
        o1!!.requireSize(o2!!.size).forEachIndexed { i, t1 ->
            val elementComp = t1.compareTo(o2[i])
            if (elementComp != 0) {
                return elementComp
            }
        }
        return 0
    }
}
