package jie.android.alexahelper.utils

//open class Singleton<out T, in P>(private val constructor: (P) -> T) {
//    @Volatile
//    private var instance: T? = null
//    fun getInstance(param: P): T =
//        instance ?: synchronized(this) {
//            constructor(param).also { instance = it }
//        }
//    fun getInstance(): T? = instance
//}

open class Singleton<out T>(private val constructor: () -> T) {
    @Volatile
    private var instance: T? = null
    fun getInstance(): T =
        instance ?: synchronized(this) {
            instance = constructor()
            return instance as T
        }
}