package com.android.automobile.view.util

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import timber.log.Timber

/**
 * Utility class for converting objects to viewable strings and back.
 */
object StringUtil {
    private const val FIELD_SEPARATOR = "\n\t"
    private const val RESULT_SEPARATOR = "\n---\n\t"

    @SuppressLint("SetTextI18n")
    fun prepend(textView: TextView, prefix: String) {
        textView.text = """
            $prefix

            ${textView.text}
            """.trimIndent()
    }

    fun convertToLatLngBounds(
        southWest: String?, northEast: String?
    ): LatLngBounds? {
        val soundWestLatLng = convertToLatLng(southWest)
        val northEastLatLng = convertToLatLng(northEast)
        return if (soundWestLatLng == null || northEast == null) {
            null
        } else northEastLatLng?.let { LatLngBounds(soundWestLatLng, it) }
    }

    fun convertToLatLng(value: String?): LatLng? {
        if (TextUtils.isEmpty(value)) {
            return null
        }
        val split = value!!.split(",").dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (split.size != 2) {
            null
        } else try {
            LatLng(split[0].toDouble(), split[1].toDouble())
        } catch (e: NullPointerException) {
            null
        } catch (e: NumberFormatException) {
            null
        }
    }

    fun countriesStringToArrayList(countriesString: String): List<String> {
        // Allow these delimiters: , ; | / \
        return listOf(
            *countriesString
                .replace("\\s".toRegex(), "|")
                .split("[,;|/\\\\]").dropLastWhile { it.isEmpty() }.toTypedArray()
        )
    }

    fun stringify(response: FindAutocompletePredictionsResponse, raw: Boolean): String {
        val builder = StringBuilder()
        builder
            .append(response.autocompletePredictions.size)
            .append(" Autocomplete Predictions Results:")
        if (raw) {
            builder.append(RESULT_SEPARATOR)
            appendListToStringBuilder(builder, response.autocompletePredictions)
        } else {
            for (autocompletePrediction in response.autocompletePredictions) {
                builder
                    .append(RESULT_SEPARATOR)
                    .append(autocompletePrediction.getFullText( /* matchStyle */null))
            }
        }
        return builder.toString()
    }

    fun stringify(response: FetchPlaceResponse, raw: Boolean): String {
        val builder = StringBuilder()
        builder.append("Fetch Place Result:").append(RESULT_SEPARATOR)
        if (raw) {
            builder.append(response.place)
        } else {
            builder.append(stringify(response.place))
        }
        return builder.toString()
    }

    fun stringify(response: FindCurrentPlaceResponse, raw: Boolean): String {
        val builder = StringBuilder()
        builder.append(response.placeLikelihoods.size).append(" Current Place Results:")
        if (raw) {
            builder.append(RESULT_SEPARATOR)
            appendListToStringBuilder(builder, response.placeLikelihoods)
        } else {
            for (placeLikelihood in response.placeLikelihoods) {
                builder
                    .append(RESULT_SEPARATOR)
                    .append("Likelihood: ")
                    .append(placeLikelihood.likelihood)
                    .append(FIELD_SEPARATOR)
                    .append("Place: ")
                    .append(stringify(placeLikelihood.place))
            }
        }
        return builder.toString()
    }

    fun stringify(place: Place): String {
        return (place.name
                + " ("
                + place.address
                + ")"
                + " is open now? "
                + place.isOpen(System.currentTimeMillis()))
    }

    fun stringify(bitmap: Bitmap): String {
        val builder = StringBuilder()
        builder
            .append("Photo size (width x height)")
            .append(RESULT_SEPARATOR)
            .append(bitmap.width)
            .append(", ")
            .append(bitmap.height)
        return builder.toString()
    }

    fun stringifyAutocompleteWidget(place: Place, raw: Boolean): String {
        val builder = StringBuilder()
        builder.append("Autocomplete Widget Result:").append(RESULT_SEPARATOR)
        if (raw) {
            builder.append(place)
        } else {
            builder.append(stringify(place))
        }
        return builder.toString()
    }

    private fun <T> appendListToStringBuilder(builder: StringBuilder, items: List<T>) {
        if (items.isEmpty()) {
            return
        }
        builder.append(items[0])
        for (i in 1 until items.size) {
            builder.append(RESULT_SEPARATOR)
            builder.append(items[i])
        }
    }

    fun stringifyNum(str: String): Any {
        return str.replace(Regex("[^0-9]"), "")
    }

    fun stringifyMixNum(str: String): Any {
        return Regex("[0-9]+").findAll(str)
            .map(MatchResult::value)
            .toList()
    }

    fun stringify(str: String): String {
        return str.contains("^[a-zA-Z]*$".toRegex()).toString()
    }

    fun digitFilter(text: String): Long {
        val result = text.filter { it.isDigit() }
        return result.toLong()
    }

    private fun stringFilter(text: String): Any {
        return text.filter { it.isLetter() }
    }

    fun timeConverter(duration: String): Long {
        when {
            "mins" == (stringFilter(duration)) -> {
                return digitFilter(duration)
            }
            "hrs" == stringFilter(duration) -> {
                return digitFilter(duration)
            }
            else -> {
                Timber.tag("String").i("No string found")

            }
        }
        return digitFilter(duration)
    }


}