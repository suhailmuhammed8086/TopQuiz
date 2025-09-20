package com.example.quiztap.network.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class CategoriesListResponse(
    @SerializedName("trivia_categories")
    val categories: List<CategoryModel>
)

@Parcelize
data class CategoryModel(
    val id: Int,
    val name: String,
    var icon: Int = -1,
): Parcelable {
    fun getDisplayName(): String {
        if (name.contains(":")) {
            return name.split(":").getOrNull(1)?: name
        }
        return name
    }
}