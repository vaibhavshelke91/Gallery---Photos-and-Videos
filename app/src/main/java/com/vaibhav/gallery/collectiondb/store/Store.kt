package com.vaibhav.gallery.collectiondb.store

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "store")
class Store {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    var path: String? = null
    var contentID = 0
    var refID = 0

    constructor() {}
    constructor(path: String?, refId: Int) {
        this.path = path
        this.refID = refId
    }
}