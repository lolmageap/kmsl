package cherhy.mongo.dsl.clazz

import org.springframework.data.mapping.toDotPath
import kotlin.reflect.KProperty1

class EmbeddedDocument<T, R> private constructor(
    private val property: KProperty1<T, R>,
) {
    val name: String
        get() = property.toDotPath()

    companion object {
        fun<T, R> of(
            property: KProperty1<T, R>,
        ) = EmbeddedDocument(property)
    }
}

class EmbeddedDocuments<T, R> private constructor(
    private val property: KProperty1<T, List<R>>,
) {
    val name: String
        get() = property.toDotPath()

    companion object {
        fun<T, R> of(
            property: KProperty1<T, List<R>>,
        ) = EmbeddedDocuments(property)
    }
}