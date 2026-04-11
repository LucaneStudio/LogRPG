# ── Gson : conserver les noms de champs des classes sérialisées ──────────────
-keepclassmembers class cam.lucane.studio.log.rpg.data.model.** { *; }
-keepclassmembers class cam.lucane.studio.log.rpg.data.entity.SpellSlot { *; }
-keepclassmembers class cam.lucane.studio.log.rpg.data.entity.ManaMode { *; }
-keepclassmembers class cam.lucane.studio.log.rpg.data.entity.CurrencyMode { *; }

# Enums Gson
-keepclassmembers enum * { public static **[] values(); public static ** valueOf(java.lang.String); }
# Conserver les signatures génériques (requis pour Gson TypeToken)
-keepattributes Signature