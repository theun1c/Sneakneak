package com.example.sneakneak.ui.main.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sneakneak.ui.components.AppCircularIconButton
import com.example.sneakneak.ui.components.AppIcon
import com.example.sneakneak.ui.components.AppIconAsset
import com.example.sneakneak.ui.components.BackButton
import com.example.sneakneak.ui.components.BottomNavBar
import com.example.sneakneak.ui.components.BottomNavItem
import com.example.sneakneak.ui.components.ProfileAvatar
import com.example.sneakneak.ui.navigation.AppRoutes
import com.example.sneakneak.ui.theme.AppColors
import kotlinx.coroutines.launch

// Shared shell for all authenticated screens: top bar, drawer and bottom navigation.
// It keeps the main screens visually consistent while feature content stays isolated.
data class MainDrawerItem(
    val title: String,
    val icon: AppIconAsset,
    val route: String,
    val badge: Boolean = false,
)

val DefaultDrawerItems = listOf(
    MainDrawerItem("Профиль", AppIconAsset.Profile, route = com.example.sneakneak.ui.navigation.AppRoutes.Profile.route),
    MainDrawerItem("Корзина", AppIconAsset.Bag, route = com.example.sneakneak.ui.navigation.AppRoutes.Cart.route),
    MainDrawerItem("Избранное", AppIconAsset.HeartOutline, route = com.example.sneakneak.ui.navigation.AppRoutes.Favorite.route),
    MainDrawerItem("Заказы", AppIconAsset.Truck, route = com.example.sneakneak.ui.navigation.AppRoutes.Orders.route),
    MainDrawerItem("Уведомления", AppIconAsset.Bell, route = com.example.sneakneak.ui.navigation.AppRoutes.Notifications.route, badge = true),
    MainDrawerItem("Настройки", AppIconAsset.Settings, route = com.example.sneakneak.ui.navigation.AppRoutes.Settings.route),
)

sealed interface MainTopBarStyle {
    data class MenuTitleAction(
        val title: String,
        val actionIcon: AppIconAsset? = null,
        val actionBadge: Boolean = false,
        val actionTint: Color = AppColors.TextPrimary,
    ) : MainTopBarStyle

    data class BackTitleAction(
        val title: String,
        val actionIcon: AppIconAsset? = null,
        val actionTint: Color = AppColors.TextPrimary,
    ) : MainTopBarStyle

    data class CenterButton(
        val buttonText: String,
    ) : MainTopBarStyle
}

@Composable
fun MainShellScaffold(
    currentRoute: String,
    topBarStyle: MainTopBarStyle,
    modifier: Modifier = Modifier,
    showBottomBar: Boolean = true,
    onBottomItemClick: (BottomNavItem) -> Unit,
    onDrawerItemClick: (MainDrawerItem) -> Unit,
    onBackClick: () -> Unit = {},
    onTopActionClick: () -> Unit = {},
    content: @Composable (Modifier) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = topBarStyle is MainTopBarStyle.MenuTitleAction,
        drawerContent = {
            MainDrawer(
                items = DefaultDrawerItems,
                onItemClick = {
                    scope.launch { drawerState.close() }
                    onDrawerItemClick(it)
                },
                onLogoutClick = {
                    scope.launch { drawerState.close() }
                    onDrawerItemClick(
                        MainDrawerItem(
                            title = "Выйти",
                            icon = AppIconAsset.Logout,
                            route = AppRoutes.Logout.route,
                        )
                    )
                },
            )
        },
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = AppColors.Background,
            topBar = {
                MainTopBar(
                    style = topBarStyle,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onBackClick = onBackClick,
                    onActionClick = onTopActionClick,
                )
            },
            bottomBar = {
                if (showBottomBar) {
                    BottomNavBar(
                        currentRoute = currentRoute,
                        onItemClick = onBottomItemClick,
                    )
                }
            },
        ) { padding ->
            content(Modifier.padding(padding))
        }
    }
}

@Composable
fun MainTopBar(
    style: MainTopBarStyle,
    onMenuClick: () -> Unit,
    onBackClick: () -> Unit,
    onActionClick: () -> Unit,
) {
    when (style) {
        is MainTopBarStyle.MenuTitleAction -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppCircularIconButton(
                    asset = AppIconAsset.Menu,
                    onClick = onMenuClick,
                    contentDescription = "Меню",
                    size = 40.dp,
                    containerColor = Color.Transparent,
                    iconTint = AppColors.TextPrimary,
                )
                Text(
                    text = style.title,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineLarge,
                    color = AppColors.TextPrimary,
                )
                if (style.actionIcon != null) {
                    BadgedCircleIcon(
                        asset = style.actionIcon,
                        onClick = onActionClick,
                        tint = style.actionTint,
                        showBadge = style.actionBadge,
                    )
                } else {
                    Spacer(modifier = Modifier.width(40.dp))
                }
            }
        }

        is MainTopBarStyle.BackTitleAction -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BackButton(onClick = onBackClick)
                Text(
                    text = style.title,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    color = AppColors.TextPrimary,
                )
                if (style.actionIcon != null) {
                    AppCircularIconButton(
                        asset = style.actionIcon,
                        onClick = onActionClick,
                        contentDescription = style.title,
                        size = 40.dp,
                        containerColor = AppColors.Surface,
                        iconTint = style.actionTint,
                    )
                } else {
                    Spacer(modifier = Modifier.width(40.dp))
                }
            }
        }

        is MainTopBarStyle.CenterButton -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .clickable(onClick = onActionClick),
                    shape = MaterialTheme.shapes.medium,
                    color = AppColors.Primary,
                ) {
                    Text(
                        text = style.buttonText,
                        modifier = Modifier.padding(vertical = 10.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                        color = AppColors.OnPrimary,
                    )
                }
            }
        }
    }
}

@Composable
fun MainDrawer(
    items: List<MainDrawerItem>,
    onItemClick: (MainDrawerItem) -> Unit,
    onLogoutClick: () -> Unit,
) {
    ModalDrawerSheet(
        modifier = Modifier.fillMaxHeight(),
        drawerContainerColor = AppColors.Primary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Primary)
                .padding(horizontal = 20.dp, vertical = 28.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            ProfileAvatar(
                name = "ЭК",
                size = 76.dp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Эмануэль Кверти",
                style = MaterialTheme.typography.headlineLarge,
                color = AppColors.Surface,
            )
            Spacer(modifier = Modifier.height(36.dp))

            items.forEach { item ->
                DrawerMenuRow(
                    item = item,
                    onClick = { onItemClick(item) },
                )
                Spacer(modifier = Modifier.height(18.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = AppColors.PrimaryLight.copy(alpha = 0.45f))
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onLogoutClick),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppIcon(
                    asset = AppIconAsset.Logout,
                    contentDescription = "Выйти",
                    tint = AppColors.Surface,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Выйти",
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.Surface,
                )
            }
        }
    }
}

@Composable
fun DrawerMenuRow(
    item: MainDrawerItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            AppIcon(
                asset = item.icon,
                contentDescription = item.title,
                tint = AppColors.Surface,
            )
            if (item.badge) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(AppColors.Accent, androidx.compose.foundation.shape.CircleShape)
                        .align(Alignment.TopEnd),
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.Surface,
        )
        AppIcon(
            asset = AppIconAsset.ChevronRight,
            contentDescription = null,
            tint = AppColors.Surface,
        )
    }
}

@Composable
fun BadgedCircleIcon(
    asset: AppIconAsset,
    onClick: () -> Unit,
    tint: Color,
    showBadge: Boolean,
) {
    Box {
        AppCircularIconButton(
            asset = asset,
            onClick = onClick,
            contentDescription = null,
            size = 44.dp,
            containerColor = AppColors.Surface,
            iconTint = tint,
        )
        if (showBadge) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(AppColors.Accent, androidx.compose.foundation.shape.CircleShape)
                    .align(Alignment.TopEnd),
            )
        }
    }
}
