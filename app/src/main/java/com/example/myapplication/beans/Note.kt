package com.example.myapplication.beans

import java.util.*

class Note {
    var id: Int? = null
    var title: String? = null
    var description: String? = null
    var date: String? = null

    constructor(nodeID:Int,nodeName:String,nodeDesc:String,date: String){
        this.id=nodeID
        this.title=nodeName
        this.description=nodeDesc
        this.date=date

    }

}