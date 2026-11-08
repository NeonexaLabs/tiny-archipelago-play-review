# PlayReview: Google Play's rating card for Tiny Archipelago

A small Godot 4.7 Android plugin (v2 layout, Kotlin) that shows Google Play's rating card through the
[In-App Review API](https://developer.android.com/guide/playcore/in-app-review) (`com.google.android.play:review` 2.0.2).
It is made for [Tiny Archipelago](https://github.com/NeonexaLabs/tiny-archipelago-game), whose `scripts/review/play_review_provider.gd`
is its only caller; the game decides who is asked and when (its milestone M23, "Kind Words").

## What it offers

One singleton, `PlayReview`:

| | |
| --- | --- |
| `prepare()` | asks Google for a `ReviewInfo` ahead of the moment, as Google advises (only once the game is sure to launch the flow soon) |
| `launch()` | launches the card with that `ReviewInfo`, asking for one first if there is none |
| `review_flow_finished(ok: bool)` | said exactly once per `launch()`. `ok` only says that nothing failed: Google never says whether the card was shown or what the player did |

Nothing runs when the game opens: no Google code is called before `prepare()` or `launch()`. The plugin declares no permission, reads
nothing, keeps nothing and sends nothing of its own: the card and the review are the Play Store's, under Google's terms. The card shows
only for a game installed from Google Play (Google's [testing guide](https://developer.android.com/guide/playcore/in-app-review/test)).

## Build

```sh
./gradlew assemble    # JDK 17 or later and the Android SDK (platform 35); leaves the addon in addons/PlayReview
```

`addons/PlayReview` (the export script, `plugin.cfg`, this licence and both AARs) is committed. A release's asset `addons.zip` is
`git archive` of its tag, which holds that folder only (`.gitattributes`):

```sh
git tag -a v1.0.0 -m "PlayReview 1.0.0"
git push origin main v1.0.0
git archive --format=zip -o addons.zip v1.0.0
gh release create v1.0.0 addons.zip --verify-tag --title "1.0.0"
```

The game pins the tag and the archive's SHA-256 (`tools/android_export.py`), and its CI compares the game's adapter with this source at
the tag (`tools/check_review_contract.py`).

## Licence

MIT (see `LICENSE`). The In-App Review library itself is Google's, under the
[Play Core Software Development Kit Terms of Service](https://developer.android.com/guide/playcore#license).
