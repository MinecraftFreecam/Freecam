package net.xolt.freecam.util

import com.google.gson.Gson
import com.google.gson.JsonElement
import java.nio.file.Path
import kotlin.io.path.bufferedReader
import kotlin.reflect.KClass

private val gson by lazy {
    Gson()
}

fun Any.toJsonTree(): JsonElement = gson.toJsonTree(this)

inline fun <reified T : Any> Path.fromJson(): T = fromJson(T::class)
fun <T : Any> Path.fromJson(clazz: KClass<T>): T = gson.fromJson(bufferedReader(), clazz.java)
