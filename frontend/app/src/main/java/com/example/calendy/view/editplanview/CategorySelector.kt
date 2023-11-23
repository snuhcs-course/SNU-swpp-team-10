package com.example.calendy.view.editplanview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.calendy.R
import com.example.calendy.data.maindb.category.Category
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig

@Composable
fun CategorySelector(
    currentCategory: Category?,
    categoryList: List<Category>,
    onSelectCategory: (Category?) -> Unit,
    onAddCategory: (String, Int) -> Unit,
    onUpdateCategory: (String, Int, Category) -> Unit,
    onDeleteCategory: (Category) -> Unit,
) {
    var showCategoryPickerDialog by remember { mutableStateOf(false) }
    fun closeCategoryDialog() {
        showCategoryPickerDialog = false
    }

    // UI Shown in Edit Plan Page as field
    Row(modifier = Modifier.fillMaxWidth()) {
        TextButton(
            onClick = { showCategoryPickerDialog = true }, modifier = Modifier.weight(1f)
//                .padding(end = 20.dp)
//                .bottomBorder(1.dp, color = Color.Gray)
        ) {
            Text(
                text = currentCategory?.title ?: "No Category",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Left
            )
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
            onUpdateCategory = onUpdateCategory,
            onDeleteCategory = onDeleteCategory
        )
    }
}

@Composable
private fun CategoryPickerDialog(
    categoryList: List<Category>,
    closeCategoryDialog: () -> Unit,
    onSelectCategory: (Category) -> Unit,
    onAddCategory: (String, Int) -> Unit,
    onUpdateCategory: (String, Int, Category) -> Unit,
    onDeleteCategory: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
           onDismissRequest = { closeCategoryDialog() }) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                // TODO: Should use TopAppBar in Scaffold for uniformity
                TopAppBar(showBackButton = true,
                          onBackPressed = { closeCategoryDialog() },
                          title = { Text("Category") },
                          trailingContent = {
                              IconButton(onClick = {
                                  showEditDialog = true
                                  selectedCategory = null
                              }) {
                                  Icon(
                                      imageVector = Icons.Default.Add,
                                      contentDescription = "Add Category",
                                  )
                              }
                          })

                // Display Category List
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    this.items(items = categoryList) { category ->
                        OutlinedButton(
                            onClick = {
                                onSelectCategory(category)
                                closeCategoryDialog()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = category.title)
                                    // 감싸는 Box에 clickable 수정자 적용
                                    Box(modifier = Modifier
                                        .clickable {
                                            onSelectCategory(category)
                                            closeCategoryDialog()
                                        }
                                        .fillMaxWidth()) {
                                        RatingBar(value = category.defaultPriority.toFloat(),
                                                  onValueChange = {},
                                                  onRatingChanged = {
                                                      onSelectCategory(category)
                                                      closeCategoryDialog()
                                                  })
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        showEditDialog = true
                                        selectedCategory = category
                                    },
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.edit_plan_button),
                                        contentDescription = "카테고리 수정/삭제"
                                    )
                                }
                            }

                        }
                    }
                }
            }

            if (showEditDialog) {
                CategoryEditDialog(
                    closeCategoryEditDialog = {
                        showEditDialog = false
                        selectedCategory = null
                    },
                    onAddCategory = onAddCategory,
                    onUpdateCategory = onUpdateCategory,
                    onDeleteCategory = onDeleteCategory,
                    category = selectedCategory
                )
            }
        }
    }
}

@Composable
private fun CategoryEditDialog(
    closeCategoryEditDialog: () -> Unit,
    onAddCategory: (String, Int) -> Unit,
    onUpdateCategory: (String, Int, Category) -> Unit,
    onDeleteCategory: (Category) -> Unit,
    modifier: Modifier = Modifier,
    category: Category?
) {
    var isAdd: Boolean = (category == null)
    var newCategoryTitle by remember { mutableStateOf("" )}
    var newCategoryPriority by remember { mutableStateOf(3) }

    if(!isAdd) {
        newCategoryTitle = category?.title ?: ""
        newCategoryPriority = category?.defaultPriority ?: 3
    }

    fun resetToDefault() {
        newCategoryTitle = ""
        newCategoryPriority = 3
    }


    Dialog(onDismissRequest = {
        closeCategoryEditDialog()
        resetToDefault()
    }) {
        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                TextField(value = newCategoryTitle,
                          onValueChange = { newCategoryTitle = it },
                          placeholder = {
                              Text(text = "Title")
                          },
                          modifier = Modifier.padding(bottom = 8.dp)
                )

                RatingBar(
                    value = newCategoryPriority.toFloat(),
                    onValueChange = { newCategoryPriority = it.toInt() },
                    onRatingChanged = { },
                    config = RatingBarConfig().size(40.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                if(isAdd) {
                    IconButton(onClick = {
                        if(newCategoryTitle.isBlank()) {
                            onAddCategory("Untitled", newCategoryPriority)
                        } else {
                            onAddCategory(newCategoryTitle, newCategoryPriority)
                        }
                        resetToDefault()
                        closeCategoryEditDialog()
                    }, modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .align(Alignment.End)) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Edit Category Confirm",
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = {
                            category?.let { onDeleteCategory(it) }
                            resetToDefault()
                            closeCategoryEditDialog()
                        }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "delete Category Confirm",
                            )
                        }
                        IconButton(onClick = {
                            category?.let {
                                if(newCategoryTitle.isBlank()) {
                                    onUpdateCategory("Untitled", newCategoryPriority, it)
                                } else {
                                    onUpdateCategory(newCategoryTitle, newCategoryPriority, it)
                                }
                            }
                            resetToDefault()
                            closeCategoryEditDialog()
                        }, modifier = Modifier
                            .padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Update Category Confirm",
                            )
                        }
                    }

                }

            }
        }
    }
}