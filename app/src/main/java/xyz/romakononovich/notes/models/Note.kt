package xyz.romakononovich.notes.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by romank on 26.01.18.
 */
open class Note: RealmObject() {
    lateinit var title: String
    lateinit var note: String
    @PrimaryKey
    var timestamp: Long = 0

}