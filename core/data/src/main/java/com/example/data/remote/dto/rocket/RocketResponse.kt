package com.example.data.remote.dto.rocket

import android.annotation.SuppressLint
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class RocketResponse(
    val id: String,
    val name: String,
    val type: String,
    val active: Boolean,
    val stages: Int,
    val boosters: Int,
    @SerialName("cost_per_launch") val costPerLaunch: Long,
    @SerialName("success_rate_pct") val successRatePct: Float,
    @SerialName("first_flight") val firstFlight: String,
    val country: String,
    val company: String,
    val height: Dimension,
    val diameter: Dimension,
    val mass: Mass,
    @SerialName("payload_weights") val payloadWeights: List<PayloadWeight>,
    @SerialName("first_stage") val firstStage: FirstStage,
    @SerialName("second_stage") val secondStage: SecondStage,
    val engines: Engines,
    @SerialName("landing_legs") val landingLegs: LandingLegs,
    @SerialName("flickr_images") val flickrImages: List<String>,
    val wikipedia: String,
    val description: String,
) {
    @Serializable
    data class Dimension(
        val meters: Float?,
        val feet: Float?
    )

    @Serializable
    data class Mass(
        val kg: Int,
        val lb: Int
    )

    @Serializable
    data class PayloadWeight(
        val id: String,
        val name: String,
        val kg: Int,
        val lb: Int
    )

    @Serializable
    data class FirstStage(
        val reusable: Boolean,
        val engines: Int,
        @SerialName("fuel_amount_tons") val fuelAmountTons: Float,
        @SerialName("burn_time_sec") val burnTimeSec: Int?,
        @SerialName("thrust_sea_level") val thrustSeaLevel: Thrust,
        @SerialName("thrust_vacuum") val thrustVacuum: Thrust
    )

    @Serializable
    data class SecondStage(
        val reusable: Boolean,
        val engines: Int,
        @SerialName("fuel_amount_tons") val fuelAmountTons: Float,
        @SerialName("burn_time_sec") val burnTimeSec: Int?,
        val thrust: Thrust,
        val payloads: Payloads
    )

    @Serializable
    data class Thrust(
        val kN: Float,
        val lbf: Float
    )

    @Serializable
    data class Payloads(
        @SerialName("option_1") val option1: String?,
        @SerialName("composite_fairing") val compositeFairing: CompositeFairing?
    )

    @Serializable
    data class CompositeFairing(
        val height: Dimension,
        val diameter: Dimension
    )

    @Serializable
    data class Engines(
        val number: Int,
        val type: String,
        val version: String,
        val layout: String?,
        val isp: Isp,
        @SerialName("engine_loss_max") val engineLossMax: Int?,
        @SerialName("propellant_1") val propellant1: String,
        @SerialName("propellant_2") val propellant2: String,
        @SerialName("thrust_sea_level") val thrustSeaLevel: Thrust,
        @SerialName("thrust_vacuum") val thrustVacuum: Thrust,
        @SerialName("thrust_to_weight") val thrustToWeight: Float
    )

    @Serializable
    data class Isp(
        @SerialName("sea_level") val seaLevel: Float,
        val vacuum: Float
    )

    @Serializable
    data class LandingLegs(
        val number: Int,
        val material: String?
    )
}
