package com.vaibhav.gallery.collectiondb.collection

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collection")
class Collection {
    @PrimaryKey(autoGenerate = true)
    var id:Int=0
    var name:String?=null

    constructor(name: String?) {
        this.name = name
    }
}