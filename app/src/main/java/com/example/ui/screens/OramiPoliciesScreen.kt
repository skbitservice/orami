package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.OramiLogo
import com.example.ui.theme.OramiAmber
import com.example.ui.theme.OramiCoral
import com.example.ui.theme.OramiEmerald
import com.example.ui.theme.OramiEmeraldLight

enum class PolicyType(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val badge: String
) {
    TERMS_CONDITIONS(
        title = "Terms & Conditions",
        subtitle = "Effective Date: 13/12/2025 • Official platform guidelines & rights",
        icon = Icons.Default.Description,
        badge = "25 Sections"
    ),
    PRIVACY_POLICY(
        title = "Privacy Policy",
        subtitle = "Data security, cookies, encryption & personal rights",
        icon = Icons.Default.Lock,
        badge = "Updated Dec 2025"
    ),
    REFUND_RETURN(
        title = "Refund & Return Policy",
        subtitle = "7-Day policy, mandatory unboxing video requirement & pickups",
        icon = Icons.Default.Replay,
        badge = "7-Day Return"
    ),
    ABOUT_US(
        title = "About Orami",
        subtitle = "Orami India Private Limited • My Only Choice",
        icon = Icons.Default.Info,
        badge = "Company Story"
    ),
    CONTACT_US(
        title = "Contact Us",
        subtitle = "Mundka Industrial Area, New Delhi • Phone & Email support",
        icon = Icons.Default.Call,
        badge = "Mon - Sat Support"
    )
}

data class PolicySection(
    val number: String,
    val title: String,
    val content: String,
    val highlight: String? = null
)

object OramiPolicyData {
    val termsSections = listOf(
        PolicySection(
            number = "1",
            title = "Introduction",
            content = "These Terms and Conditions govern your use of this website. By using this website, you accept these Terms and Conditions in full. If you disagree with any part of these Terms, you must not use this website."
        ),
        PolicySection(
            number = "2",
            title = "Use of Orami Logo and Presentation",
            content = "The use of the Orami logo, branding, or any other visual representation of Orami without prior written permission from Orami India Private Limited is strictly prohibited."
        ),
        PolicySection(
            number = "3",
            title = "Age Requirement",
            content = "You must be at least 18 years of age to use this website. By using this website and agreeing to these Terms, you warrant and represent that you are at least 18 years old."
        ),
        PolicySection(
            number = "4",
            title = "Cookies",
            content = "This website uses cookies. By using this website and agreeing to these Terms, you consent to Orami's use of cookies in accordance with our Privacy Policy."
        ),
        PolicySection(
            number = "5",
            title = "Goods Use & B2B Policy",
            content = "Goods sold on Orami are intended for direct end-users and are not for resale. No Input Tax Credit (ITC) will be provided for goods sold on Orami. Currently, we do not support B2B sales.",
            highlight = "Direct end-users only • No ITC for B2B"
        ),
        PolicySection(
            number = "6",
            title = "License to Use Website",
            content = "Unless otherwise stated, Orami and/or its licensors own the intellectual property rights in the website and its material. You may view, download for caching purposes, and print pages for your personal use. You must not republish, sell, rent, reproduce, duplicate, edit, or commercially exploit any material without explicit authorization from Orami India Private Limited."
        ),
        PolicySection(
            number = "7",
            title = "Intellectual Property Rights",
            content = "Orami India Private Limited is the sole owner or lawful licensee of all rights to the website and its content, including design, layout, text, images, graphics, and sound. All intellectual property rights remain with the Company. Third-party trademarks belong to their respective owners."
        ),
        PolicySection(
            number = "8",
            title = "Acceptable Use",
            content = "You must not use this website in any manner that causes damage or impairment to its availability. You must not transmit malicious software or conduct systematic data extraction without express written consent. Unsolicited commercial marketing is strictly prohibited."
        ),
        PolicySection(
            number = "9",
            title = "Restricted Access",
            content = "Access to certain areas of this website is restricted. Orami reserves the right to restrict access at its sole discretion and may disable your account credentials without prior notice."
        ),
        PolicySection(
            number = "10",
            title = "User Content",
            content = "Material submitted to this website grants Orami a worldwide, irrevocable, royalty-free license to use, adapt, and distribute that content. Your submissions must not be illegal or infringe third-party rights."
        ),
        PolicySection(
            number = "11",
            title = "No Warranties",
            content = "This website is provided 'as is' without representations or warranties, express or implied. Orami does not warrant that the website will always be available or uninterrupted."
        ),
        PolicySection(
            number = "12",
            title = "Limitations of Liability",
            content = "Orami will not be liable to you in relation to the contents of, or use of, this website for any direct, indirect, special, or consequential loss."
        ),
        PolicySection(
            number = "13",
            title = "Indemnity",
            content = "You hereby indemnify Orami and undertake to keep Orami indemnified against any losses, damages, costs, and expenses incurred by Orami arising out of any breach by you of these Terms and Conditions."
        ),
        PolicySection(
            number = "14",
            title = "Breaches of Terms",
            content = "Without prejudice to other rights, if you breach these Terms, Orami may take appropriate action including suspending access, blocking IP addresses, and instituting legal proceedings."
        ),
        PolicySection(
            number = "15",
            title = "Variation",
            content = "Orami may revise these Terms and Conditions from time to time. Revised Terms will apply to the use of this website from the date of publication."
        ),
        PolicySection(
            number = "16",
            title = "Assignment",
            content = "Orami may transfer, sub-contract, or deal with its rights and obligations under these Terms without notifying you. You may not transfer your rights."
        ),
        PolicySection(
            number = "17",
            title = "Severability",
            content = "If a provision of these Terms is deemed unlawful or unenforceable by a court of competent jurisdiction, the other provisions will continue in effect."
        ),
        PolicySection(
            number = "18",
            title = "Entire Agreement",
            content = "These Terms and Conditions constitute the entire agreement between you and Orami in relation to your use of this website, superseding prior agreements."
        ),
        PolicySection(
            number = "19",
            title = "Governing Law & Jurisdiction",
            content = "These Terms will be governed by and construed in accordance with the laws of India. Any disputes relating to these Terms will be subject to the exclusive jurisdiction of the courts of New Delhi, India.",
            highlight = "Exclusive Jurisdiction: Courts of New Delhi, India"
        ),
        PolicySection(
            number = "20",
            title = "Shipping and Delivery",
            content = "Orami ships products across serviceable PIN codes in India. Enjoy free shipping on orders above ₹399! Standard delivery is generally completed within 3 to 7 business days depending on location.",
            highlight = "Free Shipping on Orders Above ₹399"
        ),
        PolicySection(
            number = "21",
            title = "Pricing and Product Information",
            content = "Prices, offers, and item availability are subject to change without notice. While we strive for absolute accuracy, errors may occasionally occur, and Orami reserves the right to cancel affected orders."
        ),
        PolicySection(
            number = "22",
            title = "User Feedback & Reviews",
            content = "Customer ratings and reviews help maintain transparent commerce. Submissions must reflect genuine purchase experiences and conform to community standards."
        ),
        PolicySection(
            number = "23",
            title = "Termination",
            content = "Orami reserves the right to terminate user access and services immediately upon any violation of policies or fraudulent activity."
        ),
        PolicySection(
            number = "24",
            title = "Corporate Contact Information",
            content = "Orami India Private Limited\nPlot at KH No-62/2/2 & 9, Vill. Mundka Industrial Area, New Delhi – 110041\nCustomer Support: support@orami.in\nPhone: +91 9812644480 / skbitservice@gmail.com",
            highlight = "Vill. Mundka Industrial Area, New Delhi – 110041"
        ),
        PolicySection(
            number = "25",
            title = "Acknowledgment",
            content = "By continuing to browse or purchase on Orami, you acknowledge that you have read, understood, and agreed to be bound by these Terms and Conditions."
        )
    )

    val privacySections = listOf(
        PolicySection(
            number = "1",
            title = "Commitment to Privacy",
            content = "Orami India Private Limited ('Orami', 'we', 'our') is committed to protecting the privacy and confidentiality of our users. This policy outlines how your data is collected, handled, and secured."
        ),
        PolicySection(
            number = "2",
            title = "Information We Collect",
            content = "• Personal Information: Name, email address, mobile number, delivery address, and billing information entered during checkout or account creation.\n• Device & Usage Data: IP address, device model, operating system version, browsing flow, and session duration.\n• Cookies & Session Tokens: Used for maintaining shopping cart sessions and remembering user preferences."
        ),
        PolicySection(
            number = "3",
            title = "How We Use Your Data",
            content = "We use your personal data to fulfill and dispatch orders, send real-time shipment updates, provide responsive customer assistance, protect against fraudulent transactions, and send optional promotional discounts with one-tap opt-out."
        ),
        PolicySection(
            number = "4",
            title = "Information Sharing & Third Parties",
            content = "We do NOT sell, rent, or trade your personal data. We only share essential details with certified logistics partners (for parcel delivery) and RBI-compliant payment gateways (for secure processing).",
            highlight = "Zero Data Selling • Trusted Logistics & Payment Partners Only"
        ),
        PolicySection(
            number = "5",
            title = "Data Security Standards",
            content = "We employ industry-standard encryption protocols (SSL/TLS), firewalled infrastructure, and strict role-based access controls to safeguard your personal data from unauthorized access or disclosure."
        ),
        PolicySection(
            number = "6",
            title = "Your Rights & Control",
            content = "You have the right to inspect, update, or request the deletion of your personal data stored on our servers. You may contact support@orami.in at any time to exercise your data protection rights."
        )
    )

    val returnSections = listOf(
        PolicySection(
            number = "1",
            title = "7-Day Return Policy",
            content = "We offer a 7-day return window starting from the day your package is marked delivered by the courier partner.",
            highlight = "7 Days from Delivery Date"
        ),
        PolicySection(
            number = "2",
            title = "Mandatory Unboxing Video Requirement (CRITICAL)",
            content = "To be eligible for return or replacement due to damage, defect, or incorrect items, you MUST provide an uncut, continuous unboxing video recorded from the moment the sealed parcel is opened, showing:\n1. The outer condition of the shipping package and the clear shipping label.\n2. The continuous process of breaking the seal and opening the package without cuts.\n3. The item being removed and the specific defect or damage clearly visible.\nRequests without a valid unboxing video cannot be approved.",
            highlight = "Mandatory continuous unboxing video without cuts or edits"
        ),
        PolicySection(
            number = "3",
            title = "Return Eligibility Criteria",
            content = "Items must be returned in their original condition: unused, unworn, unwashed, with all original brand tags intact, and in original manufacturer packaging with user manuals and accessories."
        ),
        PolicySection(
            number = "4",
            title = "Non-Returnable Categories",
            content = "For hygiene, safety, and health reasons, certain items cannot be returned:\n• Personal hygiene, cosmetics, and innerwear items once opened.\n• Perishable items or customized products.\n• Items marked 'Final Sale' or 'Clearance'."
        ),
        PolicySection(
            number = "5",
            title = "How to Request a Return",
            content = "1. Email support@orami.in within 7 days of delivery.\n2. Include your Order ID (e.g. ORM-2026-XXXX) and registered phone number.\n3. Attach photos of the item and the continuous unboxing video.\nOur support team will verify within 24-48 business hours and schedule a reverse pickup."
        ),
        PolicySection(
            number = "6",
            title = "Refund Processing Timelines",
            content = "Once the returned parcel reaches our fulfillment hub and passes quality inspection:\n• Prepaid Orders: Refund credited to the original payment source within 5 to 7 business days.\n• Cash on Delivery (COD): Refund transferred via UPI or NEFT bank transfer upon receiving bank details from the customer.",
            highlight = "5-7 Business Days to Original Payment / UPI"
        )
    )
}

@Composable
fun OramiPoliciesModal(
    initialPolicy: PolicyType,
    onDismiss: () -> Unit
) {
    var selectedPolicy by remember { mutableStateOf(initialPolicy) }
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedPolicy.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Orami India Private Limited",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = selectedPolicy.badge,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                // Policy Selector Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PolicyType.entries.forEach { policy ->
                        val isSelected = policy == selectedPolicy
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedPolicy = policy }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = policy.title.split(" ").first(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Policy Body Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (selectedPolicy) {
                        PolicyType.TERMS_CONDITIONS -> {
                            item {
                                PolicyOverviewBanner(
                                    title = "Terms & Conditions",
                                    effectiveDate = "13/12/2025",
                                    subtitle = "Governs all browsing, account creation, and purchases on orami.in. Operated by Orami India Private Limited.",
                                    icon = Icons.Default.Description
                                )
                            }
                            items(OramiPolicyData.termsSections) { section ->
                                PolicySectionCard(section = section)
                            }
                        }
                        PolicyType.PRIVACY_POLICY -> {
                            item {
                                PolicyOverviewBanner(
                                    title = "Privacy Policy",
                                    effectiveDate = "13/12/2025",
                                    subtitle = "How Orami India Private Limited protects your personal information, cookies, and payment details.",
                                    icon = Icons.Default.Lock
                                )
                            }
                            items(OramiPolicyData.privacySections) { section ->
                                PolicySectionCard(section = section)
                            }
                        }
                        PolicyType.REFUND_RETURN -> {
                            item {
                                PolicyOverviewBanner(
                                    title = "Refund and Return Policy",
                                    effectiveDate = "13/12/2025",
                                    subtitle = "Enjoy a 7-day hassle-free return window. Continuous unboxing video without cuts is strictly mandatory.",
                                    icon = Icons.Default.Replay
                                )
                            }
                            items(OramiPolicyData.returnSections) { section ->
                                PolicySectionCard(section = section)
                            }
                        }
                        PolicyType.ABOUT_US -> {
                            item {
                                AboutUsContent(context = context)
                            }
                        }
                        PolicyType.CONTACT_US -> {
                            item {
                                ContactUsContent(context = context)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PolicyOverviewBanner(
    title: String,
    effectiveDate: String,
    subtitle: String,
    icon: ImageVector
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Effective Date: $effectiveDate",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = OramiCoral
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PolicySectionCard(section: PolicySection) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = section.number,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = section.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            if (section.highlight != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(OramiAmber.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = OramiAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = section.highlight,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AboutUsContent(context: Context) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Hero Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OramiLogo(fontSize = 24.sp, ringSize = 30.dp, dotSize = 8.dp, showTagline = true)

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Welcome to Orami",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Operated by Orami India Private Limited",
                    style = MaterialTheme.typography.labelMedium,
                    color = OramiCoral,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "At Orami, we believe that great shopping is an effortless blend of quality, transparency, and personal style. Our vision is to empower Indian consumers with curated fashion, premium tech essentials, travel companions, and home decor that elevate everyday life.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Key Value Props
        Text(
            text = "WHY CHOOSE ORAMI",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AboutPropCard(
                title = "Free Shipping",
                desc = "Orders > ₹399 dispatched express",
                icon = Icons.Default.LocalShipping,
                modifier = Modifier.weight(1f)
            )
            AboutPropCard(
                title = "7-Day Returns",
                desc = "Smooth reverse pickups across India",
                icon = Icons.Default.Replay,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AboutPropCard(
                title = "100% Genuine",
                desc = "Direct brand sourcing guarantee",
                icon = Icons.Default.Security,
                modifier = Modifier.weight(1f)
            )
            AboutPropCard(
                title = "Dedicated Support",
                desc = "Active resolution team in New Delhi",
                icon = Icons.Default.Call,
                modifier = Modifier.weight(1f)
            )
        }

        // Brand Partners Showcase
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "FEATURED BRAND PARTNERS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cuddle Cotton • Spacewalk • Realme • Oppo • Cello • Orami Studio",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun AboutPropCard(
    title: String,
    desc: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ContactUsContent(context: Context) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Corporate Information Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Orami India Private Limited",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "CIN Registered Entity",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(14.dp))

                // Registered Address
                Text(
                    text = "REGISTERED OFFICE & FULFILLMENT CENTER",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Plot at KH No-62/2/2 & 9, Vill. Mundka Industrial Area, New Delhi – 110041, India",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Orami Address", "Plot at KH No-62/2/2 & 9, Vill. Mundka Industrial Area, New Delhi – 110041"))
                        Toast.makeText(context, "Address copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Address", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Direct Channels Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "CUSTOMER HELPLINE & EMAIL",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Phone Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(OramiEmeraldLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = OramiEmerald, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "+91 9812644480", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            Text(text = "Helpline Support", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919812644480"))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            try { context.startActivity(intent) } catch (_: Exception) {}
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Call Now", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

                // Email Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "support@orami.in", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            Text(text = "Alternate: skbitservice@gmail.com", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@orami.in")).apply {
                                putExtra(Intent.EXTRA_SUBJECT, "Customer Inquiry - Orami")
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            try { context.startActivity(intent) } catch (_: Exception) {}
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Email", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

                // Operating Hours
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "Operating Hours", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        Text(
                            text = "Monday – Friday: 10:00 – 20:00\nSaturday: 12:00 – 16:00 (Sunday Closed)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
