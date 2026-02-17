# LogRPG - Application de Gestion de Personnages JDR

Application Android 100% locale pour gérer vos personnages de jeux de rôle.

## ✨ Fonctionnalités

- **Multi-personnages** : Créez et gérez plusieurs personnages
- **Fiche PDF** : Importez et visualisez vos fiches de personnage en PDF
- **Compteurs** : Vie, Mana avec ajustement rapide
- **Monnaie** : 3 modes (unique, x10, x100) avec conversion automatique
- **Capacités** : Import/Export JSON, ajout manuel
- **Inventaire** : Gestion complète des objets
- **Export/Import** : Sauvegarde JSON de vos personnages

## 📱 Configuration

- **Package** : cam.lucane.studio.log.rpg
- **MinSDK** : 31 (Android 12)
- **TargetSDK** : 35
- **Langage** : Kotlin
- **UI** : Jetpack Compose + Material 3

## 🚀 Compilation

```bash
./gradlew assembleDebug
```

## 📂 Structure

```
app/src/main/java/cam/lucane/studio/log/rpg/
├── data/
│   ├── entity/        # Character, Ability, Item
│   ├── dao/           # Room DAOs
│   ├── model/         # Export/Import models
│   └── repository/    # CharacterRepository
├── ui/
│   ├── screen/        # Screens Compose
│   ├── viewmodel/     # ViewModels
│   ├── navigation/    # Navigation
│   └── theme/         # Theme Material 3
└── MainActivity.kt
```

## 📄 Exemples JSON

Voir le dossier `/examples` pour des exemples de:
- `abilities_example.json` - Capacités
- `inventory_example.json` - Inventaire
- `character_example.json` - Personnage complet

## 🎯 Utilisation

1. Créer un personnage
2. Importer une fiche PDF
3. Ajouter capacités et inventaire
4. Gérer les compteurs (vie, mana, monnaie)
5. Exporter/Importer en JSON

## 📝 Licence

Gratuit, sans publicité, 100% local
