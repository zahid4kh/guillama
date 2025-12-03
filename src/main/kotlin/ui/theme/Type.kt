package ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import guillaama.resources.JetBrainsMono_Bold
import guillaama.resources.JetBrainsMono_Italic
import guillaama.resources.JetBrainsMono_Regular
import guillaama.resources.Poppins_Bold
import guillaama.resources.Poppins_Italic
import guillaama.resources.Poppins_Regular
import guillaama.resources.Res
import org.jetbrains.compose.resources.Font


@Composable
fun getPoppinsFamily(): FontFamily{
    val ubuntuFontFamily = FontFamily(
        Font(resource = Res.font.Poppins_Regular, weight = FontWeight.Normal, style = FontStyle.Normal),
        Font(resource = Res.font.Poppins_Italic, weight = FontWeight.Normal, style = FontStyle.Italic),
        Font(resource = Res.font.Poppins_Bold, weight = FontWeight.Bold, style = FontStyle.Normal)
    )
    return ubuntuFontFamily
}

@Composable
fun getJetbrainsMonoFamily(): FontFamily{
    val jetbrainsMonoFamily = FontFamily(
        Font(resource = Res.font.JetBrainsMono_Regular, weight = FontWeight.Normal, style = FontStyle.Normal),
        Font(resource = Res.font.JetBrainsMono_Italic, weight = FontWeight.Normal, style = FontStyle.Italic),
        Font(resource = Res.font.JetBrainsMono_Bold, weight = FontWeight.Bold, style = FontStyle.Normal)
    )
    return jetbrainsMonoFamily
}