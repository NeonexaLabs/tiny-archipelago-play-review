# The engine finds the plugin by the class its manifest entry names (org.godotengine.plugin.v2.PlayReview) and calls the methods that carry
# @UsedByGodot by name: R8 keeps both. The review library brings its own rules.
-keep class com.neonexalabs.playreview.** { *; }
