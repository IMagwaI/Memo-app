package com.example.myapplication.beans

import java.util.*

class Note {
    var id: Int? = null
    var title: String? = null
    var description: String? = null
    var date: String? = null
    var reminderdate: String ?=null
    var img:ByteArray?=null
    var imgS:String?=null

    constructor(nodeID:Int,nodeName:String,nodeDesc:String,date: String, reminderdate:String ){
        this.id=nodeID
        this.title=nodeName
        this.description=nodeDesc
        this.date=date
        this.reminderdate=reminderdate
    }

    constructor(nodeID:Int,nodeName:String,nodeDesc:String,date: String,img:ByteArray, reminderdate:String){
        this.id=nodeID
        this.title=nodeName
        this.description=nodeDesc
        this.date=date
        this.reminderdate=reminderdate
        this.img=img

    }


    //used to store blob in firebase
    constructor(nodeID:Int,nodeName:String,nodeDesc:String,date: String,img:String, reminderdate:String){
        this.id=nodeID
        this.title=nodeName
        this.description=nodeDesc
        this.date=date
        this.reminderdate=reminderdate
        this.imgS=img

    }
}