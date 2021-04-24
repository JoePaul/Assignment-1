package com.example.assignment1.ui.custom_views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import com.example.assignment1.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend

class CustomPieChart: PieChart {

    init {
        description.isEnabled = false
        setUsePercentValues(true)
        setDrawEntryLabels(false)
        setCenterTextSize(15f)

        holeRadius = 45f
        transparentCircleRadius = 50f
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        val typedValue = TypedValue()
        context.theme?.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
        val color: Int = typedValue.data
        legend.textColor  = color
        legend.setDrawInside(false)
    }
    constructor(context: Context): super(context)
    constructor(context: Context, attr: AttributeSet): super(context, attr)
    constructor(context: Context, attr: AttributeSet, defStyle: Int): super(context, attr, defStyle)


}