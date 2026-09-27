<div align="center" id="top">

**繁體中文** | [English](README.en.md)

<!-- HEADER STYLE: CLASSIC -->
<img src="art/IMG_4838_preview_based.gif" width="160" alt="Fibi 桌寵"/>

# fhibi-chubi-virtual-pet

<em>浮在畫面上的小夥伴，替你記住小事</em>

<!-- BADGES -->
<img src="https://img.shields.io/badge/version-0.2.3-0080ff?style=flat" alt="version">
<img src="https://img.shields.io/badge/minSdk-28-0080ff?style=flat&logo=android&logoColor=white" alt="minSdk">
<img src="https://img.shields.io/badge/targetSdk-35-0080ff?style=flat&logo=android&logoColor=white" alt="targetSdk">
<img src="https://img.shields.io/badge/license-MIT-green?style=flat" alt="license">

<em>使用的工具與技術：</em>

<img src="https://img.shields.io/badge/Kotlin-7F52FF.svg?style=flat&logo=kotlin&logoColor=white" alt="Kotlin">
<img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4.svg?style=flat&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose">
<img src="https://img.shields.io/badge/Material%203-757575.svg?style=flat&logo=materialdesign&logoColor=white" alt="Material 3">
<img src="https://img.shields.io/badge/Android-34A853.svg?style=flat&logo=android&logoColor=white" alt="Android">
<img src="https://img.shields.io/badge/SQLite-003B57.svg?style=flat&logo=sqlite&logoColor=white" alt="SQLite">
<img src="https://img.shields.io/badge/Gradle-02303A.svg?style=flat&logo=gradle&logoColor=white" alt="Gradle">

</div>
<br>

---

### 目錄

- [專案概覽](#專案概覽)
- [功能特色](#功能特色)
- [專案結構](#專案結構)
    - [檔案索引](#檔案索引)
- [快速開始](#快速開始)
    - [環境需求](#環境需求)
    - [安裝](#安裝)
    - [使用方式](#使用方式)
    - [測試](#測試)
    - [正式版簽章](#正式版簽章)
- [授權](#授權)

---

## 專案概覽

「桌面寵物」是一個 Android App：Fibi 會浮在其他 App 上方陪著你，點一下牠可以選動作或隨手記下待辦，桌面小工具則讓清單一眼看得到、一鍵勾掉。這是**獨立的公開準備版**，保留桌寵互動、記一筆與小工具，不含相機、網路、AI 或 AirScroll 整合。

**為什麼選 fhibi-chubi-virtual-pet？**

這個專案想把「想到就記」的門檻降到最低，核心功能包括：

- **🐾 浮動桌寵：** 可拖曳的懸浮視窗，放開後自動吸附到螢幕邊緣，拖動時會朝移動方向跑。
- **🐾 摸摸桌寵：** 點桌寵開啟環形選單，可播放待機、左右跑、揮手、跳躍、失落、完成檢視與寫筆記。
- **🎛️ 自訂桌寵選單：** 可調整內建功能的位置，或指定其他 App 捷徑；點格子只切換選取，下方選項才變更功能，並顯示 App 圖示。
- **📝 隨手記：** 從桌寵選單選「記一筆」開啟對話框，桌寵會跟著寫字，儲存或取消時各有不同的反應動畫。
- **📋 桌面待辦小工具：** 可自由縮放的清單，直接在桌面上新增、勾選、清除已完成。
- **🔄 資料即時同步：** App、記一筆與小工具共用同一個本機 SQLite 資料庫，任何一邊修改都會立刻反映。
- **🎨 奶油手帳介面：** 奶油／橄欖色系，跟隨系統深色模式，支援放大字體。
- **🔒 純本機：** 不要求網路權限，資料只留在手機上。

---

## 功能特色

|      | 元件 | 說明 |
| :--- | :--- | :--- |
| ⚙️ | **架構** | <ul><li>單一 `app` 模組，套件 `com.umuumu.virtualpet`</li><li>Jetpack Compose UI + `AndroidViewModel` + `StateFlow`</li><li>Navigation Compose 三個主分頁：陪伴／記一筆／設定</li><li>`PetOverlayService` 前景服務負責懸浮桌寵</li></ul> |
| 🔩 | **程式品質** | <ul><li>Kotlin 2.0.21、JVM 17</li><li>記一筆草稿透過 `SavedStateHandle` 跨畫面重建保留</li><li>空白內容不能存、存檔中防重複送出</li><li>無障礙：標題語意、`liveRegion` 錯誤提示、小工具列的內容描述</li></ul> |
| 📄 | **文件** | <ul><li>`.ui-work/` 內有設計規格、設計交接與各版發布說明</li><li>`CLAUDE.md` 記錄給 Claude Code 的開發規則（與整合版的分工、簽章與發版流程）</li></ul> |
| 🔌 | **系統整合** | <ul><li>`SYSTEM_ALERT_WINDOW` 懸浮視窗</li><li>`specialUse` 類型前景服務與常駐通知（可從通知收起桌寵）</li><li>`AppWidgetProvider` + `RemoteViewsService` 桌面小工具</li></ul> |
| 🧩 | **模組化** | <ul><li>`screens/` 依畫面分包：`home`、`notes`、`settings`</li><li>`ui/` 放主題與角色圖</li><li>`TodoRepository` 單例集中所有待辦讀寫</li></ul> |
| 🧪 | **測試** | <ul><li>8 項 instrumentation 測試，涵蓋 `CreamInterfaceTest` 與選單編輯器</li><li>涵蓋草稿保留、清單與小工具同步、真實小工具點擊、`MainActivity` 重用、換頁時畫面不跳動與選單功能配置</li></ul> |
| ⚡️ | **效能** | <ul><li>資料庫操作走 `Dispatchers.IO`</li><li>廣播接收器與小工具更新用 `goAsync()` 在背景執行緒處理</li><li>桌寵以單張 sprite sheet 逐格繪製</li></ul> |
| 🛡️ | **安全** | <ul><li>沒有 `INTERNET` 權限</li><li>內部元件 `exported="false"`，廣播限定本 App 套件</li><li>正式版金鑰從 Gradle properties 讀取，不進版控</li></ul> |
| 📦 | **相依套件** | <ul><li>Compose BOM `2024.12.01`、Material 3</li><li>`activity-compose` 1.9.3、`lifecycle-*-compose` 2.8.7、`navigation-compose` 2.8.5</li><li>Android Gradle Plugin 8.7.3、Gradle 8.11.1</li></ul> |

---

## 專案結構

```sh
└── fhibi-chubi-virtual-pet/
    ├── .ui-work/                  # 設計規格與發布說明
    ├── CLAUDE.md                  # 給 Claude Code 的開發規則
    ├── app/
    │   ├── build.gradle.kts
    │   └── src/
    │       ├── androidTest/       # instrumentation 測試
    │       └── main/
    │           ├── AndroidManifest.xml
    │           ├── java/com/umuumu/virtualpet/
    │           │   ├── screens/   # Compose 畫面（home、notes、settings）
    │           │   └── ui/        # 主題與角色圖
    │           └── res/           # sprite sheet、小工具版面、字串、圖示
    ├── art/                       # 角色原始素材（GIF、sprite sheet）
    ├── build.gradle.kts
    ├── gradle/wrapper/
    ├── gradle.properties
    ├── gradlew
    ├── gradlew.bat
    └── settings.gradle.kts
```

### 檔案索引

<details open>
	<summary><b><code>FHIBI-CHUBI-VIRTUAL-PET/</code></b></summary>
	<details>
		<summary><b>__root__</b></summary>
		<blockquote>
			<table style='width: 100%; border-collapse: collapse;'>
			<thead>
				<tr style='background-color: #f8f9fa;'>
					<th style='width: 30%; text-align: left; padding: 8px;'>檔案</th>
					<th style='text-align: left; padding: 8px;'>摘要</th>
				</tr>
			</thead>
				<tr style='border-bottom: 1px solid #eee;'>
					<td style='padding: 8px;'><b><a href='build.gradle.kts'>build.gradle.kts</a></b></td>
					<td style='padding: 8px;'>宣告整個專案使用的 Android Gradle Plugin 8.7.3、Kotlin 2.0.21 與 Compose 編譯器外掛版本，實際套用交給 app 模組。</td>
				</tr>
				<tr style='border-bottom: 1px solid #eee;'>
					<td style='padding: 8px;'><b><a href='settings.gradle.kts'>settings.gradle.kts</a></b></td>
					<td style='padding: 8px;'>設定外掛與相依套件的來源倉庫（Google、Maven Central），並把唯一的 <code>:app</code> 模組納入 VirtualPet 專案。</td>
				</tr>
				<tr style='border-bottom: 1px solid #eee;'>
					<td style='padding: 8px;'><b><a href='gradle.properties'>gradle.properties</a></b></td>
					<td style='padding: 8px;'>Gradle 執行參數：JVM 記憶體、UTF-8 編碼、啟用 AndroidX 與非遞移式 R 類別。</td>
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
					<th style='width: 30%; text-align: left; padding: 8px;'>檔案</th>
					<th style='text-align: left; padding: 8px;'>摘要</th>
				</tr>
			</thead>
				<tr style='border-bottom: 1px solid #eee;'>
					<td style='padding: 8px;'><b><a href='app/build.gradle.kts'>build.gradle.kts</a></b></td>
					<td style='padding: 8px;'>定義 App 的套件 ID、SDK 範圍（28–35）、版本 0.2.3 與 Compose 相依套件。正式版簽章設定只在 Gradle properties 提供金鑰時才會建立，金鑰本身不進版控。</td>
				</tr>
				<tr style='border-bottom: 1px solid #eee;'>
					<td style='padding: 8px;'><b><a href='app/src/main/AndroidManifest.xml'>AndroidManifest.xml</a></b></td>
					<td style='padding: 8px;'>註冊主畫面、記一筆對話框、懸浮桌寵前景服務、小工具提供者與動作接收器。只申請懸浮視窗、前景服務與通知權限，沒有網路權限。</td>
				</tr>
			</table>
			<details>
				<summary><b>src/main/java/com/umuumu/virtualpet</b></summary>
				<blockquote>
					<table style='width: 100%; border-collapse: collapse;'>
					<thead>
						<tr style='background-color: #f8f9fa;'>
							<th style='width: 30%; text-align: left; padding: 8px;'>檔案</th>
							<th style='text-align: left; padding: 8px;'>摘要</th>
						</tr>
					</thead>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/MainActivity.kt'>MainActivity.kt</a></b></td>
							<td style='padding: 8px;'>App 進入點，掛上奶油主題與導覽。負責開關懸浮桌寵、導向懸浮視窗授權頁、開啟記一筆，並在 Android 13 以上要求通知權限。外部 Intent 可指定要跳到哪個分頁。</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/PetOverlayService.kt'>PetOverlayService.kt</a></b></td>
							<td style='padding: 8px;'>以前景服務在其他 App 上方顯示桌寵。處理拖曳、放開後吸附螢幕邊緣與點擊開啟環形選單，並接收記一筆的開啟／儲存／取消狀態來切換寫字、檢查、失敗等動畫。附常駐通知，可從通知收起桌寵。</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/PetSpriteView.kt'>PetSpriteView.kt</a></b></td>
							<td style='padding: 8px;'>從 sprite sheet 逐格繪製桌寵的自訂 View，定義待機、左右奔跑、揮手、跳躍、失敗、寫字、檢查等動畫。待機時慢慢呼吸、每隔幾秒才眨一次眼。</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/PetMenuView.kt'>PetMenuView.kt</a></b></td>
							<td style='padding: 8px;'>點桌寵後出現的全螢幕選單，在桌寵四周的 3×3 格畫出八個對話泡泡，尾巴都指向桌寵。泡泡顯示內建功能或 App 的圖示與名稱，也能切換成「摸摸桌寵」的動作子選單。點空格不會關閉選單，點泡泡以外的地方才會關閉。</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/PetMenuPreferences.kt'>PetMenuPreferences.kt</a></b></td>
							<td style='padding: 8px;'>定義選單可用的內建功能（摸摸桌寵、設定、記一筆、關閉），以及每格可放的內容：內建功能、其他 App 或空格。八格配置存在 SharedPreferences，無法解析的設定會退回預設配置，已無法開啟的 App 會顯示提示文字。</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/QuickNoteActivity.kt'>QuickNoteActivity.kt</a></b></td>
							<td style='padding: 8px;'>對話框樣式的記一筆畫面，桌寵、小工具與 App 都從這裡新增待辦。也兼任小工具「清除已完成」的確認框，並把開啟與關閉狀態通知桌寵。</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/TodoRepository.kt'>TodoRepository.kt</a></b></td>
							<td style='padding: 8px;'>單例 SQLite 資料層，App、記一筆與小工具共用。提供新增、切換完成、清除已完成，清單排序為未完成優先、新的在前，並以版本號 Flow 通知畫面重新載入。</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/VirtualPetAppWidgetProvider.kt'>VirtualPetAppWidgetProvider.kt</a></b></td>
							<td style='padding: 8px;'>桌面待辦小工具，標題旁顯示總件數，並綁定新增、勾選、清除已完成三種操作；清除按鈕只在有已完成項目時出現。提供 <code>refreshAll</code> 讓任何資料變動後同步更新所有已放置的小工具。</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/TodoWidgetService.kt'>TodoWidgetService.kt</a></b></td>
							<td style='padding: 8px;'>提供小工具清單的每一列，每筆待辦是一張小卡片，已完成項目加刪除線並換成勾選圖示，每列都有給螢幕閱讀器的完成狀態描述。</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/TodoActionReceiver.kt'>TodoActionReceiver.kt</a></b></td>
							<td style='padding: 8px;'>接收小工具上的勾選與清除動作，在背景執行緒更新資料庫後刷新小工具。</td>
						</tr>
					</table>
					<details>
						<summary><b>screens</b></summary>
						<blockquote>
							<table style='width: 100%; border-collapse: collapse;'>
							<thead>
								<tr style='background-color: #f8f9fa;'>
									<th style='width: 30%; text-align: left; padding: 8px;'>檔案</th>
									<th style='text-align: left; padding: 8px;'>摘要</th>
								</tr>
							</thead>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/PetApp.kt'>PetApp.kt</a></b></td>
									<td style='padding: 8px;'>App 外框：底部導覽列、頂部標題列與各分頁路由，另含小工具說明頁。切換分頁時保留各頁狀態，平板上內容寬度限制在 600dp。</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/home/HomeScreen.kt'>home/HomeScreen.kt</a></b></td>
									<td style='padding: 8px;'>「陪伴」首頁，顯示桌寵目前是陪伴中還是休息中，提供授權或叫出／收起桌寵的按鈕，以及記一筆與小工具的捷徑卡片。</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/notes/NotesScreen.kt'>notes/NotesScreen.kt</a></b></td>
									<td style='padding: 8px;'>「記一筆」分頁的待辦清單，包含未完成數量、空清單插圖、載入失敗重試，以及清除已完成前的確認框。</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/notes/NotesViewModel.kt'>notes/NotesViewModel.kt</a></b></td>
									<td style='padding: 8px;'>監聽資料庫變動並重新載入清單，處理勾選與清除，每次修改後同步刷新桌面小工具。</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/notes/QuickNoteScreen.kt'>notes/QuickNoteScreen.kt</a></b></td>
									<td style='padding: 8px;'>記一筆對話框的輸入介面，自動聚焦、鍵盤完成鍵直接儲存，空白時停用儲存鈕，失敗時顯示錯誤。</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/notes/QuickNoteViewModel.kt'>notes/QuickNoteViewModel.kt</a></b></td>
									<td style='padding: 8px;'>保存草稿並處理儲存流程，確保同一筆只寫入一次，完成後刷新小工具。</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/settings/SettingsScreen.kt'>settings/SettingsScreen.kt</a></b></td>
									<td style='padding: 8px;'>設定頁：顯示桌寵開關、懸浮視窗授權狀態、自訂桌寵選單、小工具說明入口與 App 版本。</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/settings/PetMenuEditorScreen.kt'>settings/PetMenuEditorScreen.kt</a></b></td>
									<td style='padding: 8px;'>「自訂桌寵選單」頁面，用 3×3 格預覽選單配置，中間是桌寵。選一格後可指定內建功能、從可搜尋的清單挑其他 App，或移除已放的 App，也能恢復預設排列。</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/screens/settings/PetMenuEditorViewModel.kt'>settings/PetMenuEditorViewModel.kt</a></b></td>
									<td style='padding: 8px;'>讀寫選單配置，並在背景載入手機上可啟動的 App 清單（排除本 App、依名稱排序）。每個內建功能只會出現一次：指定到新格子時會和原本的格子交換；App 蓋掉內建功能時，該功能會移到空格，沒有空格就不能放。</td>
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
									<th style='width: 30%; text-align: left; padding: 8px;'>檔案</th>
									<th style='text-align: left; padding: 8px;'>摘要</th>
								</tr>
							</thead>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/ui/PetTheme.kt'>PetTheme.kt</a></b></td>
									<td style='padding: 8px;'>奶油手帳的 Material 3 主題，日間與夜間兩組色票、字級與圓角。跟隨系統深色模式，不使用桌布動態取色。</td>
								</tr>
								<tr style='border-bottom: 1px solid #eee;'>
									<td style='padding: 8px;'><b><a href='app/src/main/java/com/umuumu/virtualpet/ui/PetArtwork.kt'>PetArtwork.kt</a></b></td>
									<td style='padding: 8px;'>在 Compose 畫面中畫出桌寵靜態圖，作為裝飾圖不會被螢幕閱讀器朗讀。</td>
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
							<th style='width: 30%; text-align: left; padding: 8px;'>檔案</th>
							<th style='text-align: left; padding: 8px;'>摘要</th>
						</tr>
					</thead>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/androidTest/java/com/umuumu/virtualpet/CreamInterfaceTest.kt'>CreamInterfaceTest.kt</a></b></td>
							<td style='padding: 8px;'>在實機或模擬器上執行的介面測試，驗證 <code>MainActivity</code> 重用、草稿跨重建保留且只存一次、小工具勾選同步到開啟中的清單，以及真實小工具的點擊與顯示。</td>
						</tr>
						<tr style='border-bottom: 1px solid #eee;'>
							<td style='padding: 8px;'><b><a href='app/src/androidTest/java/com/umuumu/virtualpet/PetMenuEditorTest.kt'>PetMenuEditorTest.kt</a></b></td>
							<td style='padding: 8px;'>自訂桌寵選單的測試，驗證點另一格只會切換選取、不改配置；指定內建功能會和原本的格子交換；把 App 放到內建功能的格子時，該功能會移到空格。每個測試結束後都會還原原本的配置。</td>
						</tr>
					</table>
				</blockquote>
			</details>
		</blockquote>
	</details>
</details>

---

## 快速開始

### 環境需求

- **程式語言：** Kotlin（JDK 17）
- **建置工具：** Gradle 8.11.1（已附 Gradle Wrapper）
- **Android SDK：** compileSdk 35
- **執行裝置：** Android 9（API 28）以上的手機或模擬器

建議直接用 Android Studio 開啟專案，它會自動產生 `local.properties` 指向你的 Android SDK。只用命令列建置的話，請先把環境變數 `ANDROID_HOME` 設為 Android SDK 的路徑，或在專案根目錄建立 `local.properties` 並寫入 `sdk.dir=<Android SDK 路徑>`，否則建置會出現 `SDK location not found`。

### 安裝

1. **Clone 專案：**

    ```sh
    ❯ git clone https://github.com/rowing195/fhibi-chubi-virtual-pet.git
    ```

2. **進入專案資料夾：**

    ```sh
    ❯ cd fhibi-chubi-virtual-pet
    ```

3. **建置並安裝 debug 版到已連接的裝置：**

    ```sh
    ❯ ./gradlew installDebug
    ```

    Windows 請用 `gradlew.bat installDebug`。只想產生 APK 可以用 `./gradlew assembleDebug`，檔案會在 `app/build/outputs/apk/debug/`。

### 使用方式

1. 打開「桌面寵物」，在首頁按 **前往授權**，允許「顯示在其他應用程式上層」。
2. 回到 App 按 **叫出寵物**，Fibi 就會浮在畫面上。
3. **拖曳**可以移動桌寵，放開後會自動貼到最近的螢幕邊緣。
4. **點一下**桌寵開啟選單；選「摸摸桌寵」可播放動作，選「記一筆」可輸入內容後按「記下」。
5. 到 **設定 → 自訂桌寵選單**，點選要設定的格子，再從下方選內建功能或「其他應用程式」；點另一格只會切換選取，內建功能會交換位置或移到空位。
6. 在桌面長按空白處 → 小工具 → 找到「待辦清單」加入桌面，就能直接勾選或新增待辦。
7. 想收起桌寵，可以在 App 裡按 **收起寵物**，或從通知列的「收起寵物」操作。

### 測試

專案使用 AndroidX Test 與 Compose UI Test 的 instrumentation 測試，需要先連接實機或啟動模擬器：

```sh
❯ ./gradlew connectedDebugAndroidTest
```

另外可以用 Android Lint 檢查：

```sh
❯ ./gradlew lint
```

### 正式版簽章

正式版簽章金鑰放在 repo 外，從使用者層級的 Gradle properties（例如 `~/.gradle/gradle.properties`）讀取：

```properties
PUBLIC_PET_RELEASE_STORE_FILE=/path/to/keystore.jks
PUBLIC_PET_RELEASE_STORE_PASSWORD=...
PUBLIC_PET_RELEASE_KEY_ALIAS=...
PUBLIC_PET_RELEASE_KEY_PASSWORD=...
```

設定好之後執行：

```sh
❯ ./gradlew assembleRelease
```

沒有設定這些值時，`assembleRelease` 仍可建置，但產出的是未簽章的 APK。請勿把 keystore 或密碼提交到 repo。

換電腦開發時，keystore 檔本身不在 repo 裡：請把它和上面四行設定一起複製到新電腦，並把 `PUBLIC_PET_RELEASE_STORE_FILE` 改成新的路徑。GitHub Release 上的 APK 都是用這把金鑰簽署的，換了金鑰建出來的版本無法覆蓋更新已安裝的舊版，所以請另外備份這把金鑰。

> [!NOTE]
> 本版與含 AirScroll 的整合版使用相同套件 ID、不同簽章，兩者不能互相覆蓋安裝，也不能同時安裝。

---

## 授權

本專案採用 [MIT 授權](LICENSE) 釋出。

<div align="left"><a href="#top">回到頂端</a></div>

---
