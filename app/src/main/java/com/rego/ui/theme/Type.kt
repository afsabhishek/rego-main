package com.rego.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.rego.R

/**
 *
 * Thin	Thin Italic	100
 * Extra Light	Extra Light Italic	200
 * Light	Light Italic	300
 * Regular	Italic	400
 * Medium	Medium Italic	500
 * Semi Bold	Semi Bold Italic	600
 * Bold	Bold Italic	700
 * Extra Bold	Extra Bold Italic	800
 * Black	Black Italic	900
 *
 */

@Composable
fun fontLightPoppins() = TextStyle(
    fontFamily = FontFamily(
        Font(
            resId = R.font.poppins_light,
        )
    ),
)

@Composable
fun fontExtraLightPoppins() = TextStyle(
    fontFamily = FontFamily(
        Font(
            resId = R.font.poppins_extra_light,
        )
    ),
)

@Composable
fun fontMediumPoppins() = TextStyle(
    fontFamily = FontFamily(
        Font(
            resId = R.font.poppins_medium,
        )
    ),
)

@Composable
fun fontRegularPoppins() = TextStyle(
    fontFamily = FontFamily(
        Font(
            resId = R.font.poppins_regular,
        )
    ),
)

@Composable
fun fontSemiBoldPoppins() = TextStyle(
    fontFamily = FontFamily(
        Font(
            resId = R.font.poppins_semibold,
        )
    ),
)

@Composable
fun fontBoldPoppins() = TextStyle(
    fontFamily = FontFamily(
        Font(
            resId = R.font.poppins_bold,
        )
    ),
)

@Composable
fun fontExtraBoldPoppins() = TextStyle(
    fontFamily = FontFamily(
        Font(
            resId = R.font.poppins_extra_bold,
        )
    ),
)

@Composable
fun fontLightMontserrat() = TextStyle(
    fontFamily = FontFamily(
        Font(
            resId = R.font.montserrat_light,
        )
    ),
)

@Composable
fun fontExtraLightMontserrat() = TextStyle(
    fontFamily = FontFamily(
        Font(
            resId = R.font.montserrat_extra_light,
        )
    ),
)

@Composable
fun fontMediumMontserrat() = TextStyle(
    fontFamily = FontFamily(
        Font(
            resId = R.font.montserrat_medium,
        )
    ),
)

@Composable
fun fontRegularMontserrat() = TextStyle(
    fontFamily = FontFamily(
        Font(
            resId = R.font.montserrat_regular,
        )
    ),
)

@Composable
fun fontSemiBoldMontserrat() = TextStyle(
    fontFamily = FontFamily(
        Font(
            resId = R.font.montserrat_semi_bold,
        )
    ),
)

@Composable
fun fontBoldMontserrat() = TextStyle(
    fontFamily = FontFamily(
        Font(
            resId = R.font.montserrat_bold,
        )
    ),
)

@Composable
fun fontExtraBoldMontserrat() = TextStyle(
    fontFamily = FontFamily(
        Font(
            resId = R.font.montserrat_extra_bold,
        )
    ),
)