package com.example.myapplication.beans

class Note {
    var id: Int? = null
    var title: String? = null
    var description: String? = null
    constructor(nodeID:Int,nodeName:String,nodeDesc:String){
        this.id=nodeID
        this.title=nodeName
        this.description=nodeDesc

    }

}