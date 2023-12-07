package com.varitas.gokulpos.tablet.utilities

object ChangeDueCalculator {

    private fun getCurrencyBillCoins(): Pair<ArrayList<Double>, ArrayList<Double>> {
        val lstFullBillCoins = ArrayList<Double>()
        val lstChangeBillCoins = ArrayList<Double>()
        lstFullBillCoins.addAll(listOf(1.00, 5.00, 10.00, 20.00, 50.00, 100.00)) // Dollars
        lstChangeBillCoins.addAll(listOf(1.00, 5.00, 10.00, 25.00)) // Penny, Nickel, Dime, Quarter
        return Pair(lstFullBillCoins, lstChangeBillCoins)
    }

    private fun addElement(listValues: ArrayList<Double>, value: Double): ArrayList<Double> {
        if (!listValues.contains(value)) {
            listValues.add(value)
        }
        return listValues
    }

    fun getFullBillValues(orderAmount: Double, countryName: String = "USA"): ArrayList<Double> {
        if (orderAmount == 0.0) {
            return ArrayList()
        }

        val (wholeCurrencyList, _) = getCurrencyBillCoins()

        if (wholeCurrencyList.isEmpty()) {
            return ArrayList()
        }

        wholeCurrencyList.sortBy { it }
        return getChangeReturnValues(orderAmount, wholeCurrencyList, countryName)
    }

    private fun getChangeReturnValues(orderAmount: Double, wholeCurrencyList: ArrayList<Double>, countryName: String): ArrayList<Double> {
        val returnWholeList = ArrayList<Double>()
        val returnMarginPayList = ArrayList<Double>()
        wholeCurrencyList.sort()

        val next: Double = if (orderAmount - orderAmount.toInt() == 0.0) {
            orderAmount
        } else {
            orderAmount + 1
        }

        if (wholeCurrencyList.isEmpty() || orderAmount == 0.0) {
            return returnMarginPayList
        }

        val biggestBill = wholeCurrencyList[wholeCurrencyList.size - 1]
        val smallestBill = wholeCurrencyList[0]
        val changeAmt = orderAmount - orderAmount.toInt()

        if (changeAmt > 0) {
            returnWholeList.forEach { wholeNum ->
                returnMarginPayList.add((wholeNum + changeAmt.times(100)))
            }
        }

        when {
            next <= smallestBill -> {
                returnWholeList.addAll(wholeCurrencyList)
            }
            next == biggestBill -> {
                for (v in wholeCurrencyList) {
                    if (biggestBill % v != 0.00) {
                        returnWholeList.addAll(addElement(returnWholeList, v * (biggestBill / v + 1)))
                    }
                }
                return returnMarginPayList
            }
            next > biggestBill -> {
                val baseValue = (next / biggestBill) * biggestBill

                if (baseValue == next) {
                    returnWholeList.add(baseValue)
                    wholeCurrencyList.forEach { v ->
                        if (baseValue % v != 0.00) {
                            returnWholeList.addAll(addElement(returnWholeList, v * (baseValue / v + 1)))
                        }
                    }
                } else {
                    val diff = next - baseValue

                    if (diff >= smallestBill && diff < biggestBill) {
                        var lastval = smallestBill

                        wholeCurrencyList.forEach { v ->
                            if (diff >= lastval && diff < v) {
                                returnWholeList.add(baseValue + v)
                                wholeCurrencyList.filter { c -> c > v }
                                    .forEach { N -> addElement(returnWholeList, baseValue + N) }

                                if (diff < v) {
                                    wholeCurrencyList.forEach { belowVal ->
                                        if (belowVal >= v) {
                                            return@forEach
                                        }

                                        if (diff % belowVal != 0.00) {
                                            var multiplier = 2
                                            var multiple = multiplier * belowVal
                                            while (multiple < biggestBill) {
                                                if (multiple < biggestBill && multiple > diff) {
                                                    returnWholeList.addAll(addElement(returnWholeList, baseValue + multiple))
                                                    break
                                                }
                                                multiplier++
                                                multiple = multiplier * belowVal
                                            }
                                        }
                                    }
                                }
                                return@forEach
                            }
                            lastval = v
                        }
                    } else {
                        wholeCurrencyList.forEach { belowVal ->
                            if (diff % belowVal != 0.00) {
                                var multiplier = 2
                                var multiple = multiplier * belowVal
                                while (multiple < 100) {
                                    if (multiple < 100 && multiple > next) {
                                        returnWholeList.addAll(addElement(returnWholeList, multiple))
                                        break
                                    }
                                    multiplier++
                                    multiple = multiplier * belowVal
                                }
                            }
                        }
                    }
                }
            }
            next > smallestBill && next < biggestBill -> {
                var lastval = smallestBill
                for (v in wholeCurrencyList) {
                    if (next >= lastval && next < v) {
                        returnWholeList.add(v)
                        for (N in wholeCurrencyList.filter { c -> c > v }) {
                            addElement(returnWholeList, N)
                        }

                        if (next < v) {
                            for (belowVal in wholeCurrencyList) {
                                if (belowVal >= v) {
                                    break
                                }

                                if (next % belowVal != 0.00) {
                                    var multiplier = 2
                                    var multiple = multiplier * belowVal
                                    while (multiple < biggestBill) {
                                        if (multiple < biggestBill && multiple > next) {
                                            returnWholeList.addAll(addElement(returnWholeList, multiple))
                                            break
                                        }
                                        multiplier++
                                        multiple = multiplier * belowVal
                                    }
                                }
                            }
                        }
                        break
                    }
                    lastval = v
                }
            }
        }

        val changeAmtInt = (orderAmount - orderAmount.toInt() * 100).toInt()
        if (changeAmtInt > 0) {
            returnWholeList.forEach { wholeNum ->
                returnMarginPayList.add(wholeNum + changeAmtInt)
            }
        }

        val currencyArray = returnWholeList.toSet().toList()
        currencyArray.sorted()
        returnMarginPayList.sort()
        return ArrayList(currencyArray.sortedBy { it })
    }
}