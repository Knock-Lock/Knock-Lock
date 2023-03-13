package com.knocklock.presentation.lockscreen.mapper

import android.app.PendingIntent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.service.notification.StatusBarNotification
import com.knocklock.presentation.lockscreen.model.Notification
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.knocklock.domain.model.Notification as NotificationModel

/**
 * @Created by 김현국 2023/03/07
 */

/**
 * StatusBarNotification를 Array<[NotificationModel]>로 변환합니다.
 *
 * StatusBarNotification를 수정할 수 없기 때문에 List->Array로 변환합니다.
 *
 * @param statusBarNotifications Array<out [StatusBarNotification]> activeNotifications 입니다.
 * @return Array<[NotificationModel]>
 */
fun toModel(statusBarNotifications: Array<out StatusBarNotification>, packageManager: PackageManager): Array<NotificationModel> {
    return statusBarNotifications.asSequence().filter { it.toModel(packageManager) != null }.map { it.toModel(packageManager)!! }.toList().toTypedArray()
}

/**
 * StatusBarNotification에서 Database에 저장할 키를 반환합니다.
 *
 * StatusBarNotification에서 title이나 content가 있는 경우 데이터베아스의 저장할 키를 생성합니다.
 * @param packageManager AppTitle을 가져오기 위한 packageManager입니다.
 * @return null OR Group.Key
 */
fun StatusBarNotification.getDatabaseKey(packageManager: PackageManager): String? {
    val title: String = convertString(this.notification.extras.getCharSequence("android.title"))
    val content: String = convertString(this.notification.extras.getCharSequence("android.text"))
    return if (title != "" || content != "") {
        val appTitle = getDrawableAndAppTitle(packageManager, packageName).first ?: ""
        val subText: String = convertString(this.notification.extras.getCharSequence("android.subText"))

        if (subText == "")getGroupKey(packageName, appTitle, title) else getGroupKey(packageName, appTitle, subText)
    } else {
        null
    }
}

/**
 * StatusBarNotification을 Domain Layer Model로 변환합니다.
 *
 * StatusBarNotification에서 title이나 content가 있는 경우 Notification Model(Domain)을 생성합니다.
 *
 * @param packageManager AppTitle을 가져오기 위한 packageManager입니다.
 * @return null OR Notification Domain Model
 */
fun StatusBarNotification.toModel(packageManager: PackageManager): NotificationModel? {
    val title: String = convertString(this.notification.extras.getCharSequence("android.title"))
    val content: String = convertString(this.notification.extras.getCharSequence("android.text"))
    return if (title != "" || content != "") {
        val subText: String = convertString(this.notification.extras.getCharSequence("android.subText"))
        val packageName = packageName

        val intent: PendingIntent? = notification.contentIntent

        val appTitle = getDrawableAndAppTitle(packageManager, packageName).first ?: ""

        NotificationModel(
            id = key,
            packageName = packageName,
            postedTime = postTime,
            appTitle = if (subText == "") appTitle else subText,
            title = title,
            content = content,
            isClearable = isClearable,
            groupKey = if (subText == "") getGroupKey(packageName, appTitle, title) else getGroupKey(packageName, appTitle, subText)
        )
    } else {
        null
    }
}

fun getGroupKey(packageName: String, appTitle: String, title: String): String {
    return packageName + appTitle + title
}

fun convertString(var1: Any?): String {
    return var1?.toString() ?: ""
}

/**
 * Notification Model(Domain)을 Presentation Layer Model로 변환합니다.
 *
 * packageName과 packageManager를 통해 AppTitle과 AppIcon Drawable을 얻습니다.
 * * * Notification Model(Domain)에 저장된 appTitle(SubText)가 없는 경우, [getDrawableAndAppTitle]을 통해 얻은 AppTitle로 지정합니다.
 * @param packageManager AppTitle을 가져오기 위한 packageManager입니다.
 * @return [Notification]
 */
fun NotificationModel.toModel(packageManager: PackageManager): Notification {
    val temp = getDrawableAndAppTitle(packageManager, packageName)
    val date = Date(this.postedTime)
    val stringPostTime = try {
        SimpleDateFormat("a HH:mm", Locale.KOREA).format(date)
    } catch (e: Exception) {
        ""
    }

    return Notification(
        id = this.id,
        drawable = temp.second,
        appTitle = if (this.appTitle != "") this.appTitle else temp.first ?: "",
        notiTime = stringPostTime,
        postedTime = this.postedTime,
        title = this.title,
        content = this.content,
        isClearable = this.isClearable,
        intent = null,
        packageName = this.packageName
    )
}

/**
 * PackageManager를 사용하여 AppTitle과 Drawable을 얻습니다.
 *
 * @param packageManager AppTitle을 가져오기 위한 packageManager입니다.
 * @param packageName PackageManager로 검색할 패키지명입니다.
 * @return Pair<String?, Drawable?>
 */
fun getDrawableAndAppTitle(packageManager: PackageManager, packageName: String): Pair<String?, Drawable?> {
    val applicationInfo: ApplicationInfo?
    var appTitle = ""
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        applicationInfo = runCatching {
            packageManager.getApplicationInfo(
                packageName,
                PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
            )
        }.onSuccess { info ->
            appTitle = packageManager.getApplicationLabel(info).toString()
        }.getOrNull()
    } else {
        applicationInfo = runCatching {
            packageManager.getApplicationInfo(packageName, 0)
        }.onSuccess { info ->
            appTitle = packageManager.getApplicationLabel(info).toString()
        }.getOrNull()
    }

    val icon: Drawable? = if (applicationInfo != null) {
        packageManager.getApplicationIcon(applicationInfo)
    } else null

    return Pair(appTitle, icon)
}
