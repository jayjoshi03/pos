package com.varitas.gokulpos.tablet.utilities

import java.util.Calendar

object DriverLicenseParser {
    fun parseDriverLicense(input: String): PlateData {
        val addressPattern = """DAG(.{0,35})DAI""".toRegex()
        val surnamePattern = """DCS(.{0,35})DDE""".toRegex()
        val namePattern = """DAC(.{0,35})DDF""".toRegex()
        val lastNamePattern = """DAD(.{0,35})DDG""".toRegex()
        val dobPattern = """DBB(\d{8})""".toRegex()
        val expiryPattern = """DBA(\d{8})""".toRegex()
        val licenseNumberPattern = """DAQ([A-Z0-9]{1,20})""".toRegex()
        val statePattern = """DAJ([A-Z]{2})""".toRegex()
        val postalCodePattern = """DAK(\d{5})""".toRegex()
        val cityPattern = """DAI(.{0,35})DAJ""".toRegex()

        val surnameMatch = surnamePattern.find(input)
        val nameMatch = namePattern.find(input)
        val lastNameMatch = lastNamePattern.find(input)
        val addressMatch = addressPattern.find(input)
        val dobMatch = dobPattern.find(input)
        val expiryMatch = expiryPattern.find(input)
        val licenseNumberMatch = licenseNumberPattern.find(input)
        val stateMatch = statePattern.find(input)
        val postalCodeMatch = postalCodePattern.find(input)
        val cityMatch = cityPattern.find(input)

        val age = getAge(dobMatch?.groups?.get(1)?.value ?: "")
        val dob = Constants.sdfDateFormatMMMddYY.format(Utils.convertStringToDate(formatter = Constants.sdfDateFormatMMddYYYY, parseDate = dobMatch?.groups?.get(1)?.value ?: ""))
        val expiry = Constants.sdfDateFormatMMMddYY.format(Utils.convertStringToDate(formatter = Constants.sdfDateFormatMMddYYYY, parseDate = expiryMatch?.groups?.get(1)?.value ?: ""))

        return PlateData(
            (surnameMatch?.groups?.get(1)?.value?.trim() ?: "") + " " + (nameMatch?.groups?.get(1)?.value?.trim() ?: "") + " " + (lastNameMatch?.groups?.get(1)?.value?.trim() ?: ""),
            addressMatch?.groups?.get(1)?.value?.trim() ?: "",
            dob,
            expiry,
            licenseNumberMatch?.groups?.get(1)?.value ?: "",
            stateMatch?.groups?.get(1)?.value ?: "",
            postalCodeMatch?.groups?.get(1)?.value ?: "", cityMatch?.groups?.get(1)?.value ?: "", age
                        )
    }

    private fun getAge(dobString: String): Int {
        val date = Utils.convertStringToDate(formatter = Constants.sdfDateFormatMMddYYYY, parseDate = dobString)
        val dob: Calendar = Calendar.getInstance()
        val today: Calendar = Calendar.getInstance()
        dob.time = date
        val year: Int = dob.get(Calendar.YEAR)
        val month: Int = dob.get(Calendar.MONTH)
        val day: Int = dob.get(Calendar.DAY_OF_MONTH)
        dob.set(year, month + 1, day)
        var age: Int = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

}

data class PlateData(
        val name: String,
        val address: String,
        val dobString: String,
        val expiryString: String,
        val licenseNumber: String,
        val state: String,
        val postcode: String, val city: String, val age: Int
                    ) {
    constructor() : this("", "", "", "", "", "", "", "", 0)
}

