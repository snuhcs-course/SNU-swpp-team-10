package com.example.calendy.view.editplanview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.calendy.data.category.Category
import com.example.calendy.utils.bottomBorder
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog

@Composable
fun CategorySelector(
    currentCategory: Category?,
    categoryList: List<Category>,
    onSelectCategory: (Category?) -> Unit,
    onAddCategory: (String, Int) -> Unit
) {
    var showCategoryPickerDialog by remember { mutableStateOf(false) }
    fun closeCategoryDialog() {
        showCategoryPickerDialog = false
    }

    // UI Shown in Edit Plan Page as field
    Row(modifier = Modifier.fillMaxWidth()) {
        TextButton(
            onClick = { showCategoryPickerDialog = true },
            modifier = Modifier
                .weight(1f)
                .padding(end = 20.dp)
                .bottomBorder(1.dp, color = Color.Gray)
        ) {
            Text(text = currentCategory?.title ?: "No Category")
        }
        IconButton(onClick = { onSelectCategory(null) }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Deselect Category",
            )
        }
    }

    if (showCategoryPickerDialog) {
        CategoryPickerDialog(
            categoryList = categoryList,
            closeCategoryDialog = { closeCategoryDialog() },
            onSelectCategory = onSelectCategory,
            onAddCategory = onAddCategory,
        )
    }
}

@Composable
private fun CategoryPickerDialog(
    categoryList: List<Category>,
    closeCategoryDialog: () -> Unit,
    onSelectCategory: (Category) -> Unit,
    onAddCategory: (String, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
           onDismissRequest = { closeCategoryDialog() }) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                // TODO: Should use TopAppBar in Scaffold for uniformity
                TopAppBar(showBackButton = true,
                          onBackPressed = { closeCategoryDialog() },
                          title = { Text("Category") },
                          trailingContent = {
                              IconButton(onClick = { showAddDialog = true }) {
                                  Icon(
                                      imageVector = Icons.Default.Add,
                                      contentDescription = "Add Category",
                                  )
                              }
                          })

                // Display Category List
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    this.items(items = categoryList) { category ->
                        TextButton(
                            onClick = {
                                onSelectCategory(category)
                                closeCategoryDialog()
                            }, modifier = Modifier.padding(8.dp)
                        ) {
                            Text(text = category.title)
                        }
                    }
                }
            }

            if (showAddDialog) {
                CategoryAddDialog(
                    closeCategoryDialog = { closeCategoryDialog() },
                    onAddCategory = onAddCategory
                )
            }
        }
    }
}

@Composable
private fun CategoryAddDialog(
    closeCategoryDialog: () -> Unit,
    onAddCategory: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var newCategoryTitle by remember { mutableStateOf("") }
    var newCategoryPriority by remember { mutableStateOf(3) }

    fun resetToDefault() {
        newCategoryTitle = ""
        newCategoryPriority = 3
    }


    BottomSheetDialog(onDismissRequest = {
        closeCategoryDialog()
        resetToDefault()
    }) {
        Card(modifier = Modifier.fillMaxSize()) {
            Column {
                TopAppBar(showBackButton = false, onBackPressed = { }, title = {
                    Text(
                        "New Category", modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }, trailingContent = {
                    IconButton(onClick = {
                        onAddCategory(newCategoryTitle, newCategoryPriority)
                        resetToDefault()
                        closeCategoryDialog()
                    }, modifier = Modifier.padding(horizontal = 8.dp)) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Add Category Confirm"
                        )
                    }
                })
                // TODO: Bottom Sheet 대신 작은 Dialog 로 변경
                TextField(value = newCategoryTitle,
                          onValueChange = { newCategoryTitle = it },
                          placeholder = {
                              Text(text = "Title")
                          })

                RatingBar(
                    value = newCategoryPriority.toFloat(),
                    onValueChange = { newCategoryPriority = it.toInt() },
                    onRatingChanged = { },
                    config = RatingBarConfig().size(40.dp)
                )
            }
        }
    }
}