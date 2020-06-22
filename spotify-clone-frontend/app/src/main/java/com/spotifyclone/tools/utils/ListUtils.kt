package com.spotifyclone.tools.utils

class ListUtils {

    companion object {
        inline fun <reified T : Any> sublist(
            list: List<T>,
            start: Int,
            end: Int = list.size
        ): List<T> {
            return list.subList(
                start,
                end
            )
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
