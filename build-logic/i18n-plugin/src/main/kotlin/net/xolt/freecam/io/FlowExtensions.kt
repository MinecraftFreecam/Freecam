package net.xolt.freecam.io

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.onEachConcurrent(concurrencyLevel: Int = 16, action: suspend (T) -> Unit): Flow<T> =
    flatMapMerge(concurrencyLevel) { value ->
        flow {
            action(value)
            emit(value)
        }
    }

@OptIn(ExperimentalCoroutinesApi::class)
fun <T, R> Flow<T>.concurrentMap(concurrencyLevel: Int = 16, transform: suspend (T) -> R): Flow<R> =
    flatMapMerge(concurrencyLevel) { value ->
        flow { emit(transform(value)) }
    }