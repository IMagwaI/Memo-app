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
    var password:String?=null

    constructor(nodeID:Int,nodeName:String,nodeDesc:String,date: String, reminderdate:String){
        this.id=nodeID
        this.title=nodeName
        this.description=nodeDesc
        this.date=date
        this.reminderdate=reminderdate
    }
    ///// wa7d khor replace
    constructor(nodeID:Int,nodeName:String,nodeDesc:String,date: String, reminderdate:String,password:String?){
        this.id=nodeID
        this.title=nodeName
        this.description=nodeDesc
        this.date=date
        this.reminderdate=reminderdate
        this.password=password
    }

    constructor(nodeID:Int,nodeName:String,nodeDesc:String,date: String,img:ByteArray, reminderdate:String){
        this.id=nodeID
        this.title=nodeName
        this.description=nodeDesc
        this.date=date
        this.reminderdate=reminderdate
        this.img=img

    }


    //used to store blob in firebase    had constructeur kayn mssta3ml b tari9a ghalta but makaynch 3ndo impact anyways it's changed now
//    constructor(nodeID:Int,nodeName:String,nodeDesc:String,date: String,img:String, reminderdate:String){
//        this.id=nodeID
//        this.title=nodeName
//        this.description=nodeDesc
//        this.date=date
//        this.reminderdate=reminderdate
//        this.imgS=img
//
//    }

    constructor(id: Int?, title: String?, description: String?, date: String?, img: ByteArray?, reminderdate: String?, password: String?) {
        this.id = id
        this.title = title
        this.description = description
        this.date = date
        this.img = img
        this.reminderdate = reminderdate
        this.password = password
    }

    constructor(id: Int?, title: String?, description: String?, date: String?, img:String, reminderdate: String?, password: String?) {
        this.id = id
        this.title = title
        this.description = description
        this.date = date
        this.imgS = img
        this.reminderdate = reminderdate
        this.password = password
    }
}