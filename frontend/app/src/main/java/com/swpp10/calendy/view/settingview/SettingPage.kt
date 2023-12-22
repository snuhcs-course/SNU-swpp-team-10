package com.swpp10.calendy.view.settingview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class SettingItem(val title: String, val icon: ImageVector)

val settingsList = listOf(
    SettingItem("계정", Icons.Default.AccountCircle),
    SettingItem("AI 비서", Icons.Default.Assistant),
    SettingItem("알림", Icons.Default.Notifications),
    SettingItem("도움말", Icons.Default.Help),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setting", style = MaterialTheme.typography.headlineMedium) },

            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            UserProfileSection()
            LazyColumn() {
                items(settingsList) { item ->
                    SettingItem(settingItem = item)
                }
            }
        }
    }
}

@Composable
fun SettingItem(settingItem: SettingItem) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* 항목 클릭 시 수행할 작업 */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = settingItem.icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = settingItem.title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )
        Spacer(Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null
        )
    }
}

@Composable
fun UserProfileSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "User Profile",
            modifier = Modifier
                .size(40.dp)
                .background(Color.Gray, shape = MaterialTheme.shapes.small),
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "사용자 이름",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}



@Preview(showBackground = true)
@Composable
fun SettingPreview() {
    MaterialTheme {
        SettingPage()
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileSectionPreview() {
    Surface {
        UserProfileSection()
    }
}
