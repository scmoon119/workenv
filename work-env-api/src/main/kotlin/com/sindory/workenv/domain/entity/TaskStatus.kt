package com.sindory.workenv.domain.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class TaskStatus(i: Int) {
    TODO(0){
           override fun toString():String{
               return "전";
           }
    },
    DOING(1){
        override fun toString():String{
            return "중";
        }
    },
    CANCEL(2){
        override fun toString():String{
            return "취소";
        }
    },
    DONE(3){
        override fun toString():String{
            return "완";
        }
    },
    DELEGATE(4){
        override fun toString():String{
            return "위임";
        }
    },
    POSTPONE(5){
        override fun toString():String{
            return "연기";
        }
    };

    @JsonValue
    override fun toString(): String {
        return name
    }

    companion object {
        @JsonCreator
        fun fromKoreanRepresentation(koreanRepresentation: String): TaskStatus {
            return values().find { it.toString() == koreanRepresentation }
                ?: throw IllegalArgumentException("Invalid TaskStatus: $koreanRepresentation")
        }
    }
}