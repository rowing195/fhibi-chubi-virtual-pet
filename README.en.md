<div align="center" id="top">

[繁體中文](README.md) | **English**

<!-- HEADER STYLE: CLASSIC -->
<img src="art/IMG_4838_preview_based.gif" width="160" alt="Fibi desktop pet"/>

# fhibi-chubi-virtual-pet

<em>A floating companion that remembers the little things</em>

<!-- BADGES -->
<img src="https://img.shields.io/badge/version-0.2.3-0080ff?style=flat" alt="version">
<img src="https://img.shields.io/badge/minSdk-28-0080ff?style=flat&logo=android&logoColor=white" alt="minSdk">
<img src="https://img.shields.io/badge/targetSdk-35-0080ff?style=flat&logo=android&logoColor=white" alt="targetSdk">
<img src="https://img.shields.io/badge/license-MIT-green?style=flat" alt="license">

<em>Built with the tools and technologies:</em>

<img src="https://img.shields.io/badge/Kotlin-7F52FF.svg?style=flat&logo=kotlin&logoColor=white" alt="Kotlin">
<img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4.svg?style=flat&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose">
<img src="https://img.shields.io/badge/Material%203-757575.svg?style=flat&logo=materialdesign&logoColor=white" alt="Material 3">
<img src="https://img.shields.io/badge/Android-34A853.svg?style=flat&logo=android&logoColor=white" alt="Android">
<img src="https://img.shields.io/badge/SQLite-003B57.svg?style=flat&logo=sqlite&logoColor=white" alt="SQLite">
<img src="https://img.shields.io/badge/Gradle-02303A.svg?style=flat&logo=gradle&logoColor=white" alt="Gradle">

</div>
<br>

---

### Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
    - [Project Index](#project-index)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Usage](#usage)
    - [Testing](#testing)
    - [Release Signing](#release-signing)
- [License](#license)

---

## Overview

Desktop Pet (桌面寵物) is an Android app. Fibi floats on top of your other apps, tapping it opens a menu of pet actions and quick notes, and a home-screen widget keeps your to-do list in view. This is the **standalone public-prep edition**. It ships only the pet, quick notes and the widget, with no camera, network, AI or AirScroll integration.

> [!NOTE]
> The app's interface is in Traditional Chinese only.

**Why fhibi-chubi-virtual-pet?**

The project makes jotting down a thought take as little effort as possible. The core features include:

- **🐾 Floating pet:** A draggable overlay that snaps to the nearest screen edge and runs in the direction you drag it.
- **🐾 Pet actions:** Tap the pet's menu to play idle, run, wave, jump, failed, review or writing animations.
- **🎛️ Custom pet menu:** Arrange built-in actions or shortcuts to other apps; tapping a slot only selects it, while the options below assign its action and show app icons.
- **📝 Tap to note:** Choose **記一筆** (Quick note) from the pet's menu to open the note dialog. The pet writes along with you and reacts differently when you save or cancel.
- **📋 Home-screen widget:** A resizable to-do list where you can add, check off and clear items without opening the app.
- **🔄 Live sync:** The app, the quick-note dialog and the widget share one local SQLite database, so a change in any of them shows up everywhere at once.
- **🎨 Cream journal UI:** A cream and olive palette that follows the system dark mode and supports large font scales.
- **🔒 Local only:** No network permission. Your notes never leave the phone.

---

## Features

|      | Component | Details |
| :--- | :--- | :--- |
| ⚙️ | **Architecture** | <ul><li>Single `app` module, package `com.umuumu.virtualpet`</li><li>Jetpack Compose UI + `AndroidViewModel` + `StateFlow`</li><li>Navigation Compose with three tabs: Companion / Notes / Settings</li><li>`PetOverlayService` foreground service hosts the floating pet</li></ul> |
| 🔩 | **Code Quality** | <ul><li>Kotlin 2.0.21, JVM 17</li><li>Quick-note draft kept across recreation via `SavedStateHandle`</li><li>Blank notes blocked, duplicate saves prevented</li><li>Accessibility: heading semantics, `liveRegion` errors, content descriptions on widget rows</li></ul> |
| 📄 | **Documentation** | <ul><li>Design spec, design handoff and the release notes for each version in `.ui-work/` (Traditional Chinese)</li><li>`CLAUDE.md` records the development rules for Claude Code (split with the integrated edition, signing and the release process)</li></ul> |
| 🔌 | **Integrations** | <ul><li>`SYSTEM_ALERT_WINDOW` overlay</li><li>`specialUse` foreground service with an ongoing notification (hide the pet from the notification)</li><li>`AppWidgetProvider` + `RemoteViewsService` home-screen widget</li></ul> |
| 🧩 | **Modularity** | <ul><li>`screens/` split by screen: `home`, `notes`, `settings`</li><li>`ui/` holds the theme and pet artwork</li><li>`TodoRepository` singleton owns all to-do reads and writes</li></ul> |
| 🧪 | **Testing** | <ul><li>8 instrumentation tests across `CreamInterfaceTest` and the pet menu editor</li><li>Covers draft retention, list/widget sync, real widget taps, `MainActivity` reuse, pages not jumping during navigation and menu assignments</li></ul> |
| ⚡️ | **Performance** | <ul><li>Database work on `Dispatchers.IO`</li><li>Receivers and widget updates use `goAsync()` with a background thread</li><li>Pet drawn frame by frame from a single sprite sheet</li></ul> |
| 🛡️ | **Security** | <ul><li>No `INTERNET` permission</li><li>Internal components are `exported="false"`; broadcasts are scoped to the app's own package</li><li>Release keys read from Gradle properties, never committed</li></ul> |
| 📦 | **Dependencies** | <ul><li>Compose BOM `2024.12.01`, Material 3</li><li>`activity-compose` 1.9.3, `lifecycle-*-compose` 2.8.7, `navigation-compose` 2.8.5</li><li>Android Gradle Plugin 8.7.3, Gradle 8.11.1</li></ul> |

---

## Project Structure

```sh
└── fhibi-chubi-virtual-pet/
    ├── .ui-work/                  # design spec and release notes
    ├── CLAUDE.md                  # development rules for Claude Code
    ├── app/
    │   ├── build.gradle.kts
    │   └── src/
    │       ├── androidTest/       # instrumentation tests
    │       └── main/
    │           ├── AndroidManifest.xml
    │           ├── java/com/umuumu/virtualpet/
    │           │   ├── screens/   # Compose screens (home, notes, settings)
    │           │   └── ui/        # theme and pet artwork
    │           └── res/           # sprite sheet, widget layouts, strings, icons
    ├── art/                       # source character art (GIF, sprite sheet)
    ├── build.gradle.kts
    ├── gradle/wrapper/
    ├── gradle.properties
    ├── gradlew
    ├── gradlew.bat
    └── settings.gradle.kts
```

### Project Index

<details open>
	<summary><b><code>FHIBI-CHUBI-VIRTUAL-PET/</code></b></summary>
	<details>
		<summary><b>__root__</b></summary>
		<blockquote>
			<table style='width: 100%; border-collapse: collapse;'>
			<thead>
				<tr style='background-color: #f8f9fa;'>
					<th style='width: 30%; text-align: left; padding: 8px;'>File Name</th>
					<th style='text-align: left; padding: 8px;'>Summary</th>
				</tr>
			</thead>
				<tr style='border-bottom: 1px solid #eee;'>
					<td style='padding: 8px;'><b><a href='build.gradle.kts'>build.gradle.kts</a></b></td>
					<td style='padding: 8px;'>Declares the project-wide plugin versions: Android Gradle Plugin 8.7.3, Kotlin 2.0.21 and the Compose compiler plugin. The app module applies them.</td>
				</tr>
				<tr style='border-bottom: 1px solid #eee;'>
					<td style='padding: 8px;'><b><a href='settings.gradle.kts'>settings.gradle.kts</a></b></td>
					<td style='padding: 8px;'>Configures plugin and dependency repositories (Google, Maven Central) and includes the single <code>:app</code> module in the VirtualPet project.</td>
				</tr>
				<tr style='border-bottom: 1px solid #eee;'>
					<td style='padding: 8px;'><b><a href='gradle.properties'>gradle.properties</a></b></td>
					<td style='padding: 8px;'>Gradle runtime settings: JVM heap, UTF-8 encoding, AndroidX and non-transitive R classes.</td>
				</tr>
			</table>
		</blockquote>
	</details>
	<details>
		<summary><b>app</b></summary>
		<blockquote>
			<table style='width: 100%; border-collapse: collapse;'>
			<thead>
				<tr style='background-color: #f8f9fa;'>
					<th style='width: 30%; text-align: left; padding: 8px;'>File Name</th>
					<th style='text-align: left; padding: 8px;'>Summary</th>
				</tr>
			</thead>
				<tr style='border-bottom: 1px solid #eee;'>
					<td style='padding: 8px;'><b><a href='app/build.gradle.kts'>build.gradle.kts</a></b></td>
					<td style='padding: 8px;'>Defines the application ID, SDK range (28–35), version 0.2.3 and the Compose dependencies. The release signing config is created only when the signing keys are supplied through Gradle properties, so no key material lives in the repo.</td>
				</tr>
				<tr style='border-bottom: 1px solid #eee;'>
					<td style='padding: 8px;'><b><a href='app/src/main/AndroidManifest.xml'>AndroidManifest.xml</a></b></td>
					<td style='padding: 8px;'>Registers the main screen, quick-note dialog, floating-pet foreground service, widget provider and action receiver. Requests only overlay, foreground-service and notification permissions, and no network access.</td>
				</tr>
			</table>
			<details>
				<summary><b>src/main/java/com/umuumu/virtualpet</b></summary>
				<blockquote>
					<table style='width: 100%; border-collapse: collapse;'>
					<thead>
						<tr style='background-color: #f8f9fa;'>
							<th style='width: 30%; text-align: left; padding: 8px;'>File Name</th>
							<th style='text-align: left; padding: 8px;'>Summary</th>
						</tr>
					</thead>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/MainActivity.kt'>MainActivity.kt</a></b></td>
							<td style='padding: 8px;'>App entry point that hosts the cream theme and navigation. Toggles the floating pet, opens the overlay permission page and the quick-note dialog, and requests notification permission on Android 13+. Incoming intents can choose which tab to open.</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/PetOverlayService.kt'>PetOverlayService.kt</a></b></td>
							<td style='padding: 8px;'>Foreground service that shows the pet above other apps. Handles dragging, snapping to the nearest edge and tap-to-note, and listens for the quick-note open, save and cancel events to switch between writing, review and failed animations. An ongoing notification can hide the pet.</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/PetSpriteView.kt'>PetSpriteView.kt</a></b></td>
							<td style='padding: 8px;'>Custom view that draws the pet frame by frame from the sprite sheet. Defines the idle, run left/right, wave, jump, failed, writing and review animations; idle breathes slowly and blinks only every few seconds.</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/PetMenuView.kt'>PetMenuView.kt</a></b></td>
							<td style='padding: 8px;'>Full-screen menu shown when you tap the pet: eight speech bubbles on a 3×3 grid around the pet, each tail pointing at it. Bubbles show a built-in feature or an app's icon and name, and can switch to the 摸摸桌寵 (Pet actions) submenu. Tapping an empty slot keeps the menu open; tapping outside the bubbles closes it.</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/PetMenuPreferences.kt'>PetMenuPreferences.kt</a></b></td>
							<td style='padding: 8px;'>Defines the menu's built-in features (Pet actions, Settings, Quick note, Close) and what each slot can hold: a built-in feature, another app, or nothing. The eight-slot layout is stored in SharedPreferences; unreadable entries fall back to the default layout, and apps that can no longer be opened show a placeholder label.</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/QuickNoteActivity.kt'>QuickNoteActivity.kt</a></b></td>
							<td style='padding: 8px;'>Dialog-style quick-note screen through which the pet, the widget and the app add to-dos. Also serves as the widget's clear-completed confirmation and reports its open and close state to the pet.</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/TodoRepository.kt'>TodoRepository.kt</a></b></td>
							<td style='padding: 8px;'>Singleton SQLite data layer shared by the app, the quick-note dialog and the widget. Adds, toggles and clears completed to-dos, sorts unfinished items first and newest first, and publishes a revision Flow so screens reload on change.</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/VirtualPetAppWidgetProvider.kt'>VirtualPetAppWidgetProvider.kt</a></b></td>
							<td style='padding: 8px;'>Home-screen to-do widget that shows the total count next to its title and wires up add, toggle and clear-completed actions; the clear button only appears when something is done. Exposes <code>refreshAll</code> so every placed widget updates after any data change.</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/TodoWidgetService.kt'>TodoWidgetService.kt</a></b></td>
							<td style='padding: 8px;'>Supplies the widget's list rows, each drawn as a small card. Completed items get a strikethrough and a checked icon, and each row carries a screen-reader description of its done state.</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/TodoActionReceiver.kt'>TodoActionReceiver.kt</a></b></td>
							<td style='padding: 8px;'>Receives toggle and clear actions from the widget, updates the database on a background thread, then refreshes the widget.</td>
						</tr>
					</table>
					<details>
						<summary><b>screens</b></summary>
						<blockquote>
							<table style='width: 100%; border-collapse: collapse;'>
							<thead>
								<tr style='background-color: #f8f9fa;'>
									<th style='width: 30%; text-align: left; padding: 8px;'>File Name</th>
									<th style='text-align: left; padding: 8px;'>Summary</th>
								</tr>
							</thead>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/PetApp.kt'>PetApp.kt</a></b></td>
									<td style='padding: 8px;'>App shell with the bottom navigation bar, top app bar and tab routes, plus the widget guide page. Keeps each tab's state when switching and caps content width at 600dp on tablets.</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/home/HomeScreen.kt'>home/HomeScreen.kt</a></b></td>
									<td style='padding: 8px;'>Companion home tab. Shows whether the pet is active or resting, offers the permission or show/hide pet button, and links to quick notes and the widget guide.</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/notes/NotesScreen.kt'>notes/NotesScreen.kt</a></b></td>
									<td style='padding: 8px;'>To-do list for the Notes tab, with a pending count, an empty-state illustration, retry on load failure and a confirmation before clearing completed items.</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/notes/NotesViewModel.kt'>notes/NotesViewModel.kt</a></b></td>
									<td style='padding: 8px;'>Reloads the list whenever the database changes and handles toggling and clearing, refreshing the home-screen widget after every change.</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/notes/QuickNoteScreen.kt'>notes/QuickNoteScreen.kt</a></b></td>
									<td style='padding: 8px;'>Input UI for the quick-note dialog. Focuses automatically, saves on the keyboard's Done key, disables saving for blank text and shows an error on failure.</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/notes/QuickNoteViewModel.kt'>notes/QuickNoteViewModel.kt</a></b></td>
									<td style='padding: 8px;'>Keeps the draft and runs the save flow, making sure each note is written only once, then refreshes the widget.</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/settings/SettingsScreen.kt'>settings/SettingsScreen.kt</a></b></td>
									<td style='padding: 8px;'>Settings tab with the pet switch, overlay permission status, the custom pet menu, a link to the widget guide and the app version.</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/settings/PetMenuEditorScreen.kt'>settings/PetMenuEditorScreen.kt</a></b></td>
									<td style='padding: 8px;'>The custom pet menu screen, previewing the layout as a 3×3 grid with the pet in the middle. After selecting a slot you can assign a built-in feature, pick another app from a searchable list, or remove an app already there; the layout can also be reset to the default.</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/settings/PetMenuEditorViewModel.kt'>settings/PetMenuEditorViewModel.kt</a></b></td>
									<td style='padding: 8px;'>Reads and saves the menu layout and loads the phone's launchable apps in the background (excluding this app, sorted by name). Each built-in feature appears exactly once: assigning it to a new slot swaps it with its current slot, and an app placed over a built-in feature moves that feature into an empty slot, or is refused when none is free.</td>
								</tr>
							</table>
						</blockquote>
					</details>
					<details>
						<summary><b>ui</b></summary>
						<blockquote>
							<table style='width: 100%; border-collapse: collapse;'>
							<thead>
								<tr style='background-color: #f8f9fa;'>
									<th style='width: 30%; text-align: left; padding: 8px;'>File Name</th>
									<th style='text-align: left; padding: 8px;'>Summary</th>
								</tr>
							</thead>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/ui/PetTheme.kt'>PetTheme.kt</a></b></td>
									<td style='padding: 8px;'>Cream journal Material 3 theme with day and night palettes, type scale and corner shapes. Follows the system dark mode and does not use wallpaper dynamic color.</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/ui/PetArtwork.kt'>PetArtwork.kt</a></b></td>
									<td style='padding: 8px;'>Draws a still of the pet inside Compose screens. It is decorative, so screen readers skip it.</td>
								</tr>
							</table>
						</blockquote>
					</details>
				</blockquote>
			</details>
			<details>
				<summary><b>src/androidTest</b></summary>
				<blockquote>
					<table style='width: 100%; border-collapse: collapse;'>
					<thead>
						<tr style='background-color: #f8f9fa;'>
							<th style='width: 30%; text-align: left; padding: 8px;'>File Name</th>
							<th style='text-align: left; padding: 8px;'>Summary</th>
						</tr>
					</thead>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/androidTest/java/com/umuumu/virtualpet/CreamInterfaceTest.kt'>CreamInterfaceTest.kt</a></b></td>
							<td style='padding: 8px;'>On-device UI tests covering <code>MainActivity</code> reuse, a draft that survives recreation and saves exactly once, widget toggles syncing to the open list, and rendering and tapping the real widget.</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/androidTest/java/com/umuumu/virtualpet/PetMenuEditorTest.kt'>PetMenuEditorTest.kt</a></b></td>
							<td style='padding: 8px;'>Tests for the custom pet menu: selecting another slot only changes the selection and leaves the layout alone, assigning a built-in feature swaps it with its current slot, and placing an app over a built-in feature moves that feature into an empty slot. Each test restores the original layout afterwards.</td>
						</tr>
					</table>
				</blockquote>
			</details>
		</blockquote>
	</details>
</details>

---

## Getting Started

### Prerequisites

- **Programming Language:** Kotlin (JDK 17)
- **Build Tool:** Gradle 8.11.1 (Gradle Wrapper included)
- **Android SDK:** compileSdk 35
- **Device:** a phone or emulator running Android 9 (API 28) or later

Opening the project in Android Studio is the easiest route. It generates `local.properties` pointing at your Android SDK. To build from the command line only, first set the `ANDROID_HOME` environment variable to your Android SDK path, or create `local.properties` in the project root containing `sdk.dir=<path to Android SDK>`; otherwise the build fails with `SDK location not found`.

### Installation

1. **Clone the repository:**

    ```sh
    ❯ git clone https://github.com/rowing195/fhibi-chubi-virtual-pet.git
    ```

2. **Navigate to the project directory:**

    ```sh
    ❯ cd fhibi-chubi-virtual-pet
    ```

3. **Build and install the debug build on a connected device:**

    ```sh
    ❯ ./gradlew installDebug
    ```

    On Windows use `gradlew.bat installDebug`. To build the APK only, run `./gradlew assembleDebug`; the output lands in `app/build/outputs/apk/debug/`.

### Usage

1. Open Desktop Pet (桌面寵物) and tap **前往授權** (Grant permission) on the home screen to allow "Display over other apps".
2. Back in the app, tap **叫出寵物** (Show pet) and Fibi appears on screen.
3. **Drag** the pet to move it. When you let go it snaps to the nearest screen edge.
4. **Tap** the pet to open its menu. Choose **摸摸桌寵** (Pet actions) to play an animation, or **記一筆** (Quick note) to enter a note and tap **記下** (Save).
5. Open **Settings → Custom pet menu**, select a slot, then choose a built-in action or **Other apps** below. Selecting another slot only changes the selection; assigning a built-in action swaps it with its current slot or moves it into an empty slot.
6. Long-press an empty spot on your home screen → Widgets → add **待辦清單** (To-do list) to check off or add items right there.
7. To hide the pet, tap **收起寵物** (Hide pet) in the app or use the same action in the notification.

### Testing

The project uses AndroidX Test and Compose UI Test instrumentation tests. Connect a device or start an emulator first:

```sh
❯ ./gradlew connectedDebugAndroidTest
```

You can also run Android Lint:

```sh
❯ ./gradlew lint
```

### Release Signing

The release keystore lives outside the repo and is read from user-level Gradle properties (for example `~/.gradle/gradle.properties`):

```properties
PUBLIC_PET_RELEASE_STORE_FILE=/path/to/keystore.jks
PUBLIC_PET_RELEASE_STORE_PASSWORD=...
PUBLIC_PET_RELEASE_KEY_ALIAS=...
PUBLIC_PET_RELEASE_KEY_PASSWORD=...
```

Then run:

```sh
❯ ./gradlew assembleRelease
```

Without these properties `assembleRelease` still builds, but the APK is unsigned. Never commit the keystore or passwords.

When moving to another computer, note that the keystore file itself is not in the repo: copy it together with the four settings above and point `PUBLIC_PET_RELEASE_STORE_FILE` at its new location. Every APK on GitHub Releases is signed with this key, and a build signed with a different key cannot update an installed copy, so keep a backup of it.

> [!NOTE]
> This edition and the AirScroll-integrated edition share the same application ID but use different signing keys, so neither can be installed over the other, and they cannot be installed side by side.

---

## License

This project is released under the [MIT License](LICENSE).

<div align="left"><a href="#top">Back to top</a></div>

---
