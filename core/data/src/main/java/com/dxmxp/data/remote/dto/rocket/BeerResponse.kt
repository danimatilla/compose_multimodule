package com.dxmxp.data.remote.dto.rocket

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BeerResponse(
    val abv: Double,

    @SerialName("attenuation_level")
    val attenuationLevel: Double? = null,

    @SerialName("boil_volume")
    val boilVolume: BoilVolume? = null,

    @SerialName("brewers_tips")
    val brewersTips: String? = null,

    @SerialName("contributed_by")
    val contributedBy: String? = null,

    val description: String,
    val ebc: Double? = null,

    @SerialName("first_brewed")
    val firstBrewed: String,

    @SerialName("food_pairing")
    val foodPairing: List<String>? = null,

    val ibu: Double? = null,
    val id: Long,
    val image: String,
    val ingredients: Ingredients? = null,
    val method: Method? = null,
    val name: String,
    val ph: Double? = null,
    val srm: Double? = null,
    val tagline: String,

    @SerialName("target_fg")
    val targetFg: Double? = null,

    @SerialName("target_og")
    val targetOg: Double? = null,

    val volume: Volume? = null
) {
    @Serializable
    data class BoilVolume(
        val unit: String,
        val value: Long
    )

    @Serializable
    data class Ingredients(
        val hops: List<Hop>,
        val malt: List<Malt>,
        val yeast: String? = null
    )

    @Serializable
    data class Hop(
        val add: String? = null,
        val amount: HopAmount? = null,
        val attribute: String? = null,
        val name: String? = null
    )

    @Serializable
    data class HopAmount(
        val unit: String? = null,
        val value: Double? = null
    )

    @Serializable
    data class Malt(
        val amount: MaltAmount,
        val name: String
    )

    @Serializable
    data class MaltAmount(
        val unit: String,
        val value: Double
    )

    @Serializable
    data class Method(
        val fermentation: Fermentation,

        @SerialName("mash_temp")
        val mashTemp: List<MashTemp>,

        val twist: String? = null
    )

    @Serializable
    data class Fermentation(
        val temp: FermentationTemp? = null
    )

    @Serializable
    data class FermentationTemp(
        val unit: String,
        val value: Double? = null
    )

    @Serializable
    data class MashTemp(
        val duration: Double? = null,
        val temp: MashTempTemp
    )

    @Serializable
    data class MashTempTemp(
        val unit: String,
        val value: Double? = null
    )

    @Serializable
    data class Volume(
        val unit: String,
        val value: Long
    )
}
