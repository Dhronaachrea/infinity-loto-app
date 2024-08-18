package com.skilrock.infinitylotoapp.utility


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GameEnginesDataClass(

    @SerializedName("gameName")
    @Expose
    val gameName: String,

    @SerializedName("gameUrl")
    @Expose
    val gameUrl: String,

    @SerializedName("imagePath")
    @Expose
    val imagePath: Int,

    @SerializedName("tag")
    @Expose
    val tag: String
)