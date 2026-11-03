package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.OramiAmber
import com.example.viewmodel.FilterState
import com.example.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    filterState: FilterState,
    onDismiss: () -> Unit,
    onApply: (FilterState) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var currentMaxPrice by remember(filterState) { mutableFloatStateOf(filterState.maxPrice) }
    var currentMinRating by remember(filterState) { mutableFloatStateOf(filterState.minRating) }
    var currentSort by remember(filterState) { mutableStateOf(filterState.sortOption) }
    var currentInStock by remember(filterState) { mutableStateOf(filterState.inStockOnly) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("filter_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Refine & Sort",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Filters")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sort By Options
            Text(
                text = "SORT BY",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            SortOption.values().forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { currentSort = option }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentSort == option,
                        onClick = { currentSort = option }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Max Price Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MAX BUDGET",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Up to ₹${currentMaxPrice.toInt()}",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Slider(
                value = currentMaxPrice,
                onValueChange = { currentMaxPrice = it },
                valueRange = 800f..6000f,
                steps = 12,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("price_filter_slider")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Minimum Rating Filter
            Text(
                text = "MINIMUM RATING",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(0.0f, 4.0f, 4.5f, 4.8f).forEach { ratingThreshold ->
                    val isSelected = currentMinRating == ratingThreshold
                    FilterChip(
                        selected = isSelected,
                        onClick = { currentMinRating = ratingThreshold },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (ratingThreshold > 0f) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = OramiAmber,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Text("${ratingThreshold}+")
                                } else {
                                    Text("Any")
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // In Stock Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "In Stock Only",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = currentInStock,
                    onCheckedChange = { currentInStock = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        currentMaxPrice = 6000f
                        currentMinRating = 0f
                        currentSort = SortOption.FEATURED
                        currentInStock = false
                        onReset()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Reset All")
                }

                Button(
                    onClick = {
                        onApply(
                            FilterState(
                                maxPrice = currentMaxPrice,
                                minRating = currentMinRating,
                                sortOption = currentSort,
                                inStockOnly = currentInStock
                            )
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("apply_filter_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Show Results")
                }
            }
        }
    }
}
