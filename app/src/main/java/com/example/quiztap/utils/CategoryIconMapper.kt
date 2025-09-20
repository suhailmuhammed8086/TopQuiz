package com.example.quiztap.utils

import com.example.quiztap.R

class CategoryIconMapper {
    private val icons = listOf(
        9 to R.drawable.img_general_knowledge,
        10 to R.drawable.img_book,
        11 to R.drawable.img_film,
        12 to R.drawable.img_music,
        13 to R.drawable.img_theatre,
        14 to R.drawable.img_tv,
        15 to R.drawable.img_video_game,
        16 to R.drawable.img_board_game,
        17 to R.drawable.img_nature,
        18 to R.drawable.img_computer,
        19 to R.drawable.img_maths,
        20 to R.drawable.img_myth,
        21 to R.drawable.img_sports,
        22 to R.drawable.img_geography,
        23 to R.drawable.img_history,
        24 to R.drawable.img_politics,
        25 to R.drawable.img_art,
        26 to R.drawable.img_celebrities,
        27 to R.drawable.img_animals,
        28 to R.drawable.img_vehicle,
        29 to R.drawable.img_comic,
        30 to R.drawable.img_gadgets,
        31 to R.drawable.img_anime,
        32 to R.drawable.img_cartoon,
    )

    fun getIcon(id: Int): Int {
        val icon = icons.find { it.first == id }?.second ?: R.drawable.img_category
        return icon
    }
}