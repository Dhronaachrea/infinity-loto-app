package com.skilrock.infinitylotoapp.data_class.response_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class JackpotAndTimerResponseData (

    @SerializedName("data")
    @Expose
    val `data`: Data?,

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int?,

    @SerializedName("message")
    @Expose
    val message: String?
) {
    data class Data(

        @SerializedName("currentTime")
        @Expose
        val currentTime: CurrentTime?,

        @SerializedName("games")
        @Expose
        val games: Games?
    ) {
        data class CurrentTime(

            @SerializedName("date")
            @Expose
            val date: String?,

            @SerializedName("timezone")
            @Expose
            val timezone: String?,

            @SerializedName("timezone_type")
            @Expose
            val timezoneType: Int?
        )

        data class Games(

            @SerializedName("FIVEBYNINETY")
            @Expose
            val fiveByNinety: FIVEBYNINETY?,

            @SerializedName("LUCKYSIX")
            @Expose
            val luckySix: LUCKYSIX?,

            @SerializedName("POWERBALL")
            @Expose
            val powerBall: POWERBALL?,

            @SerializedName("SUPERKENO")
            @Expose
            val superKeno: SUPERKENO?,

            @SerializedName("THAILOTTERYHIGHFREQUENCY")
            @Expose
            val thaiLotteryHighFrequency: THAILOTTERYHIGHFREQUENCY?,

            @SerializedName("TWELVEBYTWENTYFOUR")
            @Expose
            val twelveByTwentyFour: TWELVEBYTWENTYFOUR?
        ) {
            data class FIVEBYNINETY(

                @SerializedName("active")
                @Expose
                val active: String?,

                @SerializedName("datetime")
                @Expose
                val datetime: String?,

                @SerializedName("draw_date")
                @Expose
                val drawDate: String?,

                @SerializedName("estimated_jackpot")
                @Expose
                val estimatedJackpot: String?,

                @SerializedName("extra")
                @Expose
                val extra: Extra?,

                @SerializedName("game_code")
                @Expose
                val gameCode: String?,

                @SerializedName("guaranteed_jackpot")
                @Expose
                val guaranteedJackpot: String?,

                @SerializedName("jackpot_amount")
                @Expose
                val jackpotAmount: String?,

                @SerializedName("jackpot_title")
                @Expose
                val jackpotTitle: String?,

                @SerializedName("next_draw_date")
                @Expose
                val nextDrawDate: String?
            ) {
                data class Extra(

                    @SerializedName("currentDrawFreezeDate")
                    @Expose
                    val currentDrawFreezeDate: String?,

                    @SerializedName("currentDrawNumber")
                    @Expose
                    val currentDrawNumber: Int?,

                    @SerializedName("currentDrawStopTime")
                    @Expose
                    val currentDrawStopTime: String?,

                    @SerializedName("jackpotAmount")
                    @Expose
                    val jackpotAmount: Double?,

                    @SerializedName("unitCostJson")
                    @Expose
                    val unitCostJson: List<UnitCostJson?>?
                ) {
                    data class UnitCostJson(

                        @SerializedName("currency")
                        @Expose
                        val currency: String?,

                        @SerializedName("price")
                        @Expose
                        val price: Double?
                    )
                }
            }

            data class LUCKYSIX(

                @SerializedName("active")
                @Expose
                val active: String?,

                @SerializedName("datetime")
                @Expose
                val datetime: String?,

                @SerializedName("draw_date")
                @Expose
                val drawDate: String?,

                @SerializedName("estimated_jackpot")
                @Expose
                val estimatedJackpot: String?,

                @SerializedName("extra")
                @Expose
                val extra: Extra?,

                @SerializedName("game_code")
                @Expose
                val gameCode: String?,

                @SerializedName("guaranteed_jackpot")
                @Expose
                val guaranteedJackpot: String?,

                @SerializedName("jackpot_amount")
                @Expose
                val jackpotAmount: String?,

                @SerializedName("jackpot_title")
                @Expose
                val jackpotTitle: String?,

                @SerializedName("next_draw_date")
                @Expose
                val nextDrawDate: String?
            ) {
                data class Extra(

                    @SerializedName("currentDrawFreezeDate")
                    @Expose
                    val currentDrawFreezeDate: String?,

                    @SerializedName("currentDrawNumber")
                    @Expose
                    val currentDrawNumber: Int?,

                    @SerializedName("currentDrawStopTime")
                    @Expose
                    val currentDrawStopTime: String?,

                    @SerializedName("jackpotAmount")
                    @Expose
                    val jackpotAmount: Double?,

                    @SerializedName("unitCostJson")
                    @Expose
                    val unitCostJson: List<UnitCostJson?>?
                ) {
                    data class UnitCostJson(

                        @SerializedName("currency")
                        @Expose
                        val currency: String?,

                        @SerializedName("price")
                        @Expose
                        val price: Double?
                    )
                }
            }

            data class POWERBALL(

                @SerializedName("active")
                @Expose
                val active: String?,

                @SerializedName("datetime")
                @Expose
                val datetime: String?,

                @SerializedName("draw_date")
                @Expose
                val drawDate: String?,

                @SerializedName("estimated_jackpot")
                @Expose
                val estimatedJackpot: String?,

                @SerializedName("extra")
                @Expose
                val extra: Extra?,

                @SerializedName("game_code")
                @Expose
                val gameCode: String?,

                @SerializedName("guaranteed_jackpot")
                @Expose
                val guaranteedJackpot: String?,

                @SerializedName("jackpot_amount")
                @Expose
                val jackpotAmount: String?,

                @SerializedName("jackpot_title")
                @Expose
                val jackpotTitle: String?,

                @SerializedName("next_draw_date")
                @Expose
                val nextDrawDate: String?
            ) {
                data class Extra(

                    @SerializedName("currentDrawFreezeDate")
                    @Expose
                    val currentDrawFreezeDate: String?,

                    @SerializedName("currentDrawNumber")
                    @Expose
                    val currentDrawNumber: Int?,

                    @SerializedName("currentDrawStopTime")
                    @Expose
                    val currentDrawStopTime: String?,

                    @SerializedName("jackpotAmount")
                    @Expose
                    val jackpotAmount: Double?,

                    @SerializedName("unitCostJson")
                    @Expose
                    val unitCostJson: List<UnitCostJson?>?
                ) {
                    data class UnitCostJson(

                        @SerializedName("currency")
                        @Expose
                        val currency: String?,

                        @SerializedName("price")
                        @Expose
                        val price: Double?
                    )
                }
            }

            data class SUPERKENO(

                @SerializedName("active")
                @Expose
                val active: String?,

                @SerializedName("datetime")
                @Expose
                val datetime: String?,

                @SerializedName("draw_date")
                @Expose
                val drawDate: String?,

                @SerializedName("estimated_jackpot")
                @Expose
                val estimatedJackpot: String?,

                @SerializedName("extra")
                @Expose
                val extra: Extra?,

                @SerializedName("game_code")
                @Expose
                val gameCode: String?,

                @SerializedName("guaranteed_jackpot")
                @Expose
                val guaranteedJackpot: String?,

                @SerializedName("jackpot_amount")
                @Expose
                val jackpotAmount: String?,

                @SerializedName("jackpot_title")
                @Expose
                val jackpotTitle: String?,

                @SerializedName("next_draw_date")
                @Expose
                val nextDrawDate: String?
            ) {
                data class Extra(

                    @SerializedName("currentDrawFreezeDate")
                    @Expose
                    val currentDrawFreezeDate: String?,

                    @SerializedName("currentDrawNumber")
                    @Expose
                    val currentDrawNumber: Int?,

                    @SerializedName("currentDrawStopTime")
                    @Expose
                    val currentDrawStopTime: String?,

                    @SerializedName("jackpotAmount")
                    @Expose
                    val jackpotAmount: Double?,

                    @SerializedName("unitCostJson")
                    @Expose
                    val unitCostJson: List<UnitCostJson?>?
                ) {
                    data class UnitCostJson(

                        @SerializedName("currency")
                        @Expose
                        val currency: String?,

                        @SerializedName("price")
                        @Expose
                        val price: Double?
                    )
                }
            }

            data class THAILOTTERYHIGHFREQUENCY(

                @SerializedName("active")
                @Expose
                val active: String?,

                @SerializedName("datetime")
                @Expose
                val datetime: String?,

                @SerializedName("draw_date")
                @Expose
                val drawDate: String?,

                @SerializedName("estimated_jackpot")
                @Expose
                val estimatedJackpot: String?,

                @SerializedName("extra")
                @Expose
                val extra: Extra?,

                @SerializedName("game_code")
                @Expose
                val gameCode: String?,

                @SerializedName("guaranteed_jackpot")
                @Expose
                val guaranteedJackpot: String?,

                @SerializedName("jackpot_amount")
                @Expose
                val jackpotAmount: String?,

                @SerializedName("jackpot_title")
                @Expose
                val jackpotTitle: String?,

                @SerializedName("next_draw_date")
                @Expose
                val nextDrawDate: String?
            ) {
                data class Extra(

                    @SerializedName("currentDrawFreezeDate")
                    @Expose
                    val currentDrawFreezeDate: String?,

                    @SerializedName("currentDrawNumber")
                    @Expose
                    val currentDrawNumber: Int?,

                    @SerializedName("currentDrawStopTime")
                    @Expose
                    val currentDrawStopTime: String?,

                    @SerializedName("jackpotAmount")
                    @Expose
                    val jackpotAmount: Double?,

                    @SerializedName("unitCostJson")
                    @Expose
                    val unitCostJson: List<UnitCostJson?>?
                ) {
                    data class UnitCostJson(

                        @SerializedName("currency")
                        @Expose
                        val currency: String?,

                        @SerializedName("price")
                        @Expose
                        val price: Double?
                    )
                }
            }

            data class TWELVEBYTWENTYFOUR(

                @SerializedName("active")
                @Expose
                val active: String?,

                @SerializedName("datetime")
                @Expose
                val datetime: String?,

                @SerializedName("draw_date")
                @Expose
                val drawDate: String?,

                @SerializedName("estimated_jackpot")
                @Expose
                val estimatedJackpot: String?,

                @SerializedName("extra")
                @Expose
                val extra: Extra?,

                @SerializedName("game_code")
                @Expose
                val gameCode: String?,

                @SerializedName("guaranteed_jackpot")
                @Expose
                val guaranteedJackpot: String?,

                @SerializedName("jackpot_amount")
                @Expose
                val jackpotAmount: String?,

                @SerializedName("jackpot_title")
                @Expose
                val jackpotTitle: String?,

                @SerializedName("next_draw_date")
                @Expose
                val nextDrawDate: String?
            ) {
                data class Extra(

                    @SerializedName("currentDrawFreezeDate")
                    @Expose
                    val currentDrawFreezeDate: String?,

                    @SerializedName("currentDrawNumber")
                    @Expose
                    val currentDrawNumber: Int?,

                    @SerializedName("currentDrawStopTime")
                    @Expose
                    val currentDrawStopTime: String?,

                    @SerializedName("jackpotAmount")
                    @Expose
                    val jackpotAmount: Double?,

                    @SerializedName("unitCostJson")
                    @Expose
                    val unitCostJson: List<UnitCostJson?>?
                ) {
                    data class UnitCostJson(

                        @SerializedName("currency")
                        @Expose
                        val currency: String?,

                        @SerializedName("price")
                        @Expose
                        val price: Double?
                    )
                }
            }
        }
    }
}