package com.spotifyclone.tools.utils

class ListUtils {

    companion object {
        inline fun <reified T : Any?> sublist(
            list: List<T>,
            start: Int,
            end: Int = list.size
        ): List<T> {
            if (start > end || start > list.size || end < 0) {
                return listOf()
            }
            if (end > list.size) {
                return list
            }
            val startCut: Int = if (start < 0) 0 else start
            return list.subList(startCut, end)
        }

        inline fun <reified T : Any> swapAllAt(
            list: List<T>,
            apendList: List<T>,
            start: Int
        ): List<T> {
            val newList: MutableList<T> = cut(list, start) as MutableList<T>
            newList.addAll(apendList)
            return newList
        }

        inline fun <reified T : Any> cut(list: List<T>, start: Int): List<T> {
            return list.subList(0, start)
        }
    }
}
