# ClueCube｜APP 程式設計

113 學年度第二學期的 Android 課程作品。ClueCube 以 Kotlin 與 OpenGL ES 實作立方體拼圖和三視圖練習，探索三維形狀與二維投影的對應。

ClueCube 操作演示實錄 [Demo](https://youtu.be/31w0gicTVMQ) 

## 專案導覽

| 路徑 | 內容 |
| --- | --- |
| [Final_Project/ClueCube](Final_Project/ClueCube) | 主要 Android 專案與介面、網格、立方體及投影程式 |
| [Practice](Practice) | 課堂 Android 練習，各資料夾為獨立專案 |
| [prototypes](prototypes) | 期末專案的早期實作版本 |
| [docs](docs) | 企劃書、簡報（PDF 與可編輯版本）及 Demo.mov |
| [SOURCE_MANIFEST.json](SOURCE_MANIFEST.json) | 來源檔案路徑、大小與 SHA-256 校驗碼 |

主要程式包含 `CubeGrid`、`CellGrid`、`MyGLRenderer`、`MyGLSurfaceView`、`OrthographicViewGenerator` 與 `UnitCube` 等類別。專案保留原課程版本，未重命名內部 package。

## 開啟與建置

1. 使用 Android Studio 開啟 `Final_Project/ClueCube`，不要開啟整個儲存庫根目錄。
2. 依專案設定安裝 Android SDK 35，使用支援 Android Gradle Plugin 8.8.0 的 JDK 17，完成 Gradle 同步。
3. 在 Android 7.0（API 24）以上的裝置或模擬器上執行 `app`。

專案使用 Kotlin 1.9.24、Gradle Wrapper 8.10.2，`compileSdk`／`targetSdk` 為 35，Java／Kotlin 編譯目標為 11。各課堂練習及原型可能使用不同設定，請分別開啟。

## 版本與素材

本庫由原課程備份拆分整理，保留程式、資源與文件的來源內容。Android 建置產物、個人 SDK 路徑與簽章金鑰不屬於上傳範圍。團隊與第三方素材依原有權利使用，未新增統一授權；此次整理未重新完成 Android 建置或實機操作測試。
