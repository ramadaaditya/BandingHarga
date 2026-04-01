package com.ramstudio.bandingharga.ui.component


import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ramstudio.bandingharga.model.StoreType


@Composable
 fun DashboardTabs(
    activeTab: StoreType,
    onTabChange: (StoreType) -> Unit,
    onlineCount: Int,
    offlineCount: Int
) {
    TabRow(selectedTabIndex = if (activeTab == StoreType.ONLINE) 0 else 1) {
        Tab(
            selected = activeTab == StoreType.ONLINE,
            onClick = { onTabChange(StoreType.ONLINE) },
            text = { Text(text = "Online ($onlineCount)") }
        )
        Tab(
            selected = activeTab == StoreType.OFFLINE,
            onClick = { onTabChange(StoreType.OFFLINE) },
            text = { Text(text = "Offline ($offlineCount)") }
        )
    }
}
