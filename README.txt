# OneGo Android app — GitHub build

This project makes an Android app for OneGo BackLoad. The app opens the live OneGo website, so changes to the website appear in the app without rebuilding the APK. Changes to the Android wrapper itself do need a new APK.

## Build an APK with GitHub Actions

1. Create a GitHub repository named `onego-android`. Make it **public** if clients should download APKs from GitHub Releases without signing in; use private if only your team should access the code and releases.
2. Unzip this package. Upload the **contents** of the `OneGoAndroidGitHubBuild` folder to the repository root. The `.github` folder must be included.
3. In GitHub, open **Actions → Build OneGo Android app → Run workflow**. You can also start a build by pushing a commit to `main`.
4. Open the completed workflow run and download the `OneGo-debug-apk` artifact. Unzip it to get `app-debug.apk`.

The debug APK is for testing. It may show an Android warning because it is installed from outside Google Play. The Android app includes basic protections, but an APK downloaded directly from GitHub cannot promise that Android will suppress its “unknown app” or Play Protect warnings. For the most trusted client installation experience, publish the signed app through Google Play.

## Signed APKs for client releases

To create a release APK, make a long-term Android signing key and save it somewhere backed up and private. **Never commit the key or its passwords to GitHub.** The same key must be kept for every update, or Android will not install a future APK over the existing one.

On a computer with Java installed, run:

```bash
keytool -genkeypair -v -keystore onego-release.jks -alias onego -keyalg RSA -keysize 3072 -validity 10000
```

Convert the keystore file to a single-line Base64 value:

**macOS / Linux**

```bash
base64 < onego-release.jks | tr -d '\n' > onego-release.base64.txt
```

**Windows PowerShell**

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("onego-release.jks")) | Set-Content -NoNewline onego-release.base64.txt
```

In the GitHub repository, open **Settings → Secrets and variables → Actions → New repository secret** and add:

| Secret name | Value |
| --- | --- |
| `ONEGO_KEYSTORE_BASE64` | Entire contents of `onego-release.base64.txt` |
| `ONEGO_STORE_PASSWORD` | Keystore password chosen with `keytool` |
| `ONEGO_KEY_ALIAS` | `onego` |
| `ONEGO_KEY_PASSWORD` | Key password chosen with `keytool` |

After saving the secrets, create and push a version tag to build and publish a signed APK:

```bash
git tag v1.0.0
git push origin v1.0.0
```

The workflow creates a GitHub Release containing `app-release.apk`. Give clients the Release download page. On first install, Android may still show a warning when the APK comes from outside Google Play. A signed APK lets Android verify that future APKs came from the same publisher; it does not remove all sideloading warnings. Future releases must use the same keystore.

## Security included

- Release APKs are signed when the four GitHub secrets are configured.
- The app connects to the OneGo website over HTTPS and blocks cleartext HTTP traffic.
- The embedded browser is restricted to the OneGo site. Other HTTPS links open in the device browser; phone links open the phone app.
- JavaScript and browser storage are enabled because the OneGo website needs them. File access, file URL access, mixed HTTP content, and JavaScript pop-up windows are disabled.
- Android Safe Browsing is enabled where the device supports it. The app requests internet access only; it does not request contacts, location, camera, microphone, or storage permissions.

## Existing users and updates

The app loads `https://onego-backload-network.astute-loom-2153.chatgpt.site`. It uses the same website account and listing data as the browser. Each client registers or signs in on the website from inside the app.

For a first client release, distribute the signed `app-release.apk`. If someone installed a debug APK first, they must uninstall it before installing the signed app because the debug and release signing keys differ. Once they have the signed version, later releases signed with the same saved key can install as updates. APK updates downloaded from GitHub are manual; Google Play can manage updates for users when the app is published there.

## Project details

- Android application ID: `com.onego.backload`
- Java-based Android WebView wrapper
- Android Gradle Plugin 9.5.0, Gradle 9.6.0, JDK 17
- Minimum Android version: Android 6.0 (API 23)

## Build locally (optional)

Install JDK 17 and Android SDK platform 36, then run:

```bash
gradle --no-daemon assembleDebug
```

The debug APK is created at `app/build/outputs/apk/debug/app-debug.apk`.
