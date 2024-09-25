package com.example.kotlinmongo.collection

import com.kmsl.dsl.annotation.EmbeddedDocument

@EmbeddedDocument
data class Receipt(
    val date: String,
    val card: String,
    val price: Long,
): MutableMap<String, Any?> {
    constructor() : this(
        date = "",
        card = "",
        price = 0,
    )

    private val map: MutableMap<String, Any?> = mutableMapOf(
        "date" to date,
        "card" to card,
        "price" to price,
    )

    override fun put(key: String, value: Any?): Any? {
        return when (key) {
            "date" -> {
                map.put(key, value)
            }

            "card" -> {
                map.put(key, value)
            }

            "price" -> {
                map.put(key, value)
            }

            else -> map.put(key, value)
        }
    }

    override val entries: MutableSet<MutableMap.MutableEntry<String, Any?>>
        get() = map.entries

    override val keys: MutableSet<String>
        get() = map.keys

    override val size: Int
        get() = map.size

    override val values: MutableCollection<Any?>
        get() = map.values

    override fun clear() {
        map.clear()
    }

    override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

    override fun remove(key: String): Any? {
        return map.remove(key)
    }

    override fun putAll(from: Map<out String, Any?>) {
        map.putAll(from)
    }

    override fun get(key: String): Any? {
        return map[key]
    }

    override fun containsValue(value: Any?): Boolean {
        return map.containsValue(value)
    }

    override fun containsKey(key: String): Boolean {
        return map.containsKey(key)
    }

    companion object {
        fun of(
            date: String,
            card: String,
            price: Long,
        ) = Receipt(
            date = date,
            card = card,
            price = price,
        )
    }
}