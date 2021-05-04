package com.dreamfolks.baseapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val countryCode =
    "[{\"id\":1,\"name\":\"India\",\"dialCode\":\"+91\",\"countryCode\":\"IN\",\"minLen\":10,\"maxLen\":11,\"regexval\":\"^[0-9]*\$\"},{\"id\":2,\"name\":\"Kenya\",\"dialCode\":\"+254\",\"countryCode\":\"KE\",\"minLen\":8,\"maxLen\":10,\"regexval\":\"^[0-9]*\$\"},{\"id\":3,\"name\":\"Canada\",\"dialCode\":\"+1\",\"countryCode\":\"CA\",\"minLen\":10,\"maxLen\":11,\"regexval\":\"^[0-9]*\$\"},{\"id\":4,\"name\":\"China\",\"dialCode\":\"+86\",\"countryCode\":\"CN\",\"minLen\":10,\"maxLen\":12,\"regexval\":\"^[0-9]*\$\"},{\"id\":5,\"name\":\"Egypt\",\"dialCode\":\"+20\",\"countryCode\":\"EG\",\"minLen\":10,\"maxLen\":12,\"regexval\":\"^[0-9]*\$\"},{\"id\":6,\"name\":\"Zambia\",\"dialCode\":\"+260\",\"countryCode\":\"ZM\",\"minLen\":9,\"maxLen\":10,\"regexval\":\"^[0-9]*\$\"},{\"id\":7,\"name\":\"United Arab Emirates\",\"dialCode\":\"+971\",\"countryCode\":\"AE\",\"minLen\":7,\"maxLen\":10,\"regexval\":\"^[0-9]*\$\"},{\"id\":8,\"name\":\"Singapore\",\"dialCode\":\"+65\",\"countryCode\":\"SG\",\"minLen\":8,\"maxLen\":9,\"regexval\":\"^[0-9]*\$\"},{\"id\":9,\"name\":\"Saudi Arabia\",\"dialCode\":\"+966\",\"countryCode\":\"SA\",\"minLen\":9,\"maxLen\":11,\"regexval\":\"^[0-9]*\$\"},{\"id\":10,\"name\":\"Hong Kong\",\"dialCode\":\"+852\",\"countryCode\":\"HK\",\"minLen\":8,\"maxLen\":11,\"regexval\":\"^[0-9]*\$\"},{\"id\":11,\"name\":\"United Kingdom\",\"dialCode\":\"+44\",\"countryCode\":\"GB\",\"minLen\":10,\"maxLen\":11,\"regexval\":\"^[0-9]*\$\"},{\"id\":12,\"name\":\"Switzerland\",\"dialCode\":\"+41\",\"countryCode\":\"CH\",\"minLen\":10,\"maxLen\":11,\"regexval\":\"^[0-9]*\$\"},{\"id\":13,\"name\":\"Bahrain\",\"dialCode\":\"+973\",\"countryCode\":\"BH\",\"minLen\":8,\"maxLen\":10,\"regexval\":\"^[0-9]*\$\"},{\"id\":14,\"name\":\"Mauritius\",\"dialCode\":\"+230\",\"countryCode\":\"MU\",\"minLen\":8,\"maxLen\":11,\"regexval\":\"^[0-9]*\$\"},{\"id\":15,\"name\":\"Uganda\",\"dialCode\":\"+256\",\"countryCode\":\"UG\",\"minLen\":9,\"maxLen\":10,\"regexval\":\"^[0-9]*\$\"},{\"id\":16,\"name\":\"Gabon\",\"dialCode\":\"+241\",\"countryCode\":\"GA\",\"minLen\":8,\"maxLen\":10,\"regexval\":\"^[0-9]*\$\"},{\"id\":17,\"name\":\"Spain\",\"dialCode\":\"+34\",\"countryCode\":\"ES\",\"minLen\":9,\"maxLen\":10,\"regexval\":\"^[0-9]*\$\"},{\"id\":18,\"name\":\"Malta\",\"dialCode\":\"+356\",\"countryCode\":\"MT\",\"minLen\":7,\"maxLen\":10,\"regexval\":\"^[0-9]*\$\"},{\"id\":19,\"name\":\"Maldives\",\"dialCode\":\"+960\",\"countryCode\":\"MV\",\"minLen\":7,\"maxLen\":10,\"regexval\":\"^[0-9]*\$\"},{\"id\":20,\"name\":\"United States\",\"dialCode\":\"+1\",\"countryCode\":\"US\",\"minLen\":10,\"maxLen\":11,\"regexval\":\"^[0-9]*\$\"},{\"id\":21,\"name\":\"Germany\",\"dialCode\":\"+49\",\"countryCode\":\"DE\",\"minLen\":10,\"maxLen\":11,\"regexval\":\"^[0-9]*\$\"},{\"id\":22,\"name\":\"Zimbabwe\",\"dialCode\":\"+263\",\"countryCode\":\"ZW\",\"minLen\":6,\"maxLen\":10,\"regexval\":\"^[0-9]*\$\"},{\"id\":23,\"name\":\"Brazil\",\"dialCode\":\"+55\",\"countryCode\":\"BR\",\"minLen\":9,\"maxLen\":11,\"regexval\":\"^[0-9]*\$\"},{\"id\":24,\"name\":\"Indonesia\",\"dialCode\":\"+62\",\"countryCode\":\"ID\",\"minLen\":8,\"maxLen\":12,\"regexval\":\"^[0-9]*\$\"},{\"id\":25,\"name\":\"Oman\",\"dialCode\":\"+968\",\"countryCode\":\"OM\",\"minLen\":8,\"maxLen\":10,\"regexval\":\"^[0-9]*\$\"},{\"id\":26,\"name\":\"Luxembourg\",\"dialCode\":\"+352\",\"countryCode\":\"LU\",\"minLen\":7,\"maxLen\":10,\"regexval\":\"^[0-9]*\$\"},{\"id\":27,\"name\":\"Nigeria\",\"dialCode\":\"+234\",\"countryCode\":\"NG\",\"minLen\":8,\"maxLen\":11,\"regexval\":\"^[0-9]*\$\"},{\"id\":28,\"name\":\"Chile\",\"dialCode\":\"+56\",\"countryCode\":\"CL\",\"minLen\":9,\"maxLen\":11,\"regexval\":\"^[0-9]*\$\"},{\"id\":29,\"name\":\"Australia\",\"dialCode\":\"+61\",\"countryCode\":\"AU\",\"minLen\":9,\"maxLen\":10,\"regexval\":\"^[0-9]*\$\"},{\"id\":30,\"name\":\"Aruba\",\"dialCode\":\"+297\",\"countryCode\":\"AR\",\"minLen\":6,\"maxLen\":9,\"regexval\":\"^[0-9]*\$\"},{\"id\":31,\"name\":\"Argentina\",\"dialCode\":\"+54\",\"countryCode\":\"AG\",\"minLen\":9,\"maxLen\":12,\"regexval\":\"^[0-9]*\$\"},{\"id\":32,\"name\":\"Bolivia\",\"dialCode\":\"+591\",\"countryCode\":\"BO\",\"minLen\":7,\"maxLen\":12,\"regexval\":\"^[0-9]*\$\"},{\"id\":33,\"name\":\"Curazao\",\"dialCode\":\"+599\",\"countryCode\":\"CU\",\"minLen\":7,\"maxLen\":10,\"regexval\":\"^[0-9]*\$\"},{\"id\":34,\"name\":\"Colombia\",\"dialCode\":\"+57\",\"countryCode\":\"CO\",\"minLen\":10,\"maxLen\":12,\"regexval\":\"^[0-9]*\$\"},{\"id\":35,\"name\":\"CostaRica\",\"dialCode\":\"+506\",\"countryCode\":\"CR\",\"minLen\":7,\"maxLen\":9,\"regexval\":\"^[0-9]*\$\"},{\"id\":36,\"name\":\"Ecuador\",\"dialCode\":\"+593\",\"countryCode\":\"EC\",\"minLen\":8,\"maxLen\":10,\"regexval\":\"^[0-9]*\$\"},{\"id\":37,\"name\":\"Jamaica\",\"dialCode\":\"+876\",\"countryCode\":\"JA\",\"minLen\":7,\"maxLen\":10,\"regexval\":\"^[0-9]*\$\"},{\"id\":38,\"name\":\"Panama\",\"dialCode\":\"+507\",\"countryCode\":\"PA\",\"minLen\":7,\"maxLen\":8,\"regexval\":\"^[0-9]*\$\"},{\"id\":39,\"name\":\"Mexico\",\"dialCode\":\"+52\",\"countryCode\":\"MX\",\"minLen\":9,\"maxLen\":11,\"regexval\":\"^[0-9]*\$\"},{\"id\":40,\"name\":\"Peru\",\"dialCode\":\"+51\",\"countryCode\":\"PE\",\"minLen\":8,\"maxLen\":11,\"regexval\":\"^[0-9]*\$\"},{\"id\":41,\"name\":\"Uruguay\",\"dialCode\":\"+598\",\"countryCode\":\"UR\",\"minLen\":7,\"maxLen\":10,\"regexval\":\"^[0-9]*\$\"},{\"id\":42,\"name\":\"Afghanistan\",\"dialCode\":\"+93\",\"countryCode\":\"AF\",\"minLen\":8,\"maxLen\":12,\"regexval\":\"^[0-9]*\$\"},{\"id\":43,\"name\":\"Bulgaria\",\"dialCode\":\"+359\",\"countryCode\":\"BG\",\"minLen\":8,\"maxLen\":12,\"regexval\":\"^[0-9]*\$\"},{\"id\":44,\"name\":\"Montenegro\",\"dialCode\":\"+382\",\"countryCode\":\"ME\",\"minLen\":8,\"maxLen\":12,\"regexval\":\"^[0-9]*\$\"},{\"id\":45,\"name\":\"Antigua and Barbuda\",\"dialCode\":\"+1-268\",\"countryCode\":\"AG\",\"minLen\":8,\"maxLen\":12,\"regexval\":\"^[0-9]*\$\"},{\"id\":46,\"name\":\"Russia\",\"dialCode\":\"+7\",\"countryCode\":\"RU\",\"minLen\":9,\"maxLen\":14,\"regexval\":\"^[0-9]*\$\"}]"
/*
const val nameTitle="[{value=\"MR\",key=\"Mr.\"},{value=\"MS\",key=\"Ms.\"},{value=\"MRS\",key=\"Mrs.\"},{value=\"MISS\",key=\"Miss.\"},{value=\"DR\",key=\"Dr.\"}]"
*/
data class CountryCode(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("dialCode") val dialCode: String,
    @SerializedName("countryCode") val countryCode: String,
    @SerializedName("minLen") val minLen: Int,
    @SerializedName("maxLen") val maxLen: Int,
    @SerializedName("regexval") val regexval: String
):Serializable