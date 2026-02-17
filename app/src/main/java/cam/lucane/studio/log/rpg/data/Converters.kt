package cam.lucane.studio.log.rpg.data

import androidx.room.TypeConverter
import cam.lucane.studio.log.rpg.data.entity.CurrencyMode

class Converters {
    @TypeConverter
    fun fromCurrencyMode(value: CurrencyMode): String {
        return value.name
    }
    
    @TypeConverter
    fun toCurrencyMode(value: String): CurrencyMode {
        return CurrencyMode.valueOf(value)
    }
}
