package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.ui.components.OramiLogo
import com.example.ui.theme.OramiAmber
import com.example.ui.theme.OramiCoral
import com.example.ui.theme.OramiEmerald
import com.example.ui.theme.OramiEmeraldLight
import com.example.viewmodel.AppScreen
import com.example.viewmodel.OramiViewModel
import com.example.viewmodel.UserProfile

@Composable
fun OrdersProfileScreen(
    viewModel: OramiViewModel,
    modifier: Modifier = Modifier
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var isEditProfileOpen by remember { mutableStateOf(false) }
    var activePolicyModal by remember { mutableStateOf<PolicyType?>(null) }

    if (!isLoggedIn) {
        // Logged Out State: Show Login & Sign Up Flow
        AuthLoginSignupScreen(
            onLogin = { email, pass -> viewModel.login(email, pass) },
            onSignup = { name, email, phone, pass -> viewModel.signup(name, email, phone, pass) },
            onOpenPolicy = { policy -> activePolicyModal = policy },
            modifier = modifier
        )
    } else {
        // Logged In State: Account Management & Orders
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag("orders_profile_screen"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Profile Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar circle with monogram
                            val initials = userProfile.name.split(" ")
                                .mapNotNull { it.firstOrNull()?.toString() }
                                .take(2)
                                .joinToString("")
                                .ifEmpty { "U" }

                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initials,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = userProfile.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = "Verified Member",
                                        tint = OramiEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Text(
                                    text = userProfile.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Box(
                                    modifier = Modifier
                                        .background(OramiAmber.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = userProfile.memberTier,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            IconButton(
                                onClick = { isEditProfileOpen = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Log Out action
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Phone: ${userProfile.phone}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            TextButton(
                                onClick = { viewModel.logout() },
                                modifier = Modifier.testTag("profile_logout_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = null,
                                    tint = OramiCoral,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Log Out",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = OramiCoral
                                )
                            }
                        }
                    }
                }
            }

            // Tabs: My Orders vs Address & Support
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "My Orders (${orders.size})",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "Saved Address",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                text = "Help & Policies",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // Orders List
                    if (orders.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("No orders placed yet.", style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                                    Text("Shop Now")
                                }
                            }
                        }
                    } else {
                        items(orders, key = { it.orderId }) { order ->
                            OrderHistoryCard(
                                order = order,
                                onReorder = {
                                    order.items.forEach { viewModel.addToCart(it.product, it.selectedVariant, it.quantity) }
                                    viewModel.navigateTo(AppScreen.CART)
                                }
                            )
                        }
                    }
                }
                1 -> {
                    // Saved Address Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Default Delivery Address",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = { isEditProfileOpen = true },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Change", style = MaterialTheme.typography.labelSmall)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = userProfile.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = userProfile.defaultAddress,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Contact: ${userProfile.phone}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Help & Official Policies
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Corporate Overview Card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Orami India Private Limited",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "CIN Registered Entity • New Delhi, India",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            SupportItem(
                                icon = Icons.Default.Call,
                                title = "Customer Helpline & Address",
                                subtitle = "Vill. Mundka Industrial Area, New Delhi • +91 9812644480",
                                badge = "Mon - Sat",
                                onClick = { activePolicyModal = PolicyType.CONTACT_US }
                            )

                            SupportItem(
                                icon = Icons.Default.Replay,
                                title = "Refund and Return Policy",
                                subtitle = "7-Day policy • Mandatory continuous unboxing video requirement",
                                badge = "7-Day Return",
                                onClick = { activePolicyModal = PolicyType.REFUND_RETURN }
                            )

                            SupportItem(
                                icon = Icons.Default.Description,
                                title = "Terms & Conditions",
                                subtitle = "Effective 13/12/2025 • 25 legal clauses, IP, and New Delhi jurisdiction",
                                badge = "25 Clauses",
                                onClick = { activePolicyModal = PolicyType.TERMS_CONDITIONS }
                            )

                            SupportItem(
                                icon = Icons.Default.Security,
                                title = "Privacy Policy",
                                subtitle = "Data security, SSL encryption, cookies & user rights",
                                badge = "Protected",
                                onClick = { activePolicyModal = PolicyType.PRIVACY_POLICY }
                            )

                            SupportItem(
                                icon = Icons.Default.Info,
                                title = "About Orami & Brand Partners",
                                subtitle = "Vision, mission & official brand curation (Cuddle Cotton, Realme, Cello)",
                                badge = "Our Story",
                                onClick = { activePolicyModal = PolicyType.ABOUT_US }
                            )
                        }
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (isEditProfileOpen) {
        var editName by remember { mutableStateOf(userProfile.name) }
        var editPhone by remember { mutableStateOf(userProfile.phone) }
        var editAddress by remember { mutableStateOf(userProfile.defaultAddress) }

        AlertDialog(
            onDismissRequest = { isEditProfileOpen = false },
            title = {
                Text("Edit Profile Details", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAddress,
                        onValueChange = { editAddress = it },
                        label = { Text("Default Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(editName, editPhone, editAddress)
                        isEditProfileOpen = false
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditProfileOpen = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    activePolicyModal?.let { policy ->
        OramiPoliciesModal(
            initialPolicy = policy,
            onDismiss = { activePolicyModal = null }
        )
    }
}

@Composable
fun AuthLoginSignupScreen(
    onLogin: (String, String) -> Boolean,
    onSignup: (String, String, String, String) -> Boolean,
    onOpenPolicy: (PolicyType) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var authMode by remember { mutableIntStateOf(0) } // 0: Sign In, 1: Sign Up

    var emailInput by remember { mutableStateOf("skbitservice@gmail.com") }
    var passwordInput by remember { mutableStateOf("orami@2026") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var signupName by remember { mutableStateOf("SK Bit Service") }
    var signupPhone by remember { mutableStateOf("+91 98765 43210") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .testTag("auth_login_signup_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Brand Logo
            OramiLogo(
                fontSize = 26.sp,
                ringSize = 32.dp,
                dotSize = 9.dp,
                showTagline = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (authMode == 0) "Sign in to your account" else "Create your Orami account",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Track shipments, access exclusive vouchers, and manage your wishlist.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Switch Tabs (Sign In / Sign Up)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (authMode == 0) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { authMode = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (authMode == 0) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (authMode == 1) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { authMode = 1 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (authMode == 1) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        if (authMode == 0) {
            // Sign In Form
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address or Phone") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("auth_password_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = { onLogin(emailInput, passwordInput) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_login_submit_button")
                    ) {
                        Text("Sign In", fontWeight = FontWeight.Bold)
                    }

                    // 1-Tap Google button
                    OutlinedButton(
                        onClick = { onLogin("skbitservice@gmail.com", "google_oauth_token") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Sign in with Google", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        } else {
            // Sign Up Form
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = signupName,
                        onValueChange = { signupName = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("signup_name_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("signup_email_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = signupPhone,
                        onValueChange = { signupPhone = it },
                        label = { Text("Phone Number") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("signup_phone_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Create Password") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("signup_password_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = { onSignup(signupName, emailInput, signupPhone, passwordInput) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_signup_submit_button")
                    ) {
                        Text("Create Account & Join Orami", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "By creating an account, you agree to Orami's",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Terms & Conditions",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onOpenPolicy(PolicyType.TERMS_CONDITIONS) }
                )
                Text(" • ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = "Privacy Policy",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onOpenPolicy(PolicyType.PRIVACY_POLICY) }
                )
                Text(" • ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = "Refund Policy",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onOpenPolicy(PolicyType.REFUND_RETURN) }
                )
            }
        }
    }
}

@Composable
fun OrderHistoryCard(
    order: Order,
    onReorder: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .testTag("order_card_${order.orderId}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.orderId,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = order.orderDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .background(
                            when (order.currentStatus) {
                                OrderStatus.DELIVERED -> OramiEmeraldLight
                                OrderStatus.IN_TRANSIT -> OramiCoral.copy(alpha = 0.12f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = order.currentStatus.title.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = when (order.currentStatus) {
                            OrderStatus.DELIVERED -> OramiEmerald
                            OrderStatus.IN_TRANSIT -> OramiCoral
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Items Summary
            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = item.product.imageUrls.firstOrNull(),
                        contentDescription = item.product.title,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.product.title,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1
                        )
                        Text(
                            text = "Qty: ${item.quantity} • ₹${item.totalPrice.toInt()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(10.dp))

            // Total and Tracking toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Paid: ₹${order.total.toInt()}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onReorder,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Reorder", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    Button(
                        onClick = { isExpanded = !isExpanded },
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            if (isExpanded) "Hide Track" else "Track",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Expandable Shipment Tracker
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "LIVE TRACKING TIMELINE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    order.trackingSteps.forEachIndexed { index, step ->
                        TrackingTimelineRow(
                            step = step,
                            isLast = index == order.trackingSteps.size - 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SupportItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    badge: String? = null,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    if (badge != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
