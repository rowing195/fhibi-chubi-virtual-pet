# fhibi-chubi-virtual-pet 開發規則

給 Claude Code 的專案規則。建置步驟與架構見 [README.md](README.md)。

## 這是公開版

- 這個 repo 是公開的獨立版：`rowing195/fhibi-chubi-virtual-pet`。
- 另有私人整合版 `rowing195/Virtual-pet-fibichiubi`（本機通常在同層的 `Virtual_Pet/`），功能先在那邊開發。版號與 commit 兩邊各自獨立。
- 公開版維持純本機功能：**不加入**網路、相機或螢幕截圖、AI（嘴替、API 金鑰）或 AirScroll 整合，也不要新增 `INTERNET` 等相關權限。
- 從整合版移植功能時，只挑使用者指定的部分，用 cherry-pick 或手動移植，並確認沒有帶進上述私人功能。

## Git

- 只保留 `main`，直接在 main 上 commit，不開長期分支。
- 使用者要求時才 commit / push。
- 這是公開 repo，commit 作者只能用 noreply 信箱：`rowing195 <187868073+rowing195@users.noreply.github.com>`（歷史已全部改寫成這個信箱）。
- 一個 commit 只做一件事，修 bug 與新功能分開。

## 簽章與安裝

- release 金鑰與密碼只放在使用者層級的 `~/.gradle/gradle.properties`（`PUBLIC_PET_RELEASE_*`）和 `~/.android/keystores/`，永遠不要寫進 repo。debug 版用預設簽章。
- 公開版與整合版的 applicationId 都是 `com.umuumu.virtualpet`，但簽章不同。使用者手機上裝的是整合版，**不要把公開版裝到使用者手機上**：會覆蓋失敗，強行安裝就得移除整合版，清掉使用者的待辦與 API 金鑰。公開版在模擬器上測。
- instrumentation 測試在模擬器上跑；手機也連著時先設 `ANDROID_SERIAL=emulator-5554`。

## 發版流程

1. 修改 `app/build.gradle.kts` 的 `versionCode`（+1）與 `versionName`，並更新兩份 README 的版本徽章與版號描述。
2. 在 `.ui-work/release-vX.Y.Z.md` 寫發布說明（繁體中文，格式參考既有檔案）。
3. 確認 `assembleRelease`、`lintRelease` 通過，commit、打 annotated tag `vX.Y.Z`，push main 與 tag。
4. 以該說明建立 GitHub Release，附上 `VirtualPet-public-vX.Y.Z.apk` 與 `SHA256SUMS.txt`。
