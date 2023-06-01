package com.knocklock.presentation.lockscreen.util

import androidx.compose.runtime.snapshots.SnapshotStateMap

/**
 * @Created by 김현국 2023/05/15
 */

/**
 * Map을 SnapShotStateMap으로 변환합니다.
 */
fun <T, K>Map<T, K>.toSnapShotStateMap(): SnapshotStateMap<T, K> {
    val snapshotStateMap = SnapshotStateMap<T, K>()
    for (entry in this) {
        snapshotStateMap[entry.key] = entry.value
    }
    return snapshotStateMap
}
