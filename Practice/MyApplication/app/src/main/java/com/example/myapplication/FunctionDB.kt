package com.example.myapplication

class FunctionDB {
    fun getLevelName(levelNumber: Int): String {
        var levelName = ""
        if (levelNumber in 0..59) {
            levelName = "等級E"
        } else if (levelNumber in 60..69) {
            levelName = "等級D"
        } else if (levelNumber in 70..79) {
            levelName = "等級C"
        } else if (levelNumber in 80..89) {
            levelName = "等級B"
        } else if (levelNumber in 90..100) {
            levelName = "等級A"
        } else {
            levelName = "Error : 請輸入0~100"
        }
        return levelName
    }
}