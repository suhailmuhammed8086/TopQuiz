package com.example.quiztap.data

import com.example.quiztap.data.Difficulty.EASY
import com.example.quiztap.data.Difficulty.HARD
import com.example.quiztap.data.Difficulty.MEDIUM
import com.example.quiztap.data.TimeSetting.NO_TIME
import com.example.quiztap.data.TimeSetting.TIME_PER_QUESTION
import com.example.quiztap.data.TimeSetting.TOTAL_TIME

enum class Difficulty(val id: Int) {
    EASY(1), MEDIUM(2), HARD(3);

    fun getRequestValue(): String {
        return when (this) {
            EASY -> "easy"
            MEDIUM -> "medium"
            HARD -> "hard"
        }
    }


    fun getDisplayName(): String {
        return when (this) {
            EASY -> "Easy"
            MEDIUM -> "Medium"
            HARD -> "Hard"
        }
    }

  companion object {
      fun parseFromId(id: Int): Difficulty {
          return when (id) {
              EASY.id -> EASY
              MEDIUM.id -> MEDIUM
              HARD.id -> HARD
              else -> MEDIUM
          }
      }
  }
}

enum class TimeSetting(val id: Int) {
    NO_TIME(1),
    TIME_PER_QUESTION(2),
    TOTAL_TIME(3);

    fun getDisplayName(): String {
        return when (this) {
            NO_TIME -> "No Limit"
            TIME_PER_QUESTION -> "Time per Question"
            TOTAL_TIME -> "Time per session"
        }
    }

    companion object {
        fun parseFromId(id: Int): TimeSetting {
            return when (id) {
                NO_TIME.id -> NO_TIME
                TIME_PER_QUESTION.id -> TIME_PER_QUESTION
                TOTAL_TIME.id -> TOTAL_TIME
                else -> NO_TIME
            }
        }
    }
}

enum class AnswerType(val id: Int) {
    ALL(1),
    YES_OR_NO(2),
    MULTIPLE_CHOICE(3);

    fun getRequestValue(): String? {
        return when (this) {
            ALL -> null
            YES_OR_NO -> "boolean"
            MULTIPLE_CHOICE -> "multiple"
        }
    }

    companion object {
        fun parseFromId(id: Int): AnswerType {
            return when (id) {
                ALL.id -> ALL
                YES_OR_NO.id -> YES_OR_NO
                MULTIPLE_CHOICE.id -> MULTIPLE_CHOICE
                else -> ALL
            }
        }
    }
}

enum class Grade {
    A_PLUS,
    A,
    B,
    C,
    D,
    F;

    fun toText(): String {
        return when(this) {
            A_PLUS -> "A+"
                    A -> "A"
                    B -> "B"
                    C -> "C"
                    D -> "D"
                    F -> "F"
        }
    }



    fun getColor(): String {
        return when (this) {
            A_PLUS -> "#1ABC9C" // teal
            A -> "#27AE60"      // green
            B -> "#2980B9"      // blue
            C -> "#F1C40F"      // yellow
            D -> "#E67E22"      // orange
            F -> "#E74C3C"      // red
        }
    }
    companion object {
        fun getGrade(percentage: Int): Grade {
            return when (percentage) {
                in 90..100 -> A_PLUS
                in 80..89 -> A
                in 70..79 -> B
                in 60..69 -> C
                in 50..59 -> D
                else -> F
            }
        }
    }
}
