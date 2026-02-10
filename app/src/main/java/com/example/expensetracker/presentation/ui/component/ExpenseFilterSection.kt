package com.example.expensetracker.presentation.ui.component

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.expenseCategories
import com.example.expensetracker.data.model.incomeCategories
import androidx.compose.ui.res.stringResource
import com.example.expensetracker.R

@Composable
fun ExpenseFilterSection(
    visible: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            stringResource(R.string.search_hint),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        CustomFilterChip(
                            selected = selectedCategory == null,
                            label = stringResource(R.string.filter_all),
                            onClick = { onCategorySelected(null) }
                        )
                    }
                    items(expenseCategories + incomeCategories) { category ->
                        CustomFilterChip(
                            selected = selectedCategory == category.name,
                            label = "${category.icon} ${category.name}",
                            onClick = {
                                if (selectedCategory == category.name) onCategorySelected(null)
                                else onCategorySelected(category.name)
                            }
                        )
                    }
                }
            }
        }
    }
}