# 奶油手帳 Android specification — standalone

依 `design-handoff.md` 實作 Compose Material 3，維持 Kotlin 2.0.21、AGP 8.7.3、min 28／target 35。BOM 2024.12.01、Activity 1.9.3、Lifecycle 2.8.7、Navigation 2.8.5。

主畫面、設定、清單、widget 引導與 QuickNoteActivity 使用 Compose；桌面 widget 保留 RemoteViews。ViewModel／StateFlow 管理狀態，生命週期收集，SQLite IO 在背景執行。資料庫結構不變。

全螢幕介面消化系統 inset；輸入浮層配合原生 IME resize，可捲至儲存。600dp 以上限制內容寬度；大字體不縮字。主要操作至少 48dp；清單整列勾選與狀態語意合一。桌面 widget 最小可縮尺寸 180×180dp，列表可捲動，顯示簡短待辦數。

跟隨系統亮暗，停用桌布動態色；補齊 Material surface container 色票，避免卡片回退成紫色。原型的固定高度改成可捲動原生佈局，不把系統鍵盤畫進 App。

驗證結果與 release 說明見 `release-v0.2.0.md`。未聲稱完整 TalkBack、所有 launcher、摺疊機或橫向實機驗收。公開準備版維持沒有相機、網路、AI 與 AirScroll；repo 的 private 可見性不變。

Release 簽署使用使用者 Gradle properties 的 PUBLIC_PET_RELEASE_STORE_FILE、PUBLIC_PET_RELEASE_STORE_PASSWORD、PUBLIC_PET_RELEASE_KEY_ALIAS、PUBLIC_PET_RELEASE_KEY_PASSWORD。金鑰在 repo 外，後續更新必須沿用同一簽章。不得提交 keystore 或密碼。
