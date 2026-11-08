@tool
extends EditorPlugin
## Adds the PlayReview Android plugin (Google Play's rating card) to Android exports: its AAR and the one library it needs.

var _export_plugin: AndroidExportPlugin


func _enter_tree() -> void:
	_export_plugin = AndroidExportPlugin.new()
	add_export_plugin(_export_plugin)


func _exit_tree() -> void:
	remove_export_plugin(_export_plugin)
	_export_plugin = null


class AndroidExportPlugin extends EditorExportPlugin:
	const PLUGIN_NAME := "PlayReview"
	## The one library the plugin needs, at the version it is built against.
	const DEPENDENCIES: PackedStringArray = ["com.google.android.play:review:2.0.2"]

	func _supports_platform(platform: EditorExportPlatform) -> bool:
		return platform is EditorExportPlatformAndroid

	func _get_android_libraries(_platform: EditorExportPlatform, debug: bool) -> PackedStringArray:
		var kind := "debug" if debug else "release"
		return PackedStringArray(["%s/bin/%s/%s-%s.aar" % [PLUGIN_NAME, kind, PLUGIN_NAME, kind]])

	func _get_android_dependencies(_platform: EditorExportPlatform, _debug: bool) -> PackedStringArray:
		return DEPENDENCIES

	func _get_name() -> String:
		return PLUGIN_NAME
